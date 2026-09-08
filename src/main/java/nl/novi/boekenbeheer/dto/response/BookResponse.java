package nl.novi.boekenbeheer.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response body van een boek")
public record BookResponse(

        @Schema(example = "1")
        Long id,

        @Schema(example = "978-0-7475-3269-9")
        String isbn,

        @Schema(example = "Harry Potter en de Steen der Wijzen")
        String title,

        @Schema(example = "Bloomsbury")
        String publisher,

        @Schema(example = "1997")
        int publicationYear,

        @Schema(example = "Fantasy")
        String category,

        @Schema(example = "Het eerste boek in de Harry Potter serie")
        String description,

        @Schema(example = "null")
        String coverImagePath,

        AuthorResponse author
) {}