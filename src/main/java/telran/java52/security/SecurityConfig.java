package telran.java52.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;

import lombok.RequiredArgsConstructor;
import telran.java52.accounting.model.Role;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
	final CustomWebSecurity webSecurity;
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.httpBasic(Customizer.withDefaults()); // базовая уатентификация
		http.csrf(csrf -> csrf.disable()); // кроссайтовая подмена запросов, в данном случае не нужна - отключаем
//		http.sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)); 
//		 включить куки для аутентификации по сессии, по дефолту они отключены!!!

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
					
// эта система сейчас не работает - устарела
//				.requestMatchers(HttpMethod.PUT, "/forum/post/{id}")				
//					.access(new WebExpressionAuthorizationManager("@webSecurity.checkPostAuthor(#id, authentication.name)"))
					
//	сейчас пишут так: 
//	https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html#migrate-authorize-requests
					
				.requestMatchers(HttpMethod.PUT, "/forum/post/{id}")
					.access((authentication, context) -> new AuthorizationDecision(
							webSecurity.checkPostAuthor(context.getVariables().get("id"), authentication.get().getName())))
				.requestMatchers(HttpMethod.DELETE, "/forum/post/{id}")
					.access((authentication, context) -> {
						boolean checkAuthor = webSecurity.checkPostAuthor(context.getVariables().get("id"), authentication.get().getName());
						boolean checkModerator = context.getRequest().isUserInRole(Role.MODERATOR.name());
						return new AuthorizationDecision(checkAuthor || checkModerator);
						})
				.anyRequest().authenticated()); 
// все запросы требуют аутентификации, (HttpMethod.PUT, "/account/password") проверяется тут
		
		return http.build();
	}
}
