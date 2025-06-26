package it.uniroma3.siw.repository;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Book;

public interface BookRepository extends CrudRepository<Book, Long> {

	//DA METTERE LE FUNZIONI PER IL SEARCH: ESEMPIO IN RESTAURANT REPO
	
	boolean existsByTitleAndYear(String title, Integer year);
}
