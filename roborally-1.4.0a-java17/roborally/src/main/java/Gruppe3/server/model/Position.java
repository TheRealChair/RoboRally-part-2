package Gruppe3.server.model;

import Gruppe3.server.model.CompositeKeys.GameId_GamePlayerId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "positions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(GameId_GamePlayerId.class)
public class Position {

    @Id
    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    @Id
    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @Column(name = "position_x")
    private int positionX;

    @Column(name = "position_y")
    private int positionY;


    @Column (name = "heading")
    private String heading;

}
