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
import lombok.RequiredArgsConstructor;
import telran.java52.accounting.dao.UserRepository;
import telran.java52.accounting.model.Role;
import telran.java52.accounting.model.UserAccount;

@Component
@RequiredArgsConstructor
@Order(30)
public class AccountManagingFilter implements Filter {
	final UserRepository userRepository;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		String method = request.getMethod();
		String path = request.getServletPath();

		if (checkEndpoint(method, path)) {

			String userName = path.substring(path.lastIndexOf('/') + 1);
			String login = request.getUserPrincipal().getName();
			UserAccount userByLogin = userRepository.findById(login).get();

			if (HttpMethod.PUT.matches(method) && !isOwner(login, userName)) {
				response.sendError(403);
				return;
			}

			if (HttpMethod.DELETE.matches(method) && !(isOwner(login, userName) || isAdmin(userByLogin.getRoles()))) {
				response.sendError(403);
				return;
			}

		}
		chain.doFilter(request, response);
	}

	private boolean isAdmin(Set<Role> roles) {
		return roles.contains(Role.ADMINISTRATOR);
	}

	private boolean isOwner(String login, String userName) {
		return login.equalsIgnoreCase(userName);
	}

	private boolean checkEndpoint(String method, String path) {
		return (HttpMethod.PUT.matches(method) || HttpMethod.DELETE.matches(method))
				&& path.matches("^/account/user/[^/]+$");
	}
}
