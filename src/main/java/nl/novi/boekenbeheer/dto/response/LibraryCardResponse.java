package nl.novi.boekenbeheer.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Response body van een bibliotheekpas")
public record LibraryCardResponse(

        @Schema(example = "1")
        Long id,

        @Schema(example = "LC-001")
        String cardNumber,

        @Schema(example = "2024-01-01")
        LocalDate issueDate,

        @Schema(example = "2026-01-01")
        LocalDate expirationDate,

        @Schema(example = "true")
        boolean active,

        @Schema(example = "1")
        Long customerId,

        @Schema(example = "Jan de Vries")
        String customerName
) {}