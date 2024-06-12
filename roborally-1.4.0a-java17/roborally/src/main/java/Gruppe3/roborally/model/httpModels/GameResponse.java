package Gruppe3.roborally.model.httpModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameResponse {
    @JsonProperty("gameId")
    private String gameId;

    @JsonProperty("noOfPlayers")
    private int noOfPlayers; // Assuming noOfPlayers is an integer
}
