package nl.novi.boekenbeheer.mapper;

import nl.novi.boekenbeheer.dto.request.AuthorRequest;
import nl.novi.boekenbeheer.dto.response.AuthorResponse;
import nl.novi.boekenbeheer.entity.Author;
import org.springframework.stereotype.Component;

@Component
public class AuthorMapper {

    public Author toEntity(AuthorRequest request) {
        Author author = new Author();
        author.setFirstName(request.firstName());
        author.setLastName(request.lastName());
        return author;
    }

    public AuthorResponse toResponse(Author author) {
        return new AuthorResponse(
                author.getId(),
                author.getFirstName(),
                author.getLastName()
        );
    }
}