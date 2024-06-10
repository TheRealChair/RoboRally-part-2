package Gruppe3.server.controller;

import Gruppe3.server.model.Register;
import Gruppe3.server.repository.RegisterRepo;
import Gruppe3.server.model.Player;
import Gruppe3.server.repository.PlayerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/registers")
public class RegisterController {

    private final RegisterRepo registerRepository;
    private final PlayerRepo playerRepository;

    @Autowired
    public RegisterController(RegisterRepo registerRepository, PlayerRepo playerRepository) {
        this.registerRepository = registerRepository;
        this.playerRepository = playerRepository;
    }

    // Get all registers
    @GetMapping
    public ResponseEntity<List<Register>> getRegisters() {
        List<Register> registerList = registerRepository.findAll();
        return ResponseEntity.ok(registerList);
    }

    // Create a new register
    @PostMapping
    public ResponseEntity<Register> createRegister(@RequestBody Register register) {
        Register savedRegister = registerRepository.save(register);
        return ResponseEntity.ok(savedRegister);
    }

    // Get a single register by ID
    @GetMapping("/{id}")
    public ResponseEntity<Register> getRegisterById(@PathVariable Long id) {
        Optional<Register> register = registerRepository.findById(id);
        return register.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update an existing register
    @PutMapping("/{id}")
    public ResponseEntity<Register> updateRegister(@PathVariable Long id, @RequestBody Register updatedRegister) {
        Optional<Register> optionalRegister = registerRepository.findById(id);
        if (optionalRegister.isPresent()) {
            Register existingRegister = optionalRegister.get();
            existingRegister.setCardType(updatedRegister.getCardType());
            existingRegister.setPlayer(updatedRegister.getPlayer()); // Update player
            Register savedRegister = registerRepository.save(existingRegister);
            return ResponseEntity.ok(savedRegister);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete a register
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegister(@PathVariable Long id) {
        Optional<Register> register = registerRepository.findById(id);
        if (register.isPresent()) {
            registerRepository.delete(register.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Assign a player to a register
    @PostMapping("/{registerId}/player/{playerId}")
    public ResponseEntity<Register> assignPlayerToRegister(@PathVariable Long registerId, @PathVariable Long playerId) {
        Optional<Register> optionalRegister = registerRepository.findById(registerId);
        Optional<Player> optionalPlayer = playerRepository.findById(playerId);

        if (optionalRegister.isPresent() && optionalPlayer.isPresent()) {
            Register register = optionalRegister.get();
            register.setPlayer(optionalPlayer.get());
            Register savedRegister = registerRepository.save(register);
            return ResponseEntity.ok(savedRegister);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
