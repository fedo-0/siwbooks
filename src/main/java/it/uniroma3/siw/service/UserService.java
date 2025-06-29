package it.uniroma3.siw.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	protected UserRepository userRepository;

	@Autowired
	private CredentialsService credentialsService;

	public User getUser(Long id) {
		Optional<User> result = this.userRepository.findById(id);
		return result.orElse(null);
	}

	public User saveUser(User user) {
		return this.userRepository.save(user);
	}

	public User getCurrentUser() {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return this.credentialsService.getCredentials(userDetails.getUsername()).getUser();
	}

	public boolean existsByEmail(String email) {
		return this.userRepository.existsByEmail(email);
	}
	
	public boolean checkPermessiAdmin () {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isAdmin = false;
		// Se utente non loggato
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
			isAdmin = credentials.getRole().equals(Credentials.ADMIN_ROLE);
		}
		return isAdmin;
	}
	
	public Long getId () {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof AnonymousAuthenticationToken) {
			return null;
		}
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
		return credentials.getId();
	}

}