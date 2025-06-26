package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.ReviewRepository;

@Service
public class ReviewService {
	@Autowired
	private ReviewRepository reviewRepository;
	
	public void save(Review review) {
		this.reviewRepository.save(review);
	}
	
	public void deleteById(Long id) {
		this.reviewRepository.deleteById(id);
	}
	
	public Review getReviewById(Long id) {
		return this.reviewRepository.findById(id).get();
	}
	
	public boolean existsByUserAndBook(User user, Book book) {
	    return this.reviewRepository.existsByUserAndBook(user, book);
	}
	
	public Review findByUserAndBook(User user, Book book) {
	    return this.reviewRepository.findByUserAndBook(user, book).orElse(null);
	}


}
