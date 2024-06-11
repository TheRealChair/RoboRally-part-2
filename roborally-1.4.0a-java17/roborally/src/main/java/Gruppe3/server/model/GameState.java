package Gruppe3.server.model;

import Gruppe3.server.model.CompositeKeys.GameId_GamePlayerId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "gameState")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameState {

    @EmbeddedId
    private GameId_GamePlayerId id;

    @Column(name = "register_id")
    private int registerId;

    @Column(name = "card")
    private Long card;

    @MapsId("gamePlayerId")
    @ManyToOne
    @JoinColumn(name = "game_player_id")
    private Player player;

    @MapsId("gameId")
    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;
}
