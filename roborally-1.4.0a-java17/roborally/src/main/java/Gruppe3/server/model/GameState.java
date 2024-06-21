package Gruppe3.server.model;

import Gruppe3.server.model.CompositeKeys.GameStateId;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

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
    private int register;

    private String card;

    @Column(name = "times_polled")
    private int timesPolled = 0;
}