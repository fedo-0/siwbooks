package it.uniroma3.siw.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import it.uniroma3.siw.controller.validator.AuthorValidator;
import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.service.AuthorService;
import it.uniroma3.siw.service.ImageService;
import it.uniroma3.siw.service.UserService;
import jakarta.validation.Valid;

@Controller
public class AuthorController {

	public static final String FOLD = "authors";
	@Autowired
	private ImageService imageService;
	@Autowired
	private UserService userService;
	@Autowired
	private AuthorService authorService;
	@Autowired
	private AuthorValidator authorValidator;
	
	@GetMapping("/authors")
	public String mostraAutori(Model model) {
		if (this.userService.checkPermessiAdmin())
			model.addAttribute("isAdmin", true);
		else model.addAttribute("isAdmin", false);
		model.addAttribute("authors", this.authorService.getAllAuthors());
		return "authors.html";
	}
	
	@GetMapping("/author/{id}")
	public String mostraAutore(@PathVariable("id") Long id, Model model) {
		if (this.authorService.getAuthorById(id)==null) return "redirect:/authors";
		model.addAttribute("isAdmin", this.userService.checkPermessiAdmin());
		model.addAttribute("author", this.authorService.getAuthorById(id));
		return "author.html";
	}
	
	@GetMapping("/admin/formNewAuthor")
	public String formNewArtist(Model model) {
		model.addAttribute("author", new Author());
		return "admin/formNewAuthor.html";
	}

	@PostMapping(value = "/admin/creaAuthor", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String salvaAutore(@Valid @ModelAttribute("author") Author author,
			BindingResult bindingResult,
			@RequestParam(value = "image", required = false) MultipartFile image,
			Model model) throws IOException {
		
		this.authorValidator.validate(author, bindingResult);
		
		if (bindingResult.hasErrors()) {
			return "admin/formNewAuthor.html";
		}
		
		if (image != null && !image.isEmpty()) {
	        String savedPath = this.imageService.salvaImmagine(image, FOLD); // salva singola immagine
	        author.setImagePath(savedPath);
	    }
		this.authorService.save(author);
		return "redirect:/author/" + author.getId();
	}
	
	@GetMapping("/admin/modificaAuthor/{id}")
	public String modificaBook(@PathVariable("id") Long id, Model model) {
		if (this.authorService.getAuthorById(id)==null) return "redirect:/authors";
		model.addAttribute("author", this.authorService.getAuthorById(id));
		return "admin/formModificaAuthor.html";
	}
	
	@PostMapping(value = "/admin/modificaAuthor/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String salvaModificaAuthor(
	        @PathVariable("id") Long id,
	        @Valid @ModelAttribute Author updated,
	        BindingResult bindingResult,
	        @RequestParam(name = "deleteImage", required = false) Boolean deleteImage,
	        @RequestParam(name = "image", required = false) MultipartFile image,
	        Model model) throws IOException {

	    Author a = authorService.getAuthorById(id);
	    boolean changes = !a.getName().equals(updated.getName()) || !a.getSurname().equals(updated.getSurname()) || !a.getDateOfBirth().equals(updated.getDateOfBirth());
	    if (changes) authorValidator.validate(updated, bindingResult);

	    if (bindingResult.hasErrors()) {
	        updated.setImagePath(a.getImagePath());
	        updated.setId(a.getId());
	        model.addAttribute("author", updated);
	        return "admin/formModificaAuthor.html";
	    }

	    a.setName(updated.getName());
	    a.setSurname(updated.getSurname());
	    a.setDateOfBirth(updated.getDateOfBirth());
	    a.setDateOfDeath(updated.getDateOfDeath());
	    a.setNationality(updated.getNationality());

	    if (deleteImage != null && deleteImage) {
	        this.imageService.eliminaImmagine(a.getImagePath());
	        a.setImagePath(null);
	    }
	    if (image != null && !image.isEmpty()) {
	        String newImagePath = this.imageService.salvaImmagine(image, FOLD);
	        a.setImagePath(newImagePath);
	    }

	    authorService.save(a);
	    model.addAttribute("author", a);
	    return "redirect:/author/" + a.getId();
	}
	
	@PostMapping("/admin/eliminaAuthor/{id}")
	public String eliminaAuthor (@PathVariable("id") Long id, Model model) throws IOException {
		this.imageService.eliminaImmagine(this.authorService.getAuthorById(id).getImagePath());
		this.authorService.deleteById(id);
		return "redirect:/authors";
	}

	
}
