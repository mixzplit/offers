package com.tupperware.auth.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tupperware.auth.entity.User;
import com.tupperware.auth.repository.UserRepository;
import com.tupperware.auth.utils.JwtUtil;
import com.tupperware.auth.utils.MD5PasswordEncoder;
import com.tupperware.bitacora.services.UserActionLogService;
import com.tupperware.responses.AuthResponse;
import com.tupperware.utils.ValidationUtil;

@Service
public class AuthService {
	private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
	
	@Autowired
	UserRepository userRepo;	
	@Autowired
	JwtUtil jwtUtil;
	@Autowired
	UserActionLogService actionLogService;
	@Autowired
	PasswordEncoder passwordEncoder; //bean en SecurityConfig
	
	public AuthResponse authenticate(String emailDni, String password) {
		if(ValidationUtil.isEmail(emailDni)) {
			return authenticateEmail(emailDni, password);
		}else {
			try {
				Integer dni = Integer.parseInt(emailDni);
				return authenticateDni(dni, password);
			} catch (NumberFormatException e) {
				return new AuthResponse(
		                HttpStatus.BAD_REQUEST.value(),
		                "error",
		                "Invalid DNI format",
		                null
		            );
			}
		}
	}
	
	/**
	 * Authentication by DNI
	 * @param dni
	 * @param password
	 * @return
	 */
	private AuthResponse authenticateDni(Integer dni, String password) {
		User user = userRepo.findByDni(dni);
		
		if(user != null && passwordEncoder.matches(password, user.getPassword())) {
			String jwt = jwtUtil.generateToken(user.getDni().toString());
			
			actionLogService.logAction(user.getContrato(), "LogIn", "Inicio de Sesion");
			
			return new AuthResponse(
					HttpStatus.OK.value(), 
					HttpStatus.OK.name(), "", jwt);
		}else {
			actionLogService.logAction(user.getContrato(), "LogIn", "Credenciales Invalidas");
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
	private AuthResponse authenticateEmail(String email, String password) {
		logger.info("Buscando Usuario...");
		User user = userRepo.findByEmail(email);
		
		if(user != null && passwordEncoder.matches(password, user.getPassword())) {
			logger.info("Generando token...");
			String jwt = jwtUtil.generateToken(user.getEmail());
			logger.info("token generado!! ", jwt);
			
			actionLogService.logAction(user.getContrato(), "LogIn", "Inicio de Sesion");
			
			return new AuthResponse(
					HttpStatus.OK.value(), 
					HttpStatus.OK.name(), "", jwt);
		}else {
			actionLogService.logAction(user.getContrato(), "LogIn", "Credenciales Invalidas");
			return new AuthResponse(
	                HttpStatus.UNAUTHORIZED.value(),
	                "error",
	                "Invalid credentials",
	                null
	            );
		}
	}
	
}
