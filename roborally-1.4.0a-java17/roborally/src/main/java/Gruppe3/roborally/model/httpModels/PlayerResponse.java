package Gruppe3.roborally.model.httpModels;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerResponse {
    private Long playerId;
    private GameResponse game;
    private int gamePlayerID;
}