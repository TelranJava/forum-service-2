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
import telran.java52.security.model.User;

@Component
@Order(40)
public class UpdateByOwnerFilter implements Filter {

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		String path = request.getServletPath();
		String method = request.getMethod();

		if (checkEndpoint(method, path)) {
			String[] splitPath = path.split("/");
			String ownerPath = splitPath[splitPath.length - 1];
			User userPrincipal = (User) request.getUserPrincipal();

			if (HttpMethod.PUT.matches(method) && path.matches("/account/user/\\w+")
					&& !isOwner(userPrincipal.getName(), ownerPath)) {
				response.sendError(403, "You do not have permission to access this resource");
				return;
			}

			if (HttpMethod.POST.matches(method) && path.matches("/forum/post/\\w+")
					&& !isOwner(userPrincipal.getName(), ownerPath)) {
				response.sendError(403, "You do not have permission to access this resource");
				return;
			}
			
			if (HttpMethod.PUT.matches(method) && path.matches("/forum/post/\\w+/comment/\\w+")
					&& !isOwner(userPrincipal.getName(), ownerPath)) {
				response.sendError(403, "You do not have permission to access this resource");
				return;
			}
		}
		chain.doFilter(request, response);
	}

	private boolean isOwner(String login, String userPathName) {
		return login.equalsIgnoreCase(userPathName);
	}

	private boolean checkEndpoint(String method, String path) {
		return (HttpMethod.PUT.matches(method) && path.matches("/account/user/\\w+"))
				|| (HttpMethod.POST.matches(method) && path.matches("/forum/post/\\w+"))
				|| (HttpMethod.PUT.matches(method) && path.matches("/forum/post/\\w+/comment/\\w+"));
	}
}
