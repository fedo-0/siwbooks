package it.uniroma3.siw.repository;

import java.time.LocalDate;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Author;

public interface AuthorRepository extends CrudRepository<Author, Long>{

	boolean existsByNameAndSurnameAndDateOfBirth(String name, String surname, LocalDate dateOfBirth);
}
