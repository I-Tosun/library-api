package nl.novi.boekenbeheer.dto.response;

import java.time.LocalDate;

public record LibraryCardResponse(
        Long id,
        String cardNumber,
        LocalDate issueDate,
        LocalDate expirationDate,
        boolean active,
        Long customerId,
        String customerName
) {}