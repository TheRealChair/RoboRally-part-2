package Gruppe3.server.model;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(name = "gameState")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_stateId")
    private Long gameStateId;

    @Column(name = "register_id")
    private int registerId;

    @Column(name = "card")
    private String card;

    @Column(name = "player")
    private Long player;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

}
