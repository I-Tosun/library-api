package nl.novi.boekenbeheer.dto.response;

public record BookResponse(
        Long id,
        String isbn,
        String title,
        String publisher,
        int publicationYear,
        String category,
        String description,
        String coverImagePath,
        AuthorResponse author
) {}