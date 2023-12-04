
package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration

@EnableWebSecurity
public class WebSecurityConfig {

	@Bean
	MemberDetailsService memberDetailsService() {// username return new
		return new MemberDetailsService();
	}

	@Bean
	BCryptPasswordEncoder bCryptPasswordEncoder() {// password return new
		return new BCryptPasswordEncoder();
	}

	@Bean
	DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(memberDetailsService());
		authenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());
		return authenticationProvider;
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(
				(requests) -> requests.requestMatchers(request -> request.getServletPath().equals("/")).permitAll()
				
						.requestMatchers(request -> request.getServletPath().equals("/categories")).hasRole("ADMIN")
						.requestMatchers(request -> request.getServletPath().equals("/category/add")).hasRole("ADMIN")
						.requestMatchers(request -> request.getServletPath().equals("/category/save")).hasRole("ADMIN")
						.requestMatchers(request -> request.getServletPath().equals("/bookedit")).hasRole("ADMIN")
						.requestMatchers(request -> request.getServletPath().equals("/bookadd")).hasRole("ADMIN")
						.requestMatchers(request -> request.getServletPath().equals("/booksave")).hasRole("ADMIN")
						.requestMatchers(request -> request.getServletPath().startsWith("/all")).permitAll()
						.requestMatchers(request -> request.getServletPath().startsWith("/book")).permitAll()
						.requestMatchers(request -> request.getServletPath().equals("/member")).hasRole("ADMIN")
						.requestMatchers(request -> request.getServletPath().equals("/memberadd")).permitAll()
						.requestMatchers(request -> request.getServletPath().startsWith("/member/")).permitAll()
						.requestMatchers(request -> request.getServletPath().startsWith("/bootstrap/")).permitAll()
						.requestMatchers(request -> request.getServletPath().startsWith("/images/")).permitAll()
						.requestMatchers(request -> request.getServletPath().startsWith("/css/")).permitAll()
						.requestMatchers(request -> request.getServletPath().startsWith("/js/")).permitAll()
						.requestMatchers(request -> request.getServletPath().equals("/about")).permitAll()
						.requestMatchers(request -> request.getServletPath().startsWith("/uploads/books/")).permitAll()
						.requestMatchers(request -> request.getServletPath().equals("/cart")).hasRole("USER")
						.requestMatchers(request -> request.getServletPath().equals("/feedback")).hasRole("ADMIN")
						.anyRequest().authenticated())
				.formLogin((form) -> form.loginPage("/login").defaultSuccessUrl("/", true).permitAll())
				.logout((logout) -> logout.logoutUrl("/logout").permitAll())
				.exceptionHandling(handling -> handling.accessDeniedPage("/403")); //
		/*
		 * .formLogin((form) -> form.loginPage("/login").permitAll() //
		 * .defaultSuccessUrl("/items", true) // ) // .logout((logout) ->
		 * logout.logoutUrl("/logout").permitAll()) //
		 * .exceptionHandling().accessDeniedPage("/403"); return http.build();
		 * 
		 */
		return http.build();
	}

}
