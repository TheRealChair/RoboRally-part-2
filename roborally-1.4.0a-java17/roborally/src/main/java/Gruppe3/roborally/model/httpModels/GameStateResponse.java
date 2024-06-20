package Gruppe3.roborally.model.httpModels;

import lombok.*;

import java.time.LocalDateTime;  // Import LocalDateTime

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameStateResponse {
    private int gamePlayerId;
    private GameResponse game;
    private int register;
    private String card;
    private LocalDateTime lastPolled;  // Add this field
}
