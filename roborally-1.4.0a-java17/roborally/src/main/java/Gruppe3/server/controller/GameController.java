package Gruppe3.server.controller;


import Gruppe3.server.model.Author;
import Gruppe3.server.model.Game;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import Gruppe3.server.repository.GameRepo;


import java.util.List;

@RestController
@RequestMapping("/games")
public class GameController {

    private GameRepo gameRepository;

    public GameController(GameRepo gameRepository) {
        this.gameRepository = gameRepository;
    }

    @GetMapping
    @RequestMapping(value = "")
    public ResponseEntity<List<Game>> getGames(){
        List<Game> gameList = gameRepository.findAll();
        return ResponseEntity.ok(gameList);
    }
}
