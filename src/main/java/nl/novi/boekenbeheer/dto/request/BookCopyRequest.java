package nl.novi.boekenbeheer.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BookCopyRequest(

        @NotBlank(message = "Barcode is required")
        String barcode,

        @NotNull(message = "Book ID is required")
        Long bookId
) {}