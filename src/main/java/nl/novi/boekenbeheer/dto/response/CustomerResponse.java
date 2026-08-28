package nl.novi.boekenbeheer.dto.response;

public record CustomerResponse(
        Long id,
        String keycloakId,
        String firstName,
        String lastName,
        String email,
        String phoneNumber
) {}