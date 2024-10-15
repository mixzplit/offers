package com.tupperware.wao.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tupperware.auth.entity.User;
import com.tupperware.auth.repository.UserRepository;
import com.tupperware.bitacora.services.UserActionLogService;
import com.tupperware.responses.ApiResponse;
import com.tupperware.wao.dto.OfertaUsuarioDTO;
import com.tupperware.wao.entity.OfertaWao;
import com.tupperware.wao.entity.RegistroOfertaWao;
import com.tupperware.wao.repository.OfertaWaoRepository;
import com.tupperware.wao.repository.RegistroOfertaWaoRepository;


@Service
public class RegistroOfertaWaoService {
	@Autowired
	RegistroOfertaWaoRepository registroOferta;
	@Autowired
	OfertaWaoRepository ofertaWao;
	@Autowired
	UserRepository userRepo;
	@Autowired
	UserActionLogService actionLogService;
	
	@Transactional
	public ApiResponse<?> registrarOfertaWao(String username, Integer contrato, Integer idOferta, Integer cantidad){

		User userLogueado = userRepo.findByEmail(username);
		
		if(userLogueado.getRol().getIdRol() == 2) {
			// SI ENTRA AQUI ES UM Y PUEDE CARGAR
			// OFERTAS A OTROS USUARIOS DE SU GRUPO
			if(!esResponsableUM(userLogueado.getContrato(), contrato)) {
				actionLogService.logAction(userLogueado.getIdUsuario(), "RegistroOferta", "Intento de registro: contrato "+contrato+" no pertenece al grupo");
				return new ApiResponse<>(HttpStatus.FORBIDDEN.value(),
	                    "error", "El usuario no pertenece a tu grupo", 
	                    LocalDateTime.now(), null);
			}
			
		}
		
		if(userLogueado.getRol().getIdRol() == 1 && !contrato.equals(userLogueado.getContrato())) {
			actionLogService.logAction(userLogueado.getIdUsuario(), "RegistroOferta", "Intento de registro: contrato "+contrato+" no pertenece al usuario");
			return new ApiResponse<>(HttpStatus.FORBIDDEN.value(), 
	                "error", "No puedes registrar ofertas para otro usuario", 
	                LocalDateTime.now(), null);
		}
		
		return persistirRegistroOferta(contrato, idOferta, cantidad, userLogueado.getIdUsuario());

	}
	
	/**
	 * Obtenemos las ofertas solicitadas
	 * por el usuario
	 * @param username
	 * @param anio
	 * @param campania
	 * @return
	 */
	public ApiResponse<List<OfertaUsuarioDTO>> ofertasUsuario(String username, Short anio, Short campania){
		try {
			User user = userRepo.findByEmail(username);
			Integer contrato = user.getContrato();
			List<OfertaUsuarioDTO> ofertas;
			
			if(anio == null && campania == null) {
				ofertas = registroOferta.findOfertasByContrato(contrato);
			}else {
				ofertas = registroOferta.findOfertasByContratoAndAnioAndCampania(contrato, anio, campania);
			}
			
			actionLogService.logAction(user.getIdUsuario(), "MisOfertas", "Consulta de ofertas de usuario");
			
			return new ApiResponse<>(HttpStatus.OK.value(), 
					"success", 
					"fetched", 
					LocalDateTime.now(), ofertas);
		} catch (Exception e) {
			return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
					"Error:"+ e.getMessage(), "Error al obtener las ofertas del usuario", 
					LocalDateTime.now(), null);
		}		
		
	}
	
	/**
	 * verifica si el contrato ya tiene una
	 * oferta registrada
	 * @param idOfertas
	 * @param contrato
	 * @return
	 */
	private Optional<RegistroOfertaWao> validarRegistro(Integer idOferta, Integer contrato) {
		Optional<RegistroOfertaWao> ofertaRegistrada = registroOferta.findByIdOfertaAndContrato(idOferta, contrato);
		return ofertaRegistrada;
	}
	
	/**
	 * valida si el contratoUM (Usuario Logeado)
	 * es patrocinante de contratoUsuario (usuario a quien se le cargara la oferta)
	 * @param contratoUM
	 * @param contratoUsuario
	 * @return True o False
	 */
	private boolean esResponsableUM(Integer contratoUM, Integer contratoUsuario) {
	    return userRepo.existsByPatrocinanteAndContrato(contratoUM, contratoUsuario);
	}
	
	private ApiResponse<?> persistirRegistroOferta(Integer contrato, Integer idOferta, Integer cantidad, Long idUsuario){
		
		// Verificamos que la oferta este activa
		Optional<OfertaWao> oferta = ofertaWao.findValidarOfertaActiva(LocalDateTime.now(), idOferta);
		
		if(!oferta.isPresent()) {
			return new ApiResponse<>(HttpStatus.NOT_FOUND.value(), 
					"error", "Oferta no encontrada", 
					LocalDateTime.now(), null);
		}		
		
		//Verificamos si el usuario ya tiene la oferta registrada
		if(validarRegistro(idOferta, contrato).isPresent()) {
			return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), 
	                "error", "Oferta ya registrada.", 
	                LocalDateTime.now(), null);
		}
		
		
		OfertaWao ofertaE = oferta.get(); //Entidad
		
		// Verificar si el stock es suficiente
	    if (ofertaE.getStock() < cantidad) {
	        return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), 
	                "error", "No hay suficiente stock para esta oferta.", 
	                LocalDateTime.now(), null);
	    }	    
		
		//validar cantidad maxima a solicitar
		if(cantidad > ofertaE.getCantidadMaxRev()) {
			return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), 
					"error", 
					"La cantidad solicitada supera la cantidad máxima permitida para esta oferta.", 
					LocalDateTime.now(), 
					null);	
		}
		
		try {
			
			RegistroOfertaWao registro = new RegistroOfertaWao();
			registro.setContrato(contrato);
			registro.setCantidadSolicitada(cantidad);
			registro.setIdOferta(idOferta);
			registro.setFechaRegistro(LocalDateTime.now());
			
			// Restar la cantidad del stock
		    ofertaE.setStock(ofertaE.getStock() - cantidad);
		    ofertaWao.save(ofertaE);  // Actualizar el stock en la base de datos		
		    
			// guardamos la oferta al usuario
			registroOferta.save(registro);
			
			actionLogService.logAction(idUsuario, "RegistroOferta", "Se registro la oferta: "+ idOferta);
			
			return new ApiResponse<>(HttpStatus.CREATED.value(), 
					"Registro exitoso", "", 
					LocalDateTime.now(), registro);
		} catch (Exception e) {
			return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
							"Error", "No se pudo registrar la oferta", 
							LocalDateTime.now(), e.getMessage());
		}
		
	}
}
