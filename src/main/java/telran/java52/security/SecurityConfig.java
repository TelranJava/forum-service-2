package telran.java52.security;

import java.net.http.HttpRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import telran.java52.accounting.dao.UserRepository;
import telran.java52.accounting.model.Role;
import telran.java52.accounting.model.UserAccount;
import telran.java52.model.Post;
import telran.java52.post.dao.PostRepository;

@Configuration
public class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.httpBasic(Customizer.withDefaults()); // базовая уатентификация
		http.csrf(csrf -> csrf.disable()); // кроссайтовая подмена запросов, в данном случае не нужна - отключаем


		http.authorizeHttpRequests(authorize -> authorize.requestMatchers("/account/register", "/forum/posts/**" // OR:"/forum/posts/author/{author}","/forum/posts/tags","/forum/posts/period"
		).permitAll() // // все указанные тут запросы БЕЗ аутентификации
				.requestMatchers("/account/user/{login}/role/{role}").hasRole(Role.ADMINISTRATOR.name())
				.requestMatchers(HttpMethod.PUT, "/account/user/{login}")
				.access(new WebExpressionAuthorizationManager("#login == authentication.name"))
				.requestMatchers(HttpMethod.DELETE, "/account/user/{login}")
				.access(new WebExpressionAuthorizationManager(
						"#login == authentication.name or hasRole('ADMINISTRATOR')"))
				.requestMatchers(HttpMethod.POST, "/forum/post/{author}")
				.access(new WebExpressionAuthorizationManager("#author == authentication.name"))
				.requestMatchers(HttpMethod.PUT, "/forum/post/{id}/comment/{author}")
				.access(new WebExpressionAuthorizationManager("#author == authentication.name"))

// ????????     Delete	authenticated, owner, Moderator can remove post
//				.requestMatchers(HttpMethod.DELETE, "/forum/post/{id}")
//					.access(new WebExpressionAuthorizationManager("@postService.isPostOwner('#id, authentication.name') or hasRole('MODERATOR')")) 

//				.requestMatchers(HttpMethod.DELETE, "/forum/post/{id}")
//					.access(new WebExpressionAuthorizationManager("isPostOwner(@postService, #id, authentication.name) or hasRole('MODERATOR')"))
			    
// ????????    Put authenticated, owner can update their posts
//				.requestMatchers(HttpMethod.PUT, "/forum/post/{id}")
//					.access(new WebExpressionAuthorizationManager("@postService.isPostOwner('#id, authentication.name')"))

				.anyRequest().authenticated() // все запросы требуют аутентификации, (HttpMethod.PUT,
												// "/account/password") проверяется тут
		)
		;

		return http.build();
	}
}
