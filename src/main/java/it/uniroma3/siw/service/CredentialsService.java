package it.uniroma3.siw.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.repository.CredentialsRepository;
import jakarta.transaction.Transactional;

@Service
public class CredentialsService {

	@Autowired
	protected CredentialsRepository credentialsRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Transactional
	public Credentials getCredentials(Long id) {
		Optional<Credentials> result = this.credentialsRepository.findById(id);
		return result.orElse(null);
	}

	@Transactional
	public Credentials getCredentials(String username) {
		Optional<Credentials> result = this.credentialsRepository.findByUsername(username);
		return result.orElse(null);
	}

	@Transactional
	public Credentials saveCredentials(Credentials credentials) {
		credentials.setRole(Credentials.DEFAULT_ROLE);
	    credentials.setPassword(this.passwordEncoder.encode(credentials.getPassword()));
		return this.credentialsRepository.save(credentials);
	}

	public boolean existsByUsername(String username) {
		return this.credentialsRepository.existsByUsername(username);
	}
	
	public void deleteById(Long id) {
		this.credentialsRepository.deleteById(id);
	}

}
