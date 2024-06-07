package dk.dtu.compute.se.pisd.server.repository;

import dk.dtu.compute.se.pisd.server.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorReposity extends JpaRepository<Author, Long> {

    public Author findAuthorByNameLike(String name);
}
