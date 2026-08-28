package nl.novi.boekenbeheer.mapper;

import nl.novi.boekenbeheer.dto.request.LibraryCardRequest;
import nl.novi.boekenbeheer.dto.response.LibraryCardResponse;
import nl.novi.boekenbeheer.entity.Customer;
import nl.novi.boekenbeheer.entity.LibraryCard;
import org.springframework.stereotype.Component;

@Component
public class LibraryCardMapper {

    public LibraryCard toEntity(LibraryCardRequest request, Customer customer) {
        LibraryCard libraryCard = new LibraryCard();
        libraryCard.setCardNumber(request.cardNumber());
        libraryCard.setIssueDate(request.issueDate());
        libraryCard.setExpirationDate(request.expirationDate());
        libraryCard.setActive(true);
        libraryCard.setCustomer(customer);
        return libraryCard;
    }

    public LibraryCardResponse toResponse(LibraryCard libraryCard) {
        return new LibraryCardResponse(
                libraryCard.getId(),
                libraryCard.getCardNumber(),
                libraryCard.getIssueDate(),
                libraryCard.getExpirationDate(),
                libraryCard.isActive(),
                libraryCard.getCustomer().getId(),
                libraryCard.getCustomer().getFirstName() + " " + libraryCard.getCustomer().getLastName()
        );
    }
}