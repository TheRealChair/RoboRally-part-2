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

//    @EmbeddedId
//    private GameId_GamePlayerId id;

    @Column(name = "register_id")
    private int registerId;

    @Column(name = "card")
    private Long card;

    @Column(name = "player")
    private Long player;

    @ManyToOne
    @MapsId("gameId")
    @JoinColumn(name = "game_id")
    private Game game;
}
