package nl.novi.boekenbeheer.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record LoanRequest(

        @NotNull(message = "Book copy ID is required")
        Long bookCopyId,

        @NotNull(message = "Customer ID is required")
        Long customerId,

        @NotNull(message = "Loan date is required")
        LocalDate loanDate,

        @NotNull(message = "Due date is required")
        LocalDate dueDate
) {}