package Gruppe3.server.model;

import Gruppe3.roborally.model.CommandCard;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "registers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Register {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "register_id")
    private Long registerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_type")
    private CardTypes cardType;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;
}
