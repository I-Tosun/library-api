package nl.novi.boekenbeheer.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request body voor het aanmaken van een exemplaar")
public record BookCopyRequest(

        @Schema(example = "BC-006")
        @NotBlank(message = "Barcode is required")
        String barcode,

        @Schema(example = "1")
        @NotNull(message = "Book ID is required")
        Long bookId
) {}