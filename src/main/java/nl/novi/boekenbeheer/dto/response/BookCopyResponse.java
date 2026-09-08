package nl.novi.boekenbeheer.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import nl.novi.boekenbeheer.enums.BookCopyStatus;

@Schema(description = "Response body van een exemplaar")
public record BookCopyResponse(

        @Schema(example = "1")
        Long id,

        @Schema(example = "BC-001")
        String barcode,

        @Schema(example = "AVAILABLE")
        BookCopyStatus status,

        @Schema(example = "1")
        Long bookId,

        @Schema(example = "Harry Potter en de Steen der Wijzen")
        String bookTitle
) {}