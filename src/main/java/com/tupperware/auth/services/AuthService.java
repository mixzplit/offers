package com.tupperware.auth.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tupperware.auth.entity.User;
import com.tupperware.auth.repository.UserRepository;
import com.tupperware.auth.utils.JwtUtil;
import com.tupperware.bitacora.services.UserActionLogService;
import com.tupperware.responses.AuthResponse;

@Service
public class AuthService {
	private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	JwtUtil jwtUtil;
	@Autowired
	UserActionLogService actionLogService;
	
	/**
	 * Authentication by DNI
	 * @param dni
	 * @param password
	 * @return
	 */
	public AuthResponse authenticate(Integer dni, String password) {
		User user = userRepo.findByDni(dni);
		
		if(user != null && user.getPassword().equals(password)) {
			String jwt = jwtUtil.generateToken(user.getDni().toString());
			
			return new AuthResponse(
					HttpStatus.OK.value(), 
					HttpStatus.OK.name(), "", jwt);
		}else {
			return new AuthResponse(
	                HttpStatus.UNAUTHORIZED.value(),
	                "error",
	                "Invalid credentials",
	                null
	            );
		}
	}
	/**
	 * Authentication by email
	 * @param email
	 * @param password
	 * @return
	 */
	public AuthResponse authenticate(String email, String password) {
		logger.info("Buscando Usuario...");
		User user = userRepo.findByEmail(email);
		
		if(user != null && user.getPassword().equals(password)) {
			logger.info("Generando token...");
			String jwt = jwtUtil.generateToken(user.getEmail());
			logger.info("token generado!! ", jwt);
			
			actionLogService.logAction(user.getIdUsuario(), "LogIn", "Inicio de Sesion");
			
			return new AuthResponse(
					HttpStatus.OK.value(), 
					HttpStatus.OK.name(), "", jwt);
		}else {
			actionLogService.logAction(user.getIdUsuario(), "LogIn", "Credenciales Invalidas");
			return new AuthResponse(
	                HttpStatus.UNAUTHORIZED.value(),
	                "error",
	                "Invalid credentials",
	                null
	            );
		}
	}
	
}
