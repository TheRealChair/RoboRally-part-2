package Gruppe3.roborally.model.httpModels;

import lombok.Data;

@Data
public class PlayerRequest {
    private String playerName;
    private int score;
    private Long gameId;
}