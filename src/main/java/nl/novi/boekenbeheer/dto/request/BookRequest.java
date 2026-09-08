package nl.novi.boekenbeheer.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request body voor het aanmaken of updaten van een boek")
public record BookRequest(

        @Schema(example = "978-0-7475-3269-9")
        @NotBlank(message = "ISBN is required")
        String isbn,

        @Schema(example = "Harry Potter en de Steen der Wijzen")
        @NotBlank(message = "Title is required")
        String title,

        @Schema(example = "Bloomsbury")
        @NotBlank(message = "Publisher is required")
        String publisher,

        @Schema(example = "1997")
        @NotNull(message = "Publication year is required")
        @Min(value = 1000, message = "Publication year must be valid")
        Integer publicationYear,

        @Schema(example = "Fantasy")
        @NotBlank(message = "Category is required")
        String category,

        @Schema(example = "Het eerste boek in de Harry Potter serie")
        String description,

        @Schema(example = "null")
        String coverImagePath,

        @Schema(example = "1")
        @NotNull(message = "Author ID is required")
        Long authorId
) {}