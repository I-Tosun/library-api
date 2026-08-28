package nl.novi.boekenbeheer.mapper;

import nl.novi.boekenbeheer.dto.request.BookRequest;
import nl.novi.boekenbeheer.dto.response.BookResponse;
import nl.novi.boekenbeheer.entity.Author;
import nl.novi.boekenbeheer.entity.Book;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    private final AuthorMapper authorMapper;

    public BookMapper(AuthorMapper authorMapper) {
        this.authorMapper = authorMapper;
    }

    public Book toEntity(BookRequest request, Author author) {
        Book book = new Book();
        book.setIsbn(request.isbn());
        book.setTitle(request.title());
        book.setPublisher(request.publisher());
        book.setPublicationYear(request.publicationYear());
        book.setCategory(request.category());
        book.setDescription(request.description());
        book.setCoverImagePath(request.coverImagePath());
        book.setAuthor(author);
        return book;
    }

    public BookResponse toResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getIsbn(),
                book.getTitle(),
                book.getPublisher(),
                book.getPublicationYear(),
                book.getCategory(),
                book.getDescription(),
                book.getCoverImagePath(),
                authorMapper.toResponse(book.getAuthor())
        );
    }
}