package nl.novi.boekenbeheer.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record LibraryCardRequest(

        @NotBlank(message = "Card number is required")
        String cardNumber,

        @NotNull(message = "Issue date is required")
        LocalDate issueDate,

        @NotNull(message = "Expiration date is required")
        @FutureOrPresent(message = "Expiration date must be today or in the future")
        LocalDate expirationDate,

        @NotNull(message = "Customer ID is required")
        Long customerId
) {}