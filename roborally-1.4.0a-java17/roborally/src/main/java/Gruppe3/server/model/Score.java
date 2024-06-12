package Gruppe3.server.model;


import Gruppe3.server.model.CompositeKeys.GameId_GamePlayerId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "scores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(GameId_GamePlayerId.class)
public class Score {

    @Id
    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    @Id
    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @Column (name = "checkpoints")
    private int checkpoints;
}
