package telran.java52.security.filter;

import java.io.IOException;
import java.security.Principal;
import java.util.Base64;
import java.util.Set;
import java.util.stream.Collectors;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import telran.java52.accounting.dao.UserRepository;
import telran.java52.accounting.model.Role;
import telran.java52.accounting.model.UserAccount;
import telran.java52.security.model.User;

@Component
@RequiredArgsConstructor
@Order(10)
public class AuthenticationFilter implements Filter {
	final UserRepository userRepository;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

//		System.out.println("request:");
//		System.out.println("Path "+request.getServletPath());
//		System.out.println("Method "+request.getMethod());
//		
//		System.out.println("дотуп к заголовкам");
//		System.out.println("Authorization: "+request.getHeader("Authorization"));

		if (checkEndpoint(request.getMethod(), request.getServletPath())) {
			try {
				String[] credential = getCredentials(request.getHeader("Authorization"));
				UserAccount userAccount = userRepository.findById(credential[0]).orElseThrow(RuntimeException::new);

				if (!BCrypt.checkpw(credential[1], userAccount.getPassword())) {
					throw new RuntimeException();
				}
				Set<String> roles = userAccount.getRoles().stream().map(Role::name).collect(Collectors.toSet());

				request = new WrappedRequest(request, userAccount.getLogin(), roles);
			} catch (Exception e) {
				response.sendError(401);
				return;
			}
		}
		chain.doFilter(request, response);
	}

	private boolean checkEndpoint(String method, String path) {
		return !((HttpMethod.POST.matches(method) && path.matches("/account/register"))
				|| ((HttpMethod.POST.matches(method) || HttpMethod.GET.matches(method))
						&& path.matches("/forum/posts/\\w+(/\\w+)?")));
	}

	private String[] getCredentials(String header) {
		String token = header.split(" ")[1];
		String decode = new String(Base64.getDecoder().decode(token));
		return decode.split(":");
	}

	private class WrappedRequest extends HttpServletRequestWrapper {
		private String login;
		private Set<String> roles;

		public WrappedRequest(HttpServletRequest request, String login, Set<String> roles) {
			super(request);
			this.login = login;
			this.roles = roles;
		}

		@Override
		public Principal getUserPrincipal() {
			return new User(login, roles);
		}
	}
}
