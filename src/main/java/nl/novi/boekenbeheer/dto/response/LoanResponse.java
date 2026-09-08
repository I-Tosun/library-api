package nl.novi.boekenbeheer.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Response body van een lening")
public record LoanResponse(

        @Schema(example = "1")
        Long id,

        @Schema(example = "2026-09-08")
        LocalDate loanDate,

        @Schema(example = "2026-09-22")
        LocalDate dueDate,

        @Schema(example = "null")
        LocalDate returnDate,

        @Schema(example = "1")
        Long bookCopyId,

        @Schema(example = "Harry Potter en de Steen der Wijzen")
        String bookTitle,

        @Schema(example = "BC-002")
        String bookBarcode,

        @Schema(example = "2")
        Long customerId,

        @Schema(example = "Test Klant")
        String customerName
) {}