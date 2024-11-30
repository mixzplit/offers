package com.tupperware.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.tupperware.auth.exceptions.JwtAuthenticationEntryPoint;
import com.tupperware.auth.filters.JwtAuthenticationFilter;
import com.tupperware.auth.utils.MD5PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Autowired
	JwtAuthenticationFilter jwtAuthFilter;
	@Autowired
	JwtAuthenticationEntryPoint jwtAuthEntryPoint;
	
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		
		http.csrf(crsf -> crsf.disable())
			.authorizeHttpRequests(requests -> requests
						.requestMatchers(
							"/auth/**",
							"/**",
		                    "/images/**"   
						)
						.permitAll()
						.anyRequest().authenticated()
					).formLogin(formLogin -> formLogin.disable())
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			// filtro de autenticacion jwt
			.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
			.exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthEntryPoint));
		
		
		return http.build();
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new MD5PasswordEncoder();
	}
	
}
