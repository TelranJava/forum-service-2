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
import telran.java52.accounting.exeption.UserNotFoundException;
import telran.java52.accounting.model.Role;
import telran.java52.accounting.model.UserAccount;
import telran.java52.model.Post;
import telran.java52.post.dao.PostRepository;

@Component
@RequiredArgsConstructor
@Order(60)
public class DeletePostFilter implements Filter {
	final PostRepository postRepository;
	final UserRepository userRepository;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
//		HttpServletRequest request = (HttpServletRequest) req;
//        HttpServletResponse response = (HttpServletResponse) resp;
//        if (checkEndPoint(request.getMethod(), request.getServletPath())) {
//            String principal = request.getUserPrincipal().getName();
//            UserAccount userAccount = userRepository.findById(principal).get();
//            String[] parts = request.getServletPath().split("/");
//            String postId = parts[parts.length - 1];
//            Post post = postRepository.findById(postId).orElse(null);
//            if (post == null) {
//                response.sendError(404, "Not found");
//                return;
//            }
//            if (!(principal.equals(post.getAuthor()) || userAccount.getRoles().contains(Role.MODERATOR))) {
//                response.sendError(403, "You do not have permission to access this resource");
//                return;
//            }
//        }
//        chain.doFilter(request, response);
//    }
//
//    private boolean checkEndPoint(String method, String path) {
//        return HttpMethod.DELETE.matches(method) && path.matches("/forum/post/\\w+");
//    }
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		String method = request.getMethod();
		String path = request.getServletPath();

		if (checkEndpoint(method, path)) {

			String[] splitPath = path.split("/");
			String postId = path.split("/")[splitPath.length - 1];

			String login = request.getUserPrincipal().getName();
			String author = postRepository.findById(postId).get().getAuthor();
			
			Set<Role> roles;
			try {
				UserAccount user = userRepository.findById(login).orElseThrow(UserNotFoundException::new);
				roles = user.getRoles();
			} catch (UserNotFoundException e) {
				response.sendError(404);
				return;
			}

			if (!(HttpMethod.DELETE.matches(method) && (isAuthor(login, author) || isModerator(roles)))) {
				response.sendError(403);
				return;
			}
		}

		chain.doFilter(request, response);
	}
	private boolean isModerator(Set<Role> roles) {
		return roles.contains(Role.MODERATOR);
	}

	private boolean isAuthor(String login, String author) {
		return login.equalsIgnoreCase(author);
	}

	private boolean checkEndpoint(String method, String path) {
		return HttpMethod.DELETE.matches(method) && path.matches("/forum/post/\\w+");
	}
}
