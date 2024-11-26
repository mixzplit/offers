package com.tupperware.wao.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tupperware.auth.entity.GrupoAplicacion;
import com.tupperware.auth.entity.Revendedora;
import com.tupperware.auth.entity.User;
import com.tupperware.auth.repository.RevendedoraRepository;
import com.tupperware.auth.repository.UserRepository;
import com.tupperware.bitacora.services.UserActionLogService;
import com.tupperware.responses.ApiResponse;
import com.tupperware.utils.AutenticacionUtil;
import com.tupperware.wao.dto.OfertaUsuarioDTO;
import com.tupperware.wao.entity.OfertaWao;
import com.tupperware.wao.entity.RegistroOfertaWao;
import com.tupperware.wao.repository.OfertaWaoRepository;
import com.tupperware.wao.repository.RegistroOfertaWaoRepository;

import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PessimisticLockException;


@Service
public class RegistroOfertaWaoService {
	private static final Logger logger = LoggerFactory.getLogger(RegistroOfertaWaoService.class);
	
	@Autowired
	RegistroOfertaWaoRepository registroOferta;
	@Autowired
	OfertaWaoRepository ofertaWao;
	@Autowired
	UserRepository userRepo;
	@Autowired
	RevendedoraRepository revRepo;
	@Autowired
	UserActionLogService actionLogService;
	@Autowired
	AutenticacionUtil authUtil;
	
	@Transactional
	public ApiResponse<?> registrarOfertaWao(Integer contrato, Integer idOferta, Integer cantidad){
		String username = authUtil.getAuthenticatedUserEmail();
		User userLogueado = userRepo.findByDni(Integer.valueOf(username));
		
		if(userLogueado.getIdRolWeb() == 4 && !userLogueado.getContrato().equals(contrato) ) {
			// SI ENTRA AQUI ES UM Y PUEDE CARGAR
			// OFERTAS A OTROS USUARIOS DE SU GRUPO
			if(!esResponsableUM(userLogueado.getContrato(), contrato)) {
				actionLogService.logAction(userLogueado.getContrato(), "RegistroOferta", "Intento de registro: contrato "+contrato+" no pertenece al grupo de: "+ userLogueado.getContrato());
				return new ApiResponse<>(HttpStatus.FORBIDDEN.value(),
	                    "error", "El usuario no pertenece a tu grupo", 
	                    LocalDateTime.now(), null);
			}
			
		}
		
		if(userLogueado.getIdRolWeb() == 1 && !contrato.equals(userLogueado.getContrato())) {
			actionLogService.logAction(userLogueado.getContrato(), "RegistroOferta", "Intento de registro: contrato "+contrato+" no pertenece al usuario");
			return new ApiResponse<>(HttpStatus.FORBIDDEN.value(), 
	                "error", "No puedes registrar ofertas para otro usuario", 
	                LocalDateTime.now(), null);
		}
		
		return persistirRegistroOferta(userLogueado.getContrato(), contrato, idOferta, cantidad);
		
	}
	
	/**
	 * Obtenemos las ofertas solicitadas
	 * por el usuario
	 * @param username
	 * @param anio
	 * @param campania
	 * @return
	 */
	public ApiResponse<List<OfertaUsuarioDTO>> ofertasUsuario(Short anio, Short campania){
		try {
			String username = authUtil.getAuthenticatedUserEmail();
			
			User user = userRepo.findByDni(Integer.valueOf(username));
			Integer contrato = user.getContrato();
			List<OfertaUsuarioDTO> ofertas;
			
			if(anio == null && campania == null) {
				ofertas = registroOferta.findOfertasByContrato(contrato);
			}else {
				ofertas = registroOferta.findOfertasByContratoAndAnioAndCampania(contrato, anio, campania);
			}
			
			actionLogService.logAction(user.getContrato(), "MisOfertas", "Consulta de ofertas de usuario");
			
			return new ApiResponse<>(HttpStatus.OK.value(), 
					"success", 
					"fetched", 
					LocalDateTime.now(), ofertas);
		} catch (Exception e) {
			logger.error("Error al obtener las ofertas del usuario", e);
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
	    return revRepo.existsByPatrocinanteAndContrato(contratoUM, contratoUsuario);
	}
	
	private ApiResponse<?> persistirRegistroOferta(Integer contratoLogeado, Integer contrato, Integer idOferta, Integer cantidad){
		
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
		
		//Validar que el contrato a cargar la oferta tenga asignado
		//el grupo de aplicacion configurado en la oferta
		
		//1.- Validar si el contrato logeado no es igual al contrato que viene en la
		//	  peticion
		if(!contratoLogeado.equals(contrato)) {
			// buscar los ID grupo aplicacion del contrato
			Revendedora rev = revRepo.findByContrato(contrato);
			List<Integer> gruposUsuario = rev.getGrupoAplicacion().stream()
					.map(GrupoAplicacion::getIdGrupoAplicacion) // Extraer los IDs de cada GrupoAplicacion
					.toList(); // Convertir a una lista
			
		
			boolean perteneceGrupo = gruposUsuario.stream()
						.anyMatch(id -> id.equals(ofertaE.getIdGrupoAplicacion()));
			
			if(!perteneceGrupo) {
				return new ApiResponse<>(HttpStatus.FORBIDDEN.value(), 
						"error", 
						"La oferta no esta disponible para este numero de cliente", 
						LocalDateTime.now(), 
						null);
			}
			
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
			
			actionLogService.logAction(contrato, "RegistroOferta", "Se registro la oferta: "+ idOferta);
			
			return new ApiResponse<>(HttpStatus.CREATED.value(), 
					"Registro exitoso", "", 
					LocalDateTime.now(), registro);
		} catch (OptimisticLockException | PessimisticLockException e) {
			logger.error("Conflicto de concurrencia. Por favor, intente nuevamente.", e);
		    return new ApiResponse<>(HttpStatus.CONFLICT.value(),
		            "error", "Conflicto de concurrencia. Por favor, intente nuevamente.",
		            LocalDateTime.now(), null);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("No se pudo registrar la oferta", e);
			return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
							"Error", "No se pudo registrar la oferta", 
							LocalDateTime.now(), e.getMessage());
		}
		
	}
}
