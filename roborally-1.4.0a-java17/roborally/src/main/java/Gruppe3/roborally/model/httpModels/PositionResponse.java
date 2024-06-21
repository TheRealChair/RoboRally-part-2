package Gruppe3.roborally.model.httpModels;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PositionResponse {
    private GameResponse game;
    private Long playerId;
    private int positionX;
    private int positionY;
    private String heading;
}
