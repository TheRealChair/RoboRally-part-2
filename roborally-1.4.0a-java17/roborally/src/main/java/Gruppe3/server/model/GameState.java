package Gruppe3.server.model;

import Gruppe3.server.model.CompositeKeys.GameStateId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "game-states")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(GameStateId.class) // Specify the updated composite key class
public class GameState {

    @Id
    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    @Id
    @Column(name = "game_player_id")
    private int gamePlayerId;

    @Id
    @Column(name = "register")
    private int register; // Now part of the primary key

    private String card;
}