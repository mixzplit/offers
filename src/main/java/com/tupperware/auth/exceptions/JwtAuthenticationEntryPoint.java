package com.tupperware.auth.exceptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Clase que maneja los errores de autenticacion
 * como token expirado, token no presente o token
 * invalido
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private static final String ERROR_MESSAGE = "Acceso denegado o el token ha expirado.";
	private static final ObjectMapper MAPPER = new ObjectMapper(); //PARA QUE NO SE RECONFIGURE
	
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("timestamp", System.currentTimeMillis());
		responseMap.put("status", HttpStatus.UNAUTHORIZED.value());
		responseMap.put("error", "unauthorized");
		responseMap.put("message", ERROR_MESSAGE);
		responseMap.put("path", request.getRequestURI());

		response.setContentType("application/json");
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		
		MAPPER.writeValue(response.getWriter(), responseMap);		
	}

}
