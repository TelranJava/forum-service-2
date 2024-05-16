package telran.java52.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@EqualsAndHashCode(of = { "id" })
@NoArgsConstructor
@Document(collection = "posts") // за счет этой аннотации
// при первом подключении к mongo db создастся новая коллекция внутри базы которая прописана внутри application.properties
public class Post {
//	@Id
	String id;
	@Setter
	String title;
	@Setter
	String content;
	@Setter
	String author;
	LocalDateTime dateCreated = LocalDateTime.now();
	Set<String> tags = new HashSet<>(); // на случай если не передадут при создании
	int likes;// = 0
	List<Comment> comments = new ArrayList<>(); // на случай если не передадут при создании

	public Post(String title, String content, String author, Set<String> tags) {
		this.title = title;
		this.content = content;
		this.author = author;
		this.tags = tags;
	}

	public void addLike() {
		likes++;
	}

	public boolean addTag(String tag) {
		return tags.add(tag);
	}

	public boolean removeTag(String tag) {
		return tags.remove(tag);
	}

	public boolean addComment(Comment comment) {
		return comments.add(comment);
	}

	public boolean removeComment(Comment comment) {
		return comments.remove(comment);
	}
}
