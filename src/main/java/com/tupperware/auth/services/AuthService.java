package com.tupperware.auth.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.tupperware.auth.entity.User;
import com.tupperware.auth.repository.informix.ZonasResponsablesRepository;
import com.tupperware.auth.repository.mariadb.UserRepository;
import com.tupperware.auth.utils.JwtUtil;
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
	@Autowired
	ZonasResponsablesRepository zonaRespRepo;
	
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
		String zonasResponsables = zonaRespRepo.obtenerNodoResponsable(dni.toString());

		
		if(user != null) {
			
			if((user.getIdRolWeb()== 2 || user.getIdRolWeb() == 3) && zonasResponsables == null) {
				return new AuthResponse(
						HttpStatus.UNAUTHORIZED.value(),
						"error",
						"El perfil del usuario no tiene zonas asignadas",
						null
						);
			}
			
			if(user != null && passwordEncoder.matches(password, user.getPassword())) {
				String jwt = jwtUtil.generateToken(user.getDni().toString());
				
				actionLogService.logAction(user.getContrato(), "LogIn", "Inicio de Sesion");
				
				return new AuthResponse(
						HttpStatus.OK.value(), 
						HttpStatus.OK.name(), "", jwt);
			}else {
				actionLogService.logAction(user.getContrato(), "LogIn", "Credenciales Invalidas");
				logger.error("Contrato: "+user.getContrato()+" no pudo autenticarse");
				return new AuthResponse(
						HttpStatus.UNAUTHORIZED.value(),
						"error",
						"Clave Incorrecta",
						null
						);
			}
		} else {
			actionLogService.logAction(dni, "LogIn", "El Nro de documento no existe.");
			logger.error("El DNI: "+dni+" no existe en la base de datos");
			return new AuthResponse(
					HttpStatus.FORBIDDEN.value(),
					"error",
					"El Nro de documento no existe.",
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

		User user = userRepo.findByEmail(email);

		if(user != null) {
			if(user.getIdRolWeb()==3 || user.getIdRolWeb()==2) {
				return new AuthResponse(
						HttpStatus.FORBIDDEN.value(), 
						HttpStatus.FORBIDDEN.name(), "El acceso solo está disponible para los perfiles de revendedora y UM.", "");			
			}
			
			
			if(user != null && passwordEncoder.matches(password, user.getPassword())) {
				String jwt = jwtUtil.generateToken(user.getEmail());
				actionLogService.logAction(user.getContrato(), "LogIn", "Inicio de Sesion");
			
				return new AuthResponse(
						HttpStatus.OK.value(), 
						HttpStatus.OK.name(), "", jwt);
			}else {
				actionLogService.logAction(user.getContrato(), "LogIn", "Credenciales Invalidas");
				return new AuthResponse(
		                HttpStatus.UNAUTHORIZED.value(),
		                "error",
		                "Clave Incorrecta",
		                null
		            );
			}
		} else {
			actionLogService.logAction(0, "LogIn", "El "+email+" no existe.");
			return new AuthResponse(
					HttpStatus.FORBIDDEN.value(),
					"error",
					"El email ingresado no existe.",
					null
					);
		}
	}
	
}
