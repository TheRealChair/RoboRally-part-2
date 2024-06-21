package Gruppe3.server.model.CompositeKeys;// Composite key class must also be updated to include the new field
import java.io.Serializable;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameStateId implements Serializable {
    private Long game;  // Change from int to Long to match Game ID type
    private int gamePlayerId;
    private int register;
}