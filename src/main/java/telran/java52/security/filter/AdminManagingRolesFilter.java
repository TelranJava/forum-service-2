package telran.java52.security.filter;

import java.io.IOException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import telran.java52.accounting.exeption.AccessDeniedException;
import telran.java52.accounting.exeption.UserNotFoundExeption;
import telran.java52.accounting.model.Role;
import telran.java52.accounting.model.UserAccount;

@Component
@RequiredArgsConstructor
@Order(20)
public class AdminManagingRolesFilter implements Filter {
    final UserRepository userRepository;
    private static final Pattern PATH_PATTERN = Pattern.compile("(?i)^/account/user/[^/]+/role/(ADMINISTRATOR|MODERATOR|USER)$");

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String method = request.getMethod();
        String path = request.getServletPath();

        if (checkEndpoint(method, path)) {
            Matcher matcher = PATH_PATTERN.matcher(path);
            if (matcher.matches()) {
                String login = request.getUserPrincipal().getName(); 
                UserAccount admin;

                try {
                    admin = userRepository.findById(login).orElseThrow(UserNotFoundExeption::new);

                    if (!isAdmin(admin.getRoles())) {
                        throw new AccessDeniedException();
                    }

                } catch (UserNotFoundExeption e) {
                    response.sendError(404);//HttpServletResponse.SC_NOT_FOUND);
                    return;
                } catch (AccessDeniedException e) {
                    response.sendError(403);//HttpServletResponse.SC_FORBIDDEN);
                    return;
                } catch (Exception e) {
                    response.sendError(400);//HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
            } else {
                response.sendError(400);//HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private boolean isAdmin(Set<Role> roles) {
        return roles.contains(Role.ADMINISTRATOR);
    }

    private boolean checkEndpoint(String method, String path) {
        return (HttpMethod.PUT.matches(method) || HttpMethod.DELETE.matches(method))
                && PATH_PATTERN.matcher(path).matches();
    }
}

