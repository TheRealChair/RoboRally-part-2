package Gruppe3.roborally.model.httpModels;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameStateResponse {
    private Long playerId;
    private GameResponse game;
    private int register;
    private String card;
}
