package it.uniroma3.siw.model;

import java.util.Objects;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
// vincolo per garantire una sola recensione per utente-libro
@Table(
	    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "book_id"})
)
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@NotBlank(message = "{NotBlank}")
	private String title;
	@NotBlank(message = "{NotBlank}")
	@Size(max = 255, message ="{Maxtext}")
	private String text;

	@NotNull(message = "{NotNull}")
	@Min(1)
	@Max(value=10, message = "{Maxrating}")
	private Double rating;

	@ManyToOne
	@JoinColumn(name = "book_id", nullable = false)
	private Book book;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	
	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Double getRating() {
		return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}

	public Book getRestaurant() {
		return book;
	}

	public void setRestaurant(Book book) {
		this.book = book;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public int hashCode() {
		return Objects.hash(book, rating, text, title);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Review other = (Review) obj;
		return Objects.equals(book, other.book) && Objects.equals(rating, other.rating)
				&& Objects.equals(text, other.text) && Objects.equals(title, other.title);
	}

	

}