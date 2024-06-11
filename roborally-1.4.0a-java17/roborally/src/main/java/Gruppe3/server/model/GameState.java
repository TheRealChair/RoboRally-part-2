package Gruppe3.server.model;
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
public class GameState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_stateId")
    private Long gameStateId;

    private int register;
    private String card;
    private Long gamePlayerId;
    private Long gameId;


}
