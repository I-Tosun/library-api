package nl.novi.boekenbeheer.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request body voor het aanmaken of updaten van een auteur")
public record AuthorRequest(

        @Schema(example = "J.K.")
        @NotBlank(message = "First name is required")
        String firstName,

        @Schema(example = "Rowling")
        @NotBlank(message = "Last name is required")
        String lastName
) {}