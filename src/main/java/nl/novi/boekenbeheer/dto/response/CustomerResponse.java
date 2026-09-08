package nl.novi.boekenbeheer.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response body van een klant")
public record CustomerResponse(

        @Schema(example = "1")
        Long id,

        @Schema(example = "977d1973-d0dc-45df-bd3c-f9823611acee")
        String keycloakId,

        @Schema(example = "Jan")
        String firstName,

        @Schema(example = "de Vries")
        String lastName,

        @Schema(example = "jan.devries@email.nl")
        String email,

        @Schema(example = "0612345678")
        String phoneNumber
) {}