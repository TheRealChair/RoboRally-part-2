package Gruppe3.server.repository;

import Gruppe3.server.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorReposity extends JpaRepository<Author, Long> {

    public Author findAuthorByNameLike(String name);
}
