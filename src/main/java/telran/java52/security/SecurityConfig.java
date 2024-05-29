package telran.java52.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;

import telran.java52.accounting.model.Role;

@Configuration
public class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.httpBasic(Customizer.withDefaults()); // базовая уатентификация
		http.csrf(csrf -> csrf.disable()); // кроссайтовая подмена запросов, в данном случае не нужна - отключаем
		http.authorizeHttpRequests(authorize -> authorize    
				.requestMatchers("/account/register",
						"/forum/posts/**" //, OR:
//						"/forum/posts/author/{author}",
//						"/forum/posts/tags",
//						"/forum/posts/period"
						).permitAll()
				
//				 authenticated, owner or Administrator
				.requestMatchers("/account/user/{login}/role/{role}")
					.hasRole(Role.ADMINISTRATOR.name())
					
//				Put	authenticated, owner
				.requestMatchers(HttpMethod.PUT,"/account/user/{login}")
					.access(new WebExpressionAuthorizationManager("#login == authentication.name")) 
				
//				Delete	authenticated, owner or Administrator
				.requestMatchers(HttpMethod.DELETE,"/account/user/{login}")
					.access(new WebExpressionAuthorizationManager("#login == authentication.name or hasRole('ADMINISTRATOR')")) 
					
//					Delete	authenticated, owner, Moderator
				.requestMatchers(HttpMethod.DELETE, "/forum/post/{id}")
				.access(new WebExpressionAuthorizationManager("#login == authentication.name or hasRole('MODERATOR')")) 
				

//				.requestMatchers("/account/password"). //	Put	authenticated, owner
//				.requestMatchers("/forum/post/{id}"). //	Put	authenticated, owner
				
//				.requestMatchers("/forum/post/{author}"). //	Post	authenticated, login == author
//				.requestMatchers("/forum/post/{id}/comment/{author}"). //	Put	authenticated, login == author
				
				.anyRequest().authenticated()  // все запросы требуют аутентификации
//				.anyRequest().permitAll()   // все запросы БЕЗ аутентификации
				);
		return http.build();
	}
}
