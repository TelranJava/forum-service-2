package telran.java52.post.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import telran.java52.model.Post;

public interface PostRepository extends MongoRepository<Post, String> {

	Stream<Post> findByAuthorIgnoreCase(String author);

	Stream<Post> findPostsByTagsIn(List<String> tags);

	@Query("{dateCreated: {$gt: ?0, $lt: ?1}}")
	Stream<Post> findPostsByDateCreatedAfterAndBefore(LocalDate after, LocalDate before);
}
