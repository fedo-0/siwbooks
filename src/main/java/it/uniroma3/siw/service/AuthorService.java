package it.uniroma3.siw.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.repository.AuthorRepository;

@Service
public class AuthorService {
	@Autowired
	private AuthorRepository authorRepository;
	
	public Iterable<Author> getAllAuthors() {
		return this.authorRepository.findAll();
	}

	public Author getAuthorById(Long id) {
		Optional<Author> result = this.authorRepository.findById(id);
		return result.orElse(null);
	}

	public void save(Author author) {
		this.authorRepository.save(author);
	}
	
	public void deleteById(Long id) {
		Author author = this.getAuthorById(id);
	    for (Book b : author.getBooks()) 
	        b.getAuthors().remove(author);
	    author.getBooks().clear();
		this.authorRepository.deleteById(id);
	}
	
	public boolean existsByNameAndSurnameAndDateOfBirth(String name,
			String surname, LocalDate dateOfBirth) {
		return this.authorRepository.existsByNameAndSurnameAndDateOfBirth(name, surname, dateOfBirth);
	}
}
