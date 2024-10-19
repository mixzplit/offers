package com.tupperware.auth.services;

import java.time.LocalDateTime;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tupperware.auth.dto.UserDTO;
import com.tupperware.auth.entity.User;
import com.tupperware.auth.repository.UserRepository;
import com.tupperware.bitacora.services.UserActionLogService;
import com.tupperware.responses.ApiResponse;

@Service
public class UserService implements UserDetailsService {

	@Autowired
	UserRepository userRepo;
	@Autowired
	UserActionLogService actionLogService;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepo.findByEmail(email);
		if (user == null) {
			throw new UsernameNotFoundException("Usuario no encontrado con el email: " + email);
		}
		
		UserDetails userDet = new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), Collections.emptyList());
		
		return userDet;
	}
	
	public ApiResponse<UserDTO> obtenerDatosUsuario(String email){
		User user = userRepo.findByEmail(email);
		
		if(user != null) {
		
			UserDTO userDto = new UserDTO();
			userDto.setIdUsuario(user.getIdUsuario());
			userDto.setNombres(user.getNombres());
			userDto.setContrato(user.getContrato());
			userDto.setDni(user.getDni());
			userDto.setPatrocinante(user.getPatrocinante());
			userDto.setEmail(user.getEmail());
			userDto.setZona(user.getZona());
			userDto.setIdPerfil(user.getRol().getNombreRol());
			userDto.setGrupoAplicacion(user.getGrupoAplicacion());
			
			actionLogService.logAction(user.getIdUsuario(), "Perfil", "Consulta perfil Usuario");
			
			return new ApiResponse<>(HttpStatus.OK.value(), 
					"success", 
					"fetched", 
					LocalDateTime.now(), userDto);
		}else {
			return new ApiResponse<>(HttpStatus.NOT_FOUND.value(), 
					"Usuario no encontrato", 
					"not found", 
					LocalDateTime.now(), null);
		}
		
	}
	

}
