package nl.novi.boekenbeheer.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BookRequest(

        @NotBlank(message = "ISBN is required")
        String isbn,

        @NotBlank(message = "Title is required")
        String title,

        @NotBlank(message = "Publisher is required")
        String publisher,

        @NotNull(message = "Publication year is required")
        @Min(value = 1000, message = "Publication year must be valid")
        Integer publicationYear,

        @NotBlank(message = "Category is required")
        String category,

        String description,

        String coverImagePath,

        @NotNull(message = "Author ID is required")
        Long authorId
) {}