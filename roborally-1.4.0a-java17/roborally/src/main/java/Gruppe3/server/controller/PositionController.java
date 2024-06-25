package Gruppe3.server.controller;

import Gruppe3.server.model.Game;
import Gruppe3.server.model.GameState;
import Gruppe3.server.model.Player;
import Gruppe3.server.model.Position;
import Gruppe3.server.repository.PositionRepo;
import Gruppe3.server.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;


/**
 * Controller for managing position data of players in games.
 * Provides endpoints for CRUD operations on positions, allowing retrieval, creation, updating, and deletion
 * of position data, as well as special logging functionality for player positions.
 * @Author Karl, Viktor
 */
@RestController
@RequestMapping("/positions")
public class PositionController {

    private final PositionRepo positionRepository;

    @Autowired
    public PositionController(PositionRepo positionRepository){
        this.positionRepository = positionRepository;
    }

    @GetMapping
    public ResponseEntity<List<Position>> getPositions(){
        List<Position> positionList = positionRepository.findAll();
        return ResponseEntity.ok(positionList);
    }

    @PostMapping
    public ResponseEntity<Position> createPosition(@RequestBody Position position){
        Position savedPosition = positionRepository.save(position);
        return ResponseEntity.ok(savedPosition);
    }

    @GetMapping("/{game_id}/{player_id}")
    public ResponseEntity<Position> getPositionById(@PathVariable("game_id") Long gameId, @PathVariable("player_id") Long playerId){


        Optional<Position> position = positionRepository.findByGameIdAndPlayerId(gameId, playerId);
        return position.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{game_id}/{player_id}")
    public ResponseEntity<Position> updatePosition(@PathVariable("game_id") Long gameId,
                                                     @PathVariable("player_id") Long playerId,
                                                     @RequestBody Position positionDetails) {

        Optional<Position> optionalPosition = positionRepository.findByGameIdAndPlayerId(gameId, playerId);
        if (optionalPosition.isPresent()) {
            Position existingPosition = optionalPosition.get();
            existingPosition.setPositionX(positionDetails.getPositionX());
            existingPosition.setPositionY(positionDetails.getPositionY());
            existingPosition.setHeading(positionDetails.getHeading());

            Position updatedPosition = positionRepository.save(existingPosition);
            return ResponseEntity.ok(updatedPosition);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("/{game_id}/{player_id}")
    public ResponseEntity<Void> deletePosition(@PathVariable("game_id") Long gameId, @PathVariable("player_id") Long playerId) {


        Optional<Position> position = positionRepository.findByGameIdAndPlayerId(gameId, playerId);
        if (position.isPresent()) {
            positionRepository.delete(position.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/log/{game_id}/{player_id}")
    public ResponseEntity<Void> logPlayerPosition(@PathVariable("game_id") Long gameId, @PathVariable("player_id") Long playerId){
        GameService.logPlayerPosition(gameId, playerId);
        return ResponseEntity.ok().build();
    }
}
