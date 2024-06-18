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

    @Override
    public String toString() {
        return "GameStateRequest{" +
                "playerId=" + playerId +
                ", gameId=" + gameId +
                ", register=" + register +
                ", card='" + card + '\'' +
                '}';
    }
}
