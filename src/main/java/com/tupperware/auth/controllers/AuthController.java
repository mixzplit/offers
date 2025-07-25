package com.tupperware.auth.controllers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tupperware.auth.dto.UserDTO;
import com.tupperware.auth.entity.User;
import com.tupperware.auth.services.AuthService;
import com.tupperware.auth.services.UserService;
import com.tupperware.responses.ApiResponse;
import com.tupperware.responses.AuthResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {
	
	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);	
	
	@Autowired
	AuthService authService;
	@Autowired
	UserService userService;
	
	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@RequestBody UserDTO user){
		logger.info("Logeando...");
		AuthResponse response =  authService.authenticate(user.getEmailDni(), user.getPassword());
		return ResponseEntity.status(response.getStatusCode()).body(response);
	}
	
	@PostMapping("/register")
	public ResponseEntity<ApiResponse<?>> register(@RequestBody User user){
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}
	
	@GetMapping("/perfil")
	public ResponseEntity<ApiResponse<UserDTO>> obtenerDatosUsuario(){
		ApiResponse<UserDTO> user = userService.obtenerDatosUsuario();
		return ResponseEntity.status(user.getStatusCode()).body(user);
	}
	
}
