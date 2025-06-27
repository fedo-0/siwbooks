package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.service.ImageService;
import it.uniroma3.siw.service.ReviewService;
import it.uniroma3.siw.service.UserService;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.controller.validator.BookValidator;
import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.service.AuthorService;
import it.uniroma3.siw.service.BookService;
import it.uniroma3.siw.service.CredentialsService;
import jakarta.validation.Valid;

@Controller
public class BookController {

	public static final String FOLD = "books";
	@Autowired
	private ImageService imageService;
	@Autowired
	private BookService bookService;
	@Autowired
	private AuthorService authorService;
	@Autowired
	private BookValidator bookValidator;
	@Autowired
	private UserService userService;
	@Autowired
	private ReviewService reviewService;
	@Autowired
	private CredentialsService credentialsService;
	
	public List<Author> salvaAutori(List<Long> ids){
		if (ids !=null && !ids.isEmpty()) {
			List<Author> authors = new ArrayList<Author>();
			for (Long id : ids) {
				Author author = this.authorService.getAuthorById(id);
				if (author != null) authors.add(author);
			}
			return authors;
		}
		return new ArrayList<Author>();
	}
	
	public List<Review> otherReviews(List<Review> tutte, Review revByUser) {
		List<Review> total = tutte;
		total.remove(revByUser);
		return total;
	}
	
	@GetMapping("/books")
	public String mostraLibri(Model model) {
		model.addAttribute("isAdmin", this.userService.checkPermessiAdmin());
		model.addAttribute("books", this.bookService.getAllBooks());
		model.addAttribute("bookNumber", this.bookService.getTotalBooks());
		return "books.html";
	}
	
	@GetMapping("/book/{id}")
	public String mostraLibro(@PathVariable("id") Long id, Model model) {
		if (this.bookService.getBookById(id)==null) return "redirect:/books";
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserDetails) {
	        UserDetails userDetails = (UserDetails) auth.getPrincipal();
	        Credentials credentials = this.credentialsService.getCredentials(userDetails.getUsername());
	        User user = credentials.getUser();
	        Review reviewByUser = this.reviewService.findByUserAndBook(
	        		user, this.bookService.getBookById(id));
	        
	        model.addAttribute("reviewByUser", reviewByUser);
	        model.addAttribute("reviews", this.otherReviews(
	        		this.bookService.getBookById(id).getReviews(), reviewByUser));
	    } else {
	    	
	        model.addAttribute("reviewByUser", null);
	        model.addAttribute("reviews", this.bookService.getBookById(id).getReviews());
	    }
	    
		model.addAttribute("isAdmin", this.userService.checkPermessiAdmin());
		model.addAttribute("book", this.bookService.getBookById(id));
		return "book.html";
	}
	
	@GetMapping("/admin/formNewBook")
	public String formNewBook(Model model) {
		model.addAttribute("allAuthors", this.authorService.getAllAuthors());
		model.addAttribute("book", new Book());
		return "admin/formNewBook.html";
	}
	
	@PostMapping(value = "/admin/creaBook", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String salvaBook(@Valid @ModelAttribute("book") Book book,
			BindingResult bindingResult,
			@RequestParam(value = "images", required = false) List<MultipartFile> images,
			@RequestParam(value = "authorIds", required = false) List<Long> authorIds,
			Model model) throws IOException {
		this.bookValidator.validate(book, bindingResult);
		if (bindingResult.hasErrors()) {
			model.addAttribute("book", book);
			model.addAttribute("allAuthors", this.authorService.getAllAuthors());
			return "admin/formNewBook.html";
		}
		book.setAuthors(this.salvaAutori(authorIds));
		List<String> savedPaths = this.imageService.salvaImmagini(images, FOLD);
		book.setImagePaths(savedPaths);
		bookService.save(book); // salva nel DB e genera un id
		return "redirect:/book/" + book.getId();
	}
	
	@GetMapping("/admin/modificaBook/{id}")
	public String modificaBook(@PathVariable("id") Long id, Model model) {
		if (this.bookService.getBookById(id)==null) return "redirect:/books";
		model.addAttribute("allAuthors", this.authorService.getAllAuthors());
		model.addAttribute("book", this.bookService.getBookById(id));
		return "admin/formModificaBook.html";
	}
	
	@PostMapping(value = "/admin/modificaBook/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String salvaModificaBook(@PathVariable("id") Long id, @Valid @ModelAttribute Book updated,
			BindingResult bindingResult,
			@RequestParam(name = "deleteImages", required = false) List<String> deleteImages,
			@RequestParam(name = "images", required = false) List<MultipartFile> images,
			@RequestParam(name = "authorIds", required = false) List<Long> authorIds, Model model)
			throws IOException {
		Book b = this.bookService.getBookById(id);
		boolean changes = !b.getTitle().equals(updated.getTitle()) || !b.getYear().equals(updated.getYear());
		
		if (changes) this.bookValidator.validate(updated, bindingResult);
		
		if (bindingResult.hasErrors()) {
			updated.setImagePaths(b.getImagePaths());
			updated.setAuthors(this.salvaAutori(authorIds));
			updated.setId(b.getId());
			model.addAttribute("allAuthors", this.authorService.getAllAuthors());
			model.addAttribute("book", updated);
			return "admin/formModificaBook.html";
		}
		b.setTitle(updated.getTitle());
		b.setYear(updated.getYear());
		b.setAuthors(this.salvaAutori(authorIds));

		List<String> finalPaths = new ArrayList<>(b.getImagePaths());
		if (deleteImages != null) {
			finalPaths.removeAll(deleteImages);
			this.imageService.eliminaImmagini(deleteImages);
		}

		finalPaths.addAll(this.imageService.salvaImmagini(images, FOLD));

		b.setImagePaths(finalPaths);

		this.bookService.save(b);
		model.addAttribute("book", b);
		return "redirect:/book/" + b.getId();
	}
	
	@PostMapping("/admin/eliminaBook/{id}")
	public String eliminaBook (@PathVariable("id") Long id, Model model) throws IOException {
		this.imageService.eliminaImmagini(this.bookService.getBookById(id).getImagePaths());
		this.bookService.deleteById(id);
		return "redirect:/books";
	}
	
	@GetMapping("/booksByYear/{year}")
	public String mostraLibriPerAnno (@PathVariable("year") Integer year, Model model) {
		if (this.bookService.getBooksByYear(year)==null) return "redirect:/books";
		model.addAttribute("isAdmin", this.userService.checkPermessiAdmin());
		model.addAttribute("books", this.bookService.getBooksByYear(year));
		model.addAttribute("year", year);
		return "books.html";
	}
	
	@GetMapping("/formSearchBook")
	public String filtraLibri(Model model) {
		model.addAttribute("year", null);
		model.addAttribute("title", null);
	    return "formSearchBook.html";
	}
	
	@PostMapping("/cercaBooks")
	public String cercaBooks (@RequestParam(required = false) String title,
			@RequestParam(required = false) String year, Model model) {
		if (title==null && year==null) return "redirect:/books";
		if (title!=null) {
			title = title.trim();
			if (title.isEmpty()) title=null;
		}
		Integer parsed = null;
		if (year != null && !year.trim().isEmpty()) {
			parsed = Integer.parseInt(year.trim());
		}
		System.out.println("\n\nTitle passato alla query: '" + title + "'");
		System.out.println("Year passato alla query: " + parsed);
		model.addAttribute("isAdmin", this.userService.checkPermessiAdmin());
		model.addAttribute("books", this.bookService.findByFilters(title, parsed));
		return "books.html";
	}
	
}
