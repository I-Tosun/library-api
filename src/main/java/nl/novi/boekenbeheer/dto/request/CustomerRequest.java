package nl.novi.boekenbeheer.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request body voor het aanmaken of updaten van een klant")
public record CustomerRequest(

        @Schema(example = "Jan")
        @NotBlank(message = "First name is required")
        String firstName,

        @Schema(example = "de Vries")
        @NotBlank(message = "Last name is required")
        String lastName,

        @Schema(example = "jan.devries@email.nl")
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @Schema(example = "0612345678")
        @NotBlank(message = "Phone number is required")
        String phoneNumber
) {}