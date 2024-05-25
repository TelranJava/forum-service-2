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
@Order(20)
public class AdminManagingRolesFilter implements Filter {

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		   if (checkEndpoint(request.getMethod(), request.getServletPath())) {
			User userPrincipal = (User) request.getUserPrincipal();
			if (!isAdmin(userPrincipal.getRoles())){
				response.sendError(403);
				return;
			}
		}
		chain.doFilter(request, response);
	}

	private boolean isAdmin(Set<String> roles) {
		return roles.contains(Role.ADMINISTRATOR.toString());
	}

	private boolean checkEndpoint(String method, String path) {
		return (HttpMethod.PUT.matches(method) || HttpMethod.DELETE.matches(method))
				&& path.matches("(?i)^/account/user/[^/]+/role/(ADMINISTRATOR|MODERATOR|USER)$");
//or another regex path.matches("/account/user/\\w+/role/\\w+");
	}
}
