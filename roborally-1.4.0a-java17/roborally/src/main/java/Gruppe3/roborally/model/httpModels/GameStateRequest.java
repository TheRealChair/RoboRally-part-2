package Gruppe3.roborally.model.httpModels;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameStateRequest {
    private Long playerId;
    private Long gameId;
    private int register;
    private String card;
}
