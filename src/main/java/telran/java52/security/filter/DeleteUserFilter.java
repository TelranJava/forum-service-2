package telran.java52.security.filter;

import java.io.IOException;
import java.util.Set;

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
import telran.java52.accounting.model.Role;
import telran.java52.security.model.User;

@Component
@Order(30)
public class DeleteUserFilter implements Filter {

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		if (checkEndpoint(request.getMethod(), request.getServletPath())) {
			String[] splitPath = request.getServletPath().split("/");
			String ownerPath = splitPath[splitPath.length - 1];
			User userPrincipal = (User) request.getUserPrincipal();

			if (!(isOwner(userPrincipal.getName(), ownerPath) || isAdmin(userPrincipal.getRoles()))) {
				response.sendError(403, "You do not have permission to access this resource");
				return;
			}
		}

		chain.doFilter(request, response);
	}

	private boolean isAdmin(Set<String> roles) {
		return roles.contains(Role.ADMINISTRATOR.toString());
	}

	private boolean isOwner(String login, String userPathName) {
		return login.equalsIgnoreCase(userPathName);
	}

	private boolean checkEndpoint(String method, String path) {
		return (HttpMethod.DELETE.matches(method)) && path.matches("/account/user/\\w+");
	}
}

