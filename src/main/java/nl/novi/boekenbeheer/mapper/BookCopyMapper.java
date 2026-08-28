package nl.novi.boekenbeheer.mapper;

import nl.novi.boekenbeheer.dto.request.BookCopyRequest;
import nl.novi.boekenbeheer.dto.response.BookCopyResponse;
import nl.novi.boekenbeheer.entity.Book;
import nl.novi.boekenbeheer.entity.BookCopy;
import nl.novi.boekenbeheer.enums.BookCopyStatus;
import org.springframework.stereotype.Component;

@Component
public class BookCopyMapper {

    public BookCopy toEntity(BookCopyRequest request, Book book) {
        BookCopy bookCopy = new BookCopy();
        bookCopy.setBarcode(request.barcode());
        bookCopy.setStatus(BookCopyStatus.AVAILABLE);
        bookCopy.setBook(book);
        return bookCopy;
    }

    public BookCopyResponse toResponse(BookCopy bookCopy) {
        return new BookCopyResponse(
                bookCopy.getId(),
                bookCopy.getBarcode(),
                bookCopy.getStatus(),
                bookCopy.getBook().getId(),
                bookCopy.getBook().getTitle()
        );
    }
}