package Gruppe3.roborally.model.httpModels;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerRequest {
    private Long playerId;
    private Long gameId;
    private int gamePlayerID;
}