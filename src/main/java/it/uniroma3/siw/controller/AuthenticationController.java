package it.uniroma3.siw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.model.*;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UserService;
import jakarta.validation.Valid;

@Controller
public class AuthenticationController {

	@Autowired
	private CredentialsService credentialsService;
	@Autowired
	private UserService userService;
	 

	@GetMapping(value = "/register")
	public String showRegisterForm(Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("credentials", new Credentials());
		return "formRegisterUser.html";
	}

	@GetMapping(value = "/login")
	public String showLoginForm(Model model) {
		return "formLogin.html";
	}

	@GetMapping(value = "/")
	public String index(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		// Se utente non loggato
		if (authentication instanceof AnonymousAuthenticationToken) {
			return "index.html";
		}

		// Se loggato
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());

		if (credentials.getRole().equals(Credentials.ADMIN_ROLE)) {
			return "admin/indexAdmin.html";
		}
		model.addAttribute("id", credentials.getId());

		return "index.html";
	}

	@GetMapping(value = "/success")
	public String defaultAfterLogin(Model model) {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
		if (credentials.getRole().equals(Credentials.ADMIN_ROLE)) {
			return "admin/indexAdmin.html";
		}
		model.addAttribute("id", credentials.getId());
		return "index.html";
	}

	@PostMapping(value = { "/register" })
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult userBindingResult,
			@Valid @ModelAttribute("credentials") Credentials credentials, BindingResult credentialsBindingResult,
			@RequestParam String confirmPassword, Model model) {

		// Verifica se esiste già un user con questa email
		if (userService.existsByEmail(user.getEmail())) {
			userBindingResult.rejectValue("email", "Duplicate.email", "Email già registrata");
		}

		// Verifica se esiste già un username
		if (credentialsService.existsByUsername(credentials.getUsername())) {
			credentialsBindingResult.rejectValue("username", "Duplicate.username", "Username già esistente");
		}

		if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
			model.addAttribute("confirmPasswordError", "Campo obbligatorio");
			return "formRegisterUser";
		} else if (!credentials.getPassword().equals(confirmPassword)) {
			model.addAttribute("confirmPasswordError", "Le password non coincidono");
			return "formRegisterUser";
		}

		// se user e credential hanno entrambi contenuti validi, memorizza User e the
		// Credentials nel DB
		if (!userBindingResult.hasErrors() && !credentialsBindingResult.hasErrors()) {
			userService.saveUser(user);
			credentials.setUser(user);
			credentialsService.saveCredentials(credentials);
			model.addAttribute("user", user);
			return "registrationSuccessful.html";
		}
		return "formRegisterUser";
	}
	
	@GetMapping("/accessoNegato")
	public String accessoNegato() {
	    return "accessoNegato.html";
	}

}
