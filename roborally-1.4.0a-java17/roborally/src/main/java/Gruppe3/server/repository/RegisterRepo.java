package Gruppe3.server.repository;

import Gruppe3.server.model.Register;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegisterRepo extends JpaRepository<Register, Long> {
    Register findByRegisterId(Long registerId);
}