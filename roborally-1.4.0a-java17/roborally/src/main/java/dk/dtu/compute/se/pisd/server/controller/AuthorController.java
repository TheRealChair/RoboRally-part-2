package dk.dtu.compute.se.pisd.server.controller;

import dk.dtu.compute.se.pisd.server.model.Author;
import dk.dtu.compute.se.pisd.server.repository.AuthorReposity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
//Base endpoint
@RequestMapping("/authors")
public class AuthorController {

    private AuthorReposity authorReposity;

    public AuthorController(AuthorReposity authorReposity) {
        this.authorReposity = authorReposity;
    }

    @GetMapping
    //Specific endpoint for the method
    @RequestMapping(value = "")
    public ResponseEntity<List<Author>> getAuthors(){
        List<Author> authorList = authorReposity.findAll();
        return ResponseEntity.ok(authorList);
    }


}
