/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package Gruppe3.roborally.fileaccess;

import Gruppe3.roborally.fileaccess.model.SpaceTemplate;
import Gruppe3.roborally.model.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import Gruppe3.roborally.fileaccess.model.BoardTemplate;
import Gruppe3.roborally.fileaccess.model.PlayerTemplate;
import Gruppe3.roborally.controller.FieldAction;

import java.io.*;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class LoadBoard {

    private static final String BOARDSFOLDER = "boards";
    private static final String DEFAULTBOARD = "defaultboard";
    private static final String JSON_EXT = "json";


    /**
         * Loads a game board from a JSON file specified by the board name.
         * If the board cannot be loaded, it defaults to loading the default board.
         * @param boardname the name of the board to load
         * @return the loaded Board object, or null if the board cannot be loaded
         * @Author: Karl
         */
    public static Board loadBoard(String boardname) {
        if (boardname == null) {
            boardname = DEFAULTBOARD;
        }

        ClassLoader classLoader = LoadBoard.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(BOARDSFOLDER + "/" + boardname + "." + JSON_EXT);
        if (inputStream == null) {
            loadBoard(DEFAULTBOARD);
            return null;
        }

        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>());
        Gson gson = gsonBuilder.create();

        JsonReader reader = null;
        try {
            reader = gson.newJsonReader(new InputStreamReader(inputStream));

            BoardTemplate template = gson.fromJson(reader, BoardTemplate.class);
            Board result = new Board(template.width, template.height);
            result.setPhase(Phase.valueOf(template.currentPhase));

            for (SpaceTemplate spaceTemplate : template.spaces) {
                Space space = result.getSpace(spaceTemplate.x, spaceTemplate.y);
                if (space != null) {
                    space.getActions().addAll(spaceTemplate.actions);

                    // Add walls to the space
                    for (Heading wall : spaceTemplate.walls) {
                        space.getWalls().add(wall);
                    }
                    for (Checkpoint checkpoint : spaceTemplate.checkpoints) {
                        space.getCheckpoints().add(checkpoint);
                    }
                    for (Pits pit : spaceTemplate.pits){
                        space.getPits().add(pit);
                    }
                    for (Reboots reboot : spaceTemplate.reboot){
                        space.getReboots().add(reboot);
                    }
                }
            }

            for (PlayerTemplate playerTemplate : template.players) {
                LoadPlayers.loadPlayer(result, playerTemplate);
            }
            result.setStep(template.currentStep);
            result.setCurrentPlayer(result.getPlayer(template.currentPlayer));


            return result;
        } catch (Exception e) {
            e.printStackTrace(); // Print the stack trace for debugging purposes
        } finally {
            // Close the reader and input stream in the finally block
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace(); // Print the stack trace for debugging purposes
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace(); // Print the stack trace for debugging purposes
                }
            }
        }

        return null;
    }


    /**
         * Saves the current state of a board to a JSON file specified by the name.
         * Utilizes Gson for serializing the board and its components to a pretty-printed JSON format.
         * @param board the board to save
         * @param name the filename for saving the board
         * @Author: Karl
         */
    public static void saveBoard(Board board, String name) {
        BoardTemplate template = new BoardTemplate();
        template.width = board.width;
        template.height = board.height;
        template.currentPhase = board.getPhase().toString();
        template.currentStep = board.getStep();
        template.currentPlayer = board.getPlayerNumber(board.getCurrentPlayer());

        for (int i=0; i<board.width; i++) {
            for (int j=0; j<board.height; j++) {
                Space space = board.getSpace(i,j);
                if (!space.getWalls().isEmpty() || !space.getActions().isEmpty()
                                                || !space.getCheckpoints().isEmpty()
                                                || !space.getPits().isEmpty()
                                                || !space.getReboots().isEmpty()) {
                    SpaceTemplate spaceTemplate = new SpaceTemplate();
                    spaceTemplate.x = space.x;
                    spaceTemplate.y = space.y;
                    spaceTemplate.actions.addAll(space.getActions());
                    spaceTemplate.walls.addAll(space.getWalls());
                    spaceTemplate.checkpoints.addAll(space.getCheckpoints());
                    spaceTemplate.pits.addAll(space.getPits());
                    spaceTemplate.reboot.addAll(space.getReboots());
                    template.spaces.add(spaceTemplate);

                    }

                }

            }

        for (Player player : board.getPlayers()) {
            template.players.add(new PlayerTemplate(player));
        }


        ClassLoader classLoader = LoadBoard.class.getClassLoader();
        // TODO: this is not very defensive, and will result in a NullPointerException
        //       when the folder "resources" does not exist! But, it does not need
        //       the file "simpleCards.json" to exist!
        String filename =
                classLoader.getResource(BOARDSFOLDER).getPath() + "/" + name + "." + JSON_EXT;

        // In simple cases, we can create a Gson object with new:
        //
        //   Gson gson = new Gson();
        //
        // But, if you need to configure it, it is better to create it from
        // a builder (here, we want to configure the JSON serialisation with
        // a pretty printer):
        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>()).
                setPrettyPrinting();
        Gson gson = simpleBuilder.create();

        FileWriter fileWriter = null;
        JsonWriter writer = null;
        try {
            fileWriter = new FileWriter(filename);
            writer = gson.newJsonWriter(fileWriter);
            gson.toJson(template, template.getClass(), writer);
            writer.close();
        } catch (IOException e1) {
            if (writer != null) {
                try {
                    writer.close();
                    fileWriter = null;
                } catch (IOException e2) {}
            }
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e2) {}
            }
        }
    }

}
