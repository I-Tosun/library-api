package nl.novi.boekenbeheer.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "Request body voor het aanmaken van een lening")
public record LoanRequest(

        @Schema(example = "1")
        @NotNull(message = "Book copy ID is required")
        Long bookCopyId,

        @Schema(example = "2")
        @NotNull(message = "Customer ID is required")
        Long customerId,

        @Schema(example = "2026-09-08")
        @NotNull(message = "Loan date is required")
        LocalDate loanDate,

        @Schema(example = "2026-09-22")
        @NotNull(message = "Due date is required")
        LocalDate dueDate
) {}