package com.tupperware.wao.services;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tupperware.auth.entity.GrupoAplicacion;
import com.tupperware.auth.entity.Revendedora;
import com.tupperware.auth.entity.User;
import com.tupperware.auth.repository.informix.ZonasResponsablesRepository;
import com.tupperware.auth.repository.mariadb.RevendedoraRepository;
import com.tupperware.auth.repository.mariadb.UserRepository;
import com.tupperware.bitacora.services.UserActionLogService;
import com.tupperware.responses.ApiResponse;
import com.tupperware.utils.AutenticacionUtil;
import com.tupperware.wao.dto.DetalleSolicitudDTO;
import com.tupperware.wao.dto.OfertaWaoDTO;
import com.tupperware.wao.entity.OfertaWao;
import com.tupperware.wao.repository.OfertaWaoRepository;

@Service
public class OfertaWaoService {
	private static final Logger logger = LoggerFactory.getLogger(OfertaWaoService.class);

	@Autowired
	OfertaWaoRepository oferta;
	@Autowired
	UserRepository userRepo;
	@Autowired
	RevendedoraRepository revRepo;
	@Autowired
	AutenticacionUtil authUtil;
	@Autowired
	UserActionLogService actionLogService;
	@Autowired
	ZonasResponsablesRepository zonasResponsables;
	
	public ApiResponse<List<OfertaWaoDTO>> obtenerOfertas() {
		try {
			String username = authUtil.getAuthenticatedUserEmail();
			User user = userRepo.findByDni(Integer.valueOf(username));
			
			List<OfertaWao> ofertas = oferta.findAll();
			
			if(!ofertas.isEmpty()) {
				
				// Convertir entidad a DTO
				List<OfertaWaoDTO> ofertasDTO = ofertas.stream()
						.map(oferta -> new OfertaWaoDTO(
								oferta.getId(),
								oferta.getCodigoArticulo(),
								oferta.getDescripcionArticulo(),
								oferta.getAnio(),
								oferta.getCampania(),
								oferta.getFechaInicio(),
								oferta.getFechaFin(),
								oferta.getStock(),
								oferta.getCantidadMaxRev(),
								oferta.getIdGrupoAplicacion(),
								oferta.getCuota(),
								oferta.getCodigoAuxiliar(),
								oferta.getZonasAsignadas(),
								1L
							)).collect(Collectors.toList());
				
				actionLogService.logAction(user.getContrato(), "Ofertas", "Consulta de todas las ofertas");
				
				return new ApiResponse<>(HttpStatus.OK.value(), 
							"success", 
							"fetched", 
							LocalDateTime.now(), ofertasDTO);
			}else {
				return new ApiResponse<>(HttpStatus.NO_CONTENT.value(), 
						HttpStatus.NO_CONTENT.name(), 
						"", 
						LocalDateTime.now(), null);
			}
		}catch (Exception e) {
			logger.error("Error al obtener las ofertas", e);
			return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
					"Error:"+ e.getMessage(), "Error al obtener las ofertas", 
					LocalDateTime.now(), null);
		}
		
	}
	
	/**
	 * Obtenemos las ofertas activas
	 * @param fecha1
	 * @param fecha2
	 * @return
	 */
	public ApiResponse<List<OfertaWaoDTO>> obtenerOfertasActivas(LocalDateTime fechaActual){
		try {
	        String username = authUtil.getAuthenticatedUserEmail();
	        User user = userRepo.findByDni(Integer.valueOf(username));
	        Revendedora rev = revRepo.findByContrato(user.getContrato());
	        String zonaUsuario = "%" + rev.getZona() + "%"; // LIKE en el Repository
	        List<OfertaWao> ofertasActivas;

	        // Determinar lógica según el rol
	        if (user.getIdRolWeb() == 1) {
	        	ofertasActivas = oferta.findOfertasActivasPorZonaAndGlobal(fechaActual, zonaUsuario);
                if (!ofertasActivas.isEmpty()) {
                    List<Integer> gruposUsuario = rev.getGrupoAplicacion().stream()
                            .map(GrupoAplicacion::getIdGrupoAplicacion)
                            .collect(Collectors.toList());

                    ofertasActivas = ofertasActivas.stream()
                            .filter(oferta -> gruposUsuario.contains(oferta.getIdGrupoAplicacion()))
                            .collect(Collectors.toList());
                }
	        }if(user.getIdRolWeb() == 4){
	            ofertasActivas = oferta.findOfertasActivasPorZonaAndGlobal(fechaActual, zonaUsuario);
	        }else {
	        	String zonasResponsablesGzGd = zonasResponsables.obtenerNodoResponsable(username);
                List<String> zonasResponsables = Arrays.stream(zonasResponsablesGzGd.split(","))
                        .map(String::trim)
                        .filter(zona -> !zona.isEmpty())
                        .collect(Collectors.toList());

                if (!zonasResponsables.isEmpty()) {
                    ofertasActivas = oferta.findOfertasGzGd(fechaActual, zonasResponsables);
                } else {
                    ofertasActivas = oferta.findOfertasActivasPorZonaAndGlobal(fechaActual, zonaUsuario);
                }
	        }
	        // Convertir las ofertas activas a DTO
	        List<OfertaWaoDTO> ofertasActivasDTO = ofertasActivas.stream()
	                .map(oferta -> convertirOfertaADTO(oferta, user, rev))
	                .collect(Collectors.toList());

	        actionLogService.logAction(user.getContrato(), "Ofertas", "Consulta de ofertas activas");

	        return new ApiResponse<>(HttpStatus.OK.value(),
	                "success",
	                "fetched",
	                LocalDateTime.now(),
	                ofertasActivasDTO);

		}catch (Exception e) {
			logger.error("Error al obtener las ofertas activas", e);
			return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
					"Error:"+ e.getMessage(), "Error al obtener las ofertas activas", 
					LocalDateTime.now(), null);
		}
	}
	
	
	public ApiResponse<List<DetalleSolicitudDTO>> detalleSolicitudes(Integer idOferta){
		
		String username = authUtil.getAuthenticatedUserEmail();
		
		User user = userRepo.findByDni(Integer.valueOf(username));
		//Revendedora rev = revRepo.findByContrato(user.getContrato());
		
		List<Object[]> result = oferta.detalleSolicitudesPorPerfilYOferta(user.getIdRolWeb(), user.getContrato(), idOferta);
		
		List<DetalleSolicitudDTO> detalleSolicitud = result.stream()
							.map(detalle -> new DetalleSolicitudDTO((Integer)detalle[0], (Integer) detalle[1],
																	(String) detalle[2],(Integer) detalle[3]))
							.collect(Collectors.toList());
		
		return new ApiResponse<>(HttpStatus.OK.value(), 
				"sucess", 
				"fetched", 
				LocalDateTime.now(), detalleSolicitud);
	}
	
	/**
	 * Convierte la Entidad Oferta al DTO
	 * @param oferta
	 * @return
	 */
	private OfertaWaoDTO convertirOfertaADTO(OfertaWao ofertaWao, User user, Revendedora rev) {
		// Calcula el conteo según el perfil y la oferta
	    Long cantidadSolicitudes = oferta.countSolicitudesPorPerfilYOferta(
	            user.getIdRolWeb(),
	            user.getContrato(),
	            ofertaWao.getId()
	    );
	    
	    return new OfertaWaoDTO(
	        ofertaWao.getId(),
	        ofertaWao.getCodigoArticulo(),
	        ofertaWao.getDescripcionArticulo(),
	        ofertaWao.getAnio(),
	        ofertaWao.getCampania(),
	        ofertaWao.getFechaInicio(),
	        ofertaWao.getFechaFin(),
	        ofertaWao.getStock(),
	        ofertaWao.getCantidadMaxRev(),
	        ofertaWao.getIdGrupoAplicacion(),
	        ofertaWao.getCuota(),
	        ofertaWao.getCodigoAuxiliar(),
	        ofertaWao.getZonasAsignadas(),
	        cantidadSolicitudes
	    );
	}
		
}
