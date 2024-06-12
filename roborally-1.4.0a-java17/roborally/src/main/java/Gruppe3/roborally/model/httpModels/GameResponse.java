package Gruppe3.roborally.model.httpModels;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GameResponse {
    @JsonProperty("gameId")
    private String gameId;

    @JsonProperty("noOfPlayers")
    private int noOfPlayers; // Assuming noOfPlayers is an integer
    @JsonProperty("boardID")
    private int boardID; // Assuming boardID is an integer

    // Getters and setters
    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public int getNoOfPlayers() {
        return noOfPlayers;
    }

    public void setNoOfPlayers(int noOfPlayers) {
        this.noOfPlayers = noOfPlayers;
    }

    public int getBoardID() {
        return boardID;
    }
}
