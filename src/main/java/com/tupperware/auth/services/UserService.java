package com.tupperware.auth.services;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tupperware.auth.dto.UserDTO;
import com.tupperware.auth.entity.Revendedora;
import com.tupperware.auth.entity.User;
import com.tupperware.auth.repository.informix.ZonasResponsablesRepository;
import com.tupperware.auth.repository.mariadb.RevendedoraRepository;
import com.tupperware.auth.repository.mariadb.UserRepository;
import com.tupperware.bitacora.services.UserActionLogService;
import com.tupperware.responses.ApiResponse;
import com.tupperware.utils.AutenticacionUtil;
import com.tupperware.utils.ValidationUtil;

@Service
public class UserService implements UserDetailsService {

	@Autowired
	UserRepository userRepo;
	@Autowired
	RevendedoraRepository revRepo;
	@Autowired
	ZonasResponsablesRepository zonaRespRepo;
	@Autowired
	UserActionLogService actionLogService;
	@Autowired
	AutenticacionUtil authUtil;
	
	@Override
	public UserDetails loadUserByUsername(String identificador) throws UsernameNotFoundException {
		User user;
		// Validamos si el usuario inicio con DNI o Email
		// y lo propagamos en el contexto de Spring Security
		if(ValidationUtil.isEmail(identificador)) {
			user = userRepo.findByEmail(identificador);
		}else {
			user = userRepo.findByDni(Integer.parseInt(identificador));
		}
		
		if (user == null) {
			throw new UsernameNotFoundException("Usuario no encontrado con el email: " + identificador);
		}
		// Detalle del usuario de spring security pasando por parametro
		// el email y pass del usuario obtenido
		UserDetails userDet = new org.springframework.security.core.userdetails.User(user.getDni().toString(), user.getPassword(), Collections.emptyList());
		
		return userDet;
	}
	
	public ApiResponse<UserDTO> obtenerDatosUsuario(){
		// usuario autenticado
		String username = authUtil.getAuthenticatedUserEmail();
		
		User user = userRepo.findByDni(Integer.valueOf(username));
		//Buscamos segun el numero de documento la/s zona/s 
		//responsable/s del usuario logeado
		String zonasResponsables = zonaRespRepo.obtenerNodoResponsable(username);
		//Creamos una lista de zonas
		List<String> zonasList = Arrays.stream(zonasResponsables.split(","))
								.map(String::trim)
								.filter(zona -> !zona.isEmpty()) // filtramos por si hay algun valor vacio entre comas
								.collect(Collectors.toList());
		
		if(user != null) {
			Revendedora rev = revRepo.findByContrato(user.getContrato());		
		
			UserDTO userDto = new UserDTO();
			//userDto.setIdUsuario(rev.getIdRevendedora());
			userDto.setNombres(rev.getNombres());
			userDto.setContrato(rev.getContrato());
			userDto.setDni(rev.getDni());
			userDto.setPatrocinante(rev.getPatrocinante());
			userDto.setEmail(rev.getEmail());
			userDto.setZona(rev.getZona());
			userDto.setDivision(rev.getDivision());
			userDto.setIdPerfil(user.getIdRolWeb());
			userDto.setNombrePerfil(user.getNombreRol());
			userDto.setGrupoAplicacion(rev.getGrupoAplicacion());
			userDto.setNodoResponsables(zonasList);
			
			actionLogService.logAction(user.getContrato(), "Perfil", "Consulta perfil Usuario");
			
			return new ApiResponse<>(HttpStatus.OK.value(), 
					"success", 
					"fetched", 
					LocalDateTime.now(), userDto);
		}else {
			return new ApiResponse<>(HttpStatus.NOT_FOUND.value(), 
					HttpStatus.NOT_FOUND.name(), 
					"Usuario no encontrato",  
					LocalDateTime.now(), null);
		}
		
	}
	/**
	 * Obtiene el nombre del usuario
	 * por contrato
	 * @param contrato
	 * @return
	 */
	public ApiResponse<UserDTO> obtenerDatosUsuarioByContrato(Integer contrato){
		
		Revendedora rev = revRepo.findByContrato(contrato);
		
		
		
		if(rev != null) {
			
			if(rev.getBloqueada()==1) {
				return new ApiResponse<>(HttpStatus.OK.value(), 
						HttpStatus.OK.name(), 
						"El número de cliente ingresado esta bloqueado", 
						LocalDateTime.now(), null);
			}
			
			UserDTO userDto = new UserDTO();
			userDto.setNombres(rev.getNombres());
			return new ApiResponse<>(HttpStatus.OK.value(), 
					"success", 
					"fetched", 
					LocalDateTime.now(), userDto);
		}else {
			return new ApiResponse<>(HttpStatus.NOT_FOUND.value(), 
					HttpStatus.NOT_FOUND.name(), 
					"Usuario no encontrato", 
					LocalDateTime.now(), null);	
		}
	}
	

}
