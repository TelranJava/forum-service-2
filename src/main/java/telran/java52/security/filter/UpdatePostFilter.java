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
import telran.java52.post.dao.PostRepository;
import telran.java52.security.model.User;

@Component
@RequiredArgsConstructor
@Order(50)
public class UpdatePostFilter implements Filter {
	final PostRepository postRepository;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		if (checkEndpoint(request.getMethod(), request.getServletPath())) {
			String[] splitPath = request.getServletPath().split("/");
			String postId = splitPath[splitPath.length - 1];
			User userPrincipal = (User) request.getUserPrincipal();
			String author = postRepository.findById(postId).get().getAuthor();

			if (!isOwner(userPrincipal.getName(), author)) {
				response.sendError(403, "You do not have permission to access this resource");
				return;
			}
		}
		chain.doFilter(request, response);
	}

	private boolean isOwner(String login, String author) {
		return login.equalsIgnoreCase(author);
	}

	private boolean checkEndpoint(String method, String path) {
		return HttpMethod.PUT.matches(method) && path.matches("/forum/post/\\w+");
	}
}
