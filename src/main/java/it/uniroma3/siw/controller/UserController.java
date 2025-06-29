package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UserService;
import jakarta.validation.Valid;

@Controller
public class UserController {

	@Autowired
	UserService userService;

	@Autowired
	CredentialsService credentialsService;

	@GetMapping("/profiloUser/{id}")
	public String mostraProfiloUser(@PathVariable("id") Long userId, Model model) {
		User user = this.userService.getUser(userId);
		if (user == null) return "redirect:/";
		
		Credentials credentials = this.credentialsService.getCredentials(userId);
		
		Long actualUserId = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			Credentials currentUserCredentials = credentialsService.getCredentials(userDetails.getUsername());
			if (currentUserCredentials != null) {
				actualUserId = currentUserCredentials.getUser().getId();
			}
		}

		boolean isOwnProfile = (actualUserId != null && actualUserId.equals(userId) && this.userService.checkPermessiAdmin());

		model.addAttribute("admin", this.userService.checkPermessiAdmin());
		model.addAttribute("user", user);
		model.addAttribute("isOwnProfile", isOwnProfile);
		model.addAttribute("credentials", credentials);

		return "user/profiloUser.html";
	}

	/*
	@GetMapping("/profiloUser/{id}/modificaProfilo")
	public String modificaProfilo(@PathVariable("id") Long userId, Model model) {
		Long currentUserId = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
			currentUserId = this.userService.getCurrentUser().getId();
		}
		User user = this.userService.getUser(userId);
		if (user == null) {
			model.addAttribute("user", user);
			return "profiloUser";
		}
		if (currentUserId == null || !(currentUserId.equals(user.getId())))
			return "formLogin";
		model.addAttribute("user", user);
		return "modificaProfilo.html";
	}

	@PostMapping("/salvaInformazioniUtente/{id}")
	public String salvaInformazioniUtente(@PathVariable("id") Long id, @Valid @ModelAttribute User updated,
			BindingResult bindingResult, Model model) {
		User u = this.userService.getUser(id);
		if (!u.getEmail().equals(updated.getEmail()) && userService.existsByEmail(u.getEmail())) {
			bindingResult.rejectValue("email", "duplicate.email", "Email già registrata");
			model.addAttribute("user", updated);
			return "modificaProfilo";
		}
		if (bindingResult.hasErrors()) {
			model.addAttribute("user", updated);
			return "modificaProfilo";
		}
		u.setEmail(updated.getEmail());
		u.setName(updated.getName());
		u.setSurname(updated.getSurname());
		this.userService.saveUser(u);
		return "redirect:/profiloUser/" + u.getId();
	}

	@GetMapping("/profiloUser/{id}/modificaPassword")
	public String modificaPassword(@PathVariable("id") Long userId, Model model) {
		Long currentUserId = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
			currentUserId = this.userService.getCurrentUser().getId();
		}
		User user = this.userService.getUser(userId);
		if (user == null) {
			model.addAttribute("user", user);
			return "profiloUser";
		}
		if (currentUserId == null || !(currentUserId.equals(user.getId())))
			return "formLogin";
		model.addAttribute("credentials", this.credentialsService.getCredentials(userId));
		return "modificaPassword.html";
	}

	@PostMapping("/salvaPassword/{id}")
	public String salvaPassword(@PathVariable("id") Long id, @Valid @ModelAttribute Credentials updated,
			BindingResult bindingResult, @RequestParam String currentPassword, @RequestParam String confirmPassword,
			Model model) {

		Credentials c = this.credentialsService.getCredentials(id);

		if (currentPassword == null || currentPassword.trim().isEmpty()) {
			model.addAttribute("currentPasswordError", "Campo obbligatorio");

		} else if (!passwordEncoder.matches(currentPassword, c.getPassword())) {
			model.addAttribute("currentPasswordError", "Password attuale non corretta");
		}

		if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
			model.addAttribute("confirmPasswordError", "Campo obbligatorio");
		} else if (!updated.getPassword().equals(confirmPassword)) {
			model.addAttribute("confirmPasswordError", "Le password non coincidono");
		}

		if (model.containsAttribute("currentPasswordError") || model.containsAttribute("confirmPasswordError")) {
			model.addAttribute("credentials", updated);
			return "modificaPassword";
		}
		if (bindingResult.hasErrors()) {
			model.addAttribute("credentials", updated);
			return "modificaPassword";
		}

		updated.setId(c.getId());
		updated.setRole(c.getRole());
		updated.setUser(c.getUser());
		this.credentialsService.saveCredentials(updated);
		return "redirect:/profiloUser/" + c.getId();
	}
*/
}
