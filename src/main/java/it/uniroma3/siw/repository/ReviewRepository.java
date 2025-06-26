package it.uniroma3.siw.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;

public interface ReviewRepository extends CrudRepository<Review, Long>{
	boolean existsByUserAndBook(User user, Book book);
	Optional<Review> findByUserAndBook(User user, Book book);
}
