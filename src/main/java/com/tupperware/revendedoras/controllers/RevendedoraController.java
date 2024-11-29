package com.tupperware.revendedoras.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tupperware.auth.dto.UserDTO;
import com.tupperware.auth.services.UserService;
import com.tupperware.responses.ApiResponse;

@RestController
@RequestMapping("/revendedora")
public class RevendedoraController {
	@Autowired
	UserService userService;
	
	@GetMapping("/{contrato}")
	public ResponseEntity<ApiResponse<UserDTO>> obtenerDatosUsuarioByContrato(@PathVariable Integer contrato){
		ApiResponse<UserDTO> rev = userService.obtenerDatosUsuarioByContrato(contrato);
		
		return ResponseEntity.status(rev.getStatusCode()).body(rev);
	}
}
