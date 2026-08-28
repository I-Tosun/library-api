package nl.novi.boekenbeheer.dto.response;

import nl.novi.boekenbeheer.enums.BookCopyStatus;

public record BookCopyResponse(
        Long id,
        String barcode,
        BookCopyStatus status,
        Long bookId,
        String bookTitle
) {}