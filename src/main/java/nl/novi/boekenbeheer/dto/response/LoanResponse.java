package nl.novi.boekenbeheer.dto.response;

import java.time.LocalDate;

public record LoanResponse(
        Long id,
        LocalDate loanDate,
        LocalDate dueDate,
        LocalDate returnDate,
        Long bookCopyId,
        String bookTitle,
        String bookBarcode,
        Long customerId,
        String customerName
) {}