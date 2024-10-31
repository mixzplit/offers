package com.tupperware.auth.filters;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.tupperware.auth.services.UserService;
import com.tupperware.auth.utils.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Clase para filtrar la autenticacion y que
 * permitira filtrar una vez por peticion http
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	JwtUtil jwtUtil;
	@Autowired
	UserService userDetailsService;
	
	private static final String BEARER_PREFIX = "Bearer ";
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		// Obtenemos el token de la request
		String token = getTokenRequest(request);
		
		
		if(token == null) {
			filterChain.doFilter(request, response);
            return;
		}
		
		String username = jwtUtil.extractUsername(token);
		if(username != null && SecurityContextHolder.getContext().getAuthentication()==null ) {
			
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			
			if(jwtUtil.validateToken(token, username)) {
			
				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				
				auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				
				SecurityContextHolder.getContext().setAuthentication(auth);
				
				logger.info("Autenticación establecida en el contexto para el usuario: " + username);
				logger.info("Detalles de autenticación: " + SecurityContextHolder.getContext().getAuthentication());
			}
			
		}
		
		filterChain.doFilter(request, response);
		
	}

	private String getTokenRequest(HttpServletRequest request) {
		String token = request.getHeader(HttpHeaders.AUTHORIZATION);
		
		//Verificar si viene un token buscando "Bearer"
		if(StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) {
			// aqui obtenemos el token que es desde
			// el caracter 7 hasta el final
			return token.substring(7);
		}
		
		return null;
	}

}
