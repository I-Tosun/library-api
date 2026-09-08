package nl.novi.boekenbeheer.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response body van een auteur")
public record AuthorResponse(

        @Schema(example = "1")
        Long id,

        @Schema(example = "J.K.")
        String firstName,

        @Schema(example = "Rowling")
        String lastName
) {}