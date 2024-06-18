package Gruppe3.server.model;
import Gruppe3.server.model.CompositeKeys.GameId_GamePlayerId;
import Gruppe3.server.model.CompositeKeys.GameStateId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "game-states")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(GameStateId.class) // Specify the composite key class
public class GameState {

    @Id
    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    @Id
    @Column(name = "game_player_id")
    private int gamePlayerID; // Changed to int

    private int register;
    private String card;
}

