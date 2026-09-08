package nl.novi.boekenbeheer.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "Request body voor het aanmaken van een bibliotheekpas")
public record LibraryCardRequest(

        @Schema(example = "LC-003")
        @NotBlank(message = "Card number is required")
        String cardNumber,

        @Schema(example = "2026-01-01")
        @NotNull(message = "Issue date is required")
        LocalDate issueDate,

        @Schema(example = "2028-01-01")
        @NotNull(message = "Expiration date is required")
        @FutureOrPresent(message = "Expiration date must be today or in the future")
        LocalDate expirationDate,

        @Schema(example = "1")
        @NotNull(message = "Customer ID is required")
        Long customerId
) {}