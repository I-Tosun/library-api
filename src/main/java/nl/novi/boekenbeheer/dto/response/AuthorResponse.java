package nl.novi.boekenbeheer.dto.response;

public record AuthorResponse(
        Long id,
        String firstName,
        String lastName
) {}