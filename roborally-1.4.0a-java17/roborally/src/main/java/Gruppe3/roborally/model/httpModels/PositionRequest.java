package Gruppe3.roborally.model.httpModels;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PositionRequest {
    private GameResponse game;
    private Long playerId;
    private int positionX;
    private int positionY;
    private String heading;
}
