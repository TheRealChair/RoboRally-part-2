package Gruppe3.server.model;

import Gruppe3.server.model.CompositeKeys.RegisterPlayerId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "registers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Register {

    @EmbeddedId
    private RegisterPlayerId id;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_type")
    private CardTypes cardType;

    @MapsId("playerId")
    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;
}
