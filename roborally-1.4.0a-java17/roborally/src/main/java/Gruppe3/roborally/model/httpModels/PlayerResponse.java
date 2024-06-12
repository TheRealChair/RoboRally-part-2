package Gruppe3.roborally.model.httpModels;

import lombok.Data;

@Data
public class PlayerResponse {
    private Long playerId;
    private String playerName;
    private int score;
    private Long gameId;
}