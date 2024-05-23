package telran.java52.security.filter;

import java.io.IOException;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import telran.java52.accounting.dao.UserRepository;
import telran.java52.post.dao.PostRepository;

@Component
@RequiredArgsConstructor
@Order(50)
public class UpdatePostFilter implements Filter {
	final PostRepository postRepository;
	final UserRepository userRepository;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		String method = request.getMethod();
		String path = request.getServletPath();

		String[] splitPath = path.split("/");
		String postId = path.split("/")[splitPath.length - 1];

		String login = request.getUserPrincipal().getName();
		String author = postRepository.findById(postId).get().getAuthor();

		if (checkEndpoint(method, path)) {
			if (!(HttpMethod.PUT.matches(method) && isAuthor(login, author))) {
				response.sendError(403);
				return;
			}
		}

		chain.doFilter(request, response);
	}

	private boolean isAuthor(String login, String author) {
		return login.equalsIgnoreCase(author);
	}

	private boolean checkEndpoint(String method, String path) {
		return HttpMethod.PUT.matches(method) 
				&& path.matches("/forum/post/\\w+");
	}
}
