package it.uniroma3.siw.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.repository.BookRepository;

@Service
public class BookService {
	@Autowired
	private BookRepository bookRepository;
	
	public Iterable<Book> getAllBooks() {
		return this.bookRepository.findAll();
	}

	public Book getBookById(Long id) {
		Optional<Book> result = this.bookRepository.findById(id);
		return result.orElse(null);
	}

	public void save(Book book) {
		this.bookRepository.save(book);
	}

	public void deleteById(Long id) {
		Book book = this.getBookById(id);
	    for (Author a : book.getAuthors())
	        a.getBooks().remove(book);
	    book.getAuthors().clear();
		this.bookRepository.deleteById(id);
	}

	public long getTotalBooks() {
		return this.bookRepository.count();
	}
	
	/**
	 * Ricerca i libri applicando i filtri opzionali definiti nel repository.
	 * 
	 * @param name il nome del libro
	 * @return lista di libri che corrispondono ai filtri
	 */
	public List<Book> findByFilters(String title, Integer year) {
		return this.bookRepository.findByFilters(title, year);
	}


	public boolean existsByTitleAndYear(String title, Integer year) {
		return this.bookRepository.existsByTitleAndYear(title, year);
	}
	
	public Iterable<Book> getBooksByYear(Integer year) {
		return this.bookRepository.findByYear(year);
	}
}
