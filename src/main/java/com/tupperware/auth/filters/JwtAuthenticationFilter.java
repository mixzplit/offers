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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tupperware.auth.services.UserService;
import com.tupperware.auth.utils.JwtUtil;
import com.tupperware.responses.ApiResponse;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
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
		try {
			// Obtenemos el token de la request
			String token = getTokenRequest(request);
			
			
	//		if(token == null) {
	//			filterChain.doFilter(request, response);
	//            return;
	//		}
			if(token != null) {
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
			}
				
			filterChain.doFilter(request, response);
		}catch(ExpiredJwtException e) {
			logger.error("Token expirado: {}", e);
	        sendJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "Token expirado");			
		}catch (MalformedJwtException e) {
			logger.error("Token mal formado: {}", e);
			sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Token mal formado.");
		}catch(Exception e) {
			logger.error("Error en JwtAuthenticationFilter: {}", e);
	        sendJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "Token no válido o error interno");
		}
		
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

	private void sendJsonError(HttpServletResponse response, int status, String message) throws IOException {
		response.setStatus(status);
		response.setContentType("application/json");
		ApiResponse<?> errorRes =  new ApiResponse<>(status, "error", message, null, null);
		ObjectMapper mapper =  new ObjectMapper();
		response.getWriter().write(mapper.writeValueAsString(errorRes));
		response.getWriter().flush();
	}
}
