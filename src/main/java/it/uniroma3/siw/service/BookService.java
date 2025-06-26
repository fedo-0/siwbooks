package it.uniroma3.siw.service;

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
		return bookRepository.count();
	}

//**** FUNZIONI PER IL SEARCH ****//
	
	/**
	 * Ricerca gli annunci applicando i filtri opzionali definiti nel repository.
	 * 
	 * @param citta la città dell'annuncio
	 * @return lista di annunci che corrispondono ai filtri
	 */
//	public List<Book> findByFilters(String city, String type) {
//		return bookRepository.findByFilters(city, type);
//	}

	/**
	 * Verifica se esiste già un annuncio con i dati specificati.
	 * 
	 * @param citta     la città
	 * @return true se esiste un annuncio con questi dati, false altrimenti
	 */
//	public boolean existsByCity(String city, String type) {
//		return bookRepository.existsByCityAndType(city, type);
//	}

	public boolean existsByTitleAndYear(String title, Integer year) {
		return this.bookRepository.existsByTitleAndYear(title, year);
	}
}
