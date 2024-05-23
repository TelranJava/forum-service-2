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

@Component
@RequiredArgsConstructor
@Order(40)
public class UpdateByOwnerFilter implements Filter {
	final PostRepository postRepository;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		String method = request.getMethod();
		String path = request.getServletPath();

		String[] splitPath = path.split("/");
		String author = path.split("/")[splitPath.length - 1];

		String login = request.getUserPrincipal().getName();

		if (checkEndpoint(method, path)) {
			if (!login.equalsIgnoreCase(author)) {
				response.sendError(403);
				return;
			}
		}

		chain.doFilter(request, response);
	}

	private boolean checkEndpoint(String method, String path) {
		return (HttpMethod.PUT.matches(method) && path.matches("/forum/post/\\w+/comment/\\w+"))
				|| (HttpMethod.POST.matches(method)) && path.matches("/forum/post/\\w+");
	}
}
