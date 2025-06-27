package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.model.Book;

public interface BookRepository extends CrudRepository<Book, Long> {

	boolean existsByTitleAndYear(String title, Integer year);
	
	Iterable<Book> findByYear(Integer year);
	
	@Query("""
		    SELECT b FROM Book b
		    WHERE (:title IS NULL OR :title='' OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')))
		      AND (:year IS NULL OR b.year = :year)
		""")
	public List<Book> findByFilters (@Param("title") String title, @Param("year") Integer year);

}
