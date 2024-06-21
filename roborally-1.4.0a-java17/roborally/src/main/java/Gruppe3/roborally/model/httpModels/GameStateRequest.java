package Gruppe3.roborally.model.httpModels;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameStateRequest {
    private int gamePlayerId;
    private Long gameId;
    private int register;
    private String card;

    @Override
    public String toString() {
        return "GameStateRequest{" +
                ", register=" + register +
                ", card='" + card + '\'' +
                '}';
    }
}
