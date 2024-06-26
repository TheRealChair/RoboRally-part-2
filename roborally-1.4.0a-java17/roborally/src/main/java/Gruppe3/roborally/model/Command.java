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
package Gruppe3.roborally.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public enum Command {

    // This is a very simplistic way of realizing different commands.
    FORWARD("Move 1"),
    RIGHT("Turn Right"),
    LEFT("Turn Left"),
    FAST_FORWARD("Move 2"),
    SUPER_FAST_FORWARD("Move 3"),
    U_TURN("U-Turn"),
    BACK_UP("Back Up"),
    OPTION_LEFT_RIGHT("Left OR Right", LEFT, RIGHT),
    AGAIN("Again");

    final public String displayName;

    final private List<Command> options;

    Command(String displayName, Command... options) {
        this.displayName = displayName;
        this.options = Collections.unmodifiableList(Arrays.asList(options));
    }

    public boolean isInteractive() {
        return !options.isEmpty();
    }

    public List<Command> getOptions() {
        return options;
    }

    public static Command fromDisplayName(String displayName) {
        for (Command command : Command.values()) {
            if (command.displayName.equals(displayName)) {
                return command;
            }
        }
        throw new IllegalArgumentException("No enum constant with display name " + displayName);
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static Command toCommand(String displayName) {
        if(displayName!=null) {
            return Command.fromDisplayName(displayName);
        }
        return null;
    }
}
