package telran.java52.security;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import telran.java52.model.Post;
import telran.java52.post.dao.PostRepository;

@Service(value = "webSecurity")
@RequiredArgsConstructor
public class CustomWebSecurity {
	final PostRepository postRepository;

	public boolean checkPostAuthor(String postId, String login) {
		Post post = postRepository.findById(postId).orElse(null);
		return post != null && post.getAuthor().equalsIgnoreCase(login);
	}
}
