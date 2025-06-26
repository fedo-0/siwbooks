package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.controller.validator.ReviewValidator;
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.BookService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.ReviewService;
import it.uniroma3.siw.service.UserService;
import jakarta.validation.Valid;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Controller
public class ReviewController {

	@Autowired
	private UserService userService;

	@Autowired
	private CredentialsService credentialsService;

	@Autowired
	private ReviewService reviewService;

	@Autowired
	private BookService bookService;
	
	@Autowired
	private ReviewValidator reviewValidator;
	
	@GetMapping("/user/formNewReview/{id}")
	public String formNewReview(@PathVariable("id") Long id, Model model) {
		if (this.bookService.getBookById(id)==null) return "redirect:/books";
		model.addAttribute("book", this.bookService.getBookById(id));
		model.addAttribute("review", new Review());
		return "user/formNewReview.html";
	}
	
	@PostMapping("/user/creaRecensione/{bookid}")
	public String salvaRecensione(@PathVariable("bookid") Long bookId, @ModelAttribute("review") @Valid Review review, 
	                           BindingResult bindingResult, Model model,
	                           @AuthenticationPrincipal UserDetails currentUser) {
	    Book book = bookService.getBookById(bookId);
	    Credentials credentials = credentialsService.getCredentials(currentUser.getUsername());
	    User user = credentials.getUser();
	    
	    this.reviewValidator.validate(review, bindingResult);
	    
	    if (bindingResult.hasErrors()) {
	    	model.addAttribute("book", this.bookService.getBookById(bookId));
			model.addAttribute("review", review);
	        return "user/formNewReview.html";
	    }
	    
	    review.setUser(user);
	    review.setBook(book);
	    this.reviewService.save(review);
	   /* 
	    if (book.getReviews() == null) {
			book.setReviews(new LinkedList<>());
		}

		if (user.getReviews() == null) {
			user.setReviews(new LinkedList<>());
		}

		user.getReviews().add(review);
		book.getReviews().add(review);

		this.bookService.save(book);
		this.userService.saveUser(user);
	    */
	    return "redirect:/book/" + bookId;
	}
	
	@GetMapping("/user/formModificaReview/{bookId}")
	public String modificaReview (@PathVariable("bookId") Long bookId, Model model) {
		if (this.bookService.getBookById(bookId)==null) return "redirect:/books";
		
		// qui non si sollevano eccezioni perche se utente non loggato allora va al login grazie al sec. filter chain
		User user = this.userService.getCurrentUser();
	    Review existingReview = this.reviewService.findByUserAndBook(user, this.bookService.getBookById(bookId));

	    if (existingReview == null) {
	        // l'utente non ha ancora scritto una recensione => redirect al form di creazione
	        return "redirect:/user/formNewReview/" + bookId;
	    }

	    model.addAttribute("book", this.bookService.getBookById(bookId));
	    model.addAttribute("review", existingReview);
	    return "user/formModificaReview.html";
	}
	
	@PostMapping("/user/modificaReview/{bookId}")
	public String salvaModificaReview(@PathVariable("bookId") Long bookId,
	                                 @Valid @ModelAttribute("review") Review updated,
	                                 BindingResult bindingResult,
	                                 Model model) {
		
		Book book = this.bookService.getBookById(bookId);
		User user = this.userService.getCurrentUser();
	    Review ex = reviewService.findByUserAndBook(user, book);
	    if (ex == null) return "redirect:/book/" + bookId;
	    if (book == null) return "redirect:/books";
	    
		boolean changes = !ex.getTitle().equals(updated.getTitle()) && !ex.getText().equals(updated.getText()) && ex.getRating().equals(updated.getRating());
		if (changes)
			this.reviewValidator.validate(updated, bindingResult);

	    if (bindingResult.hasErrors()) {
	        model.addAttribute("book", book);
	        model.addAttribute("review", updated);
	        return "user/formModificaReview.html";
	    }

	    ex.setTitle(updated.getTitle());
	    ex.setText(updated.getText());
	    ex.setRating(updated.getRating());

	    reviewService.save(ex);

	    return "redirect:/book/" + bookId;
	}
	
	@PostMapping("/eliminaReview/{id}")
	public String eliminaReview (@PathVariable("id") Long id, Model model) {
		Review review = this.reviewService.getReviewById(id);
		Book book = review.getBook();
		
		this.reviewService.deleteById(id);
		return "redirect:/book/" + book.getId();
	}

}