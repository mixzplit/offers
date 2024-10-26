package com.tupperware.wao.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tupperware.auth.entity.User;
import com.tupperware.auth.repository.UserRepository;
import com.tupperware.bitacora.services.UserActionLogService;
import com.tupperware.responses.ApiResponse;
import com.tupperware.utils.AutenticacionUtil;
import com.tupperware.wao.dto.OfertaWaoDTO;
import com.tupperware.wao.entity.OfertaWao;
import com.tupperware.wao.repository.OfertaWaoRepository;

@Service
public class OfertaWaoService {
	

	@Autowired
	OfertaWaoRepository oferta;
	@Autowired
	UserRepository userRepo;
	@Autowired
	AutenticacionUtil authUtil;
	@Autowired
	UserActionLogService actionLogService;
	
	public ApiResponse<List<OfertaWaoDTO>> obtenerOfertas() {
		
		String username = authUtil.getAuthenticatedUserEmail();
		User user = userRepo.findByEmail(username);
		
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
							oferta.getZonasAsignadas()
						)).collect(Collectors.toList());
			
			actionLogService.logAction(user.getIdUsuario(), "Ofertas", "Consulta de todas las ofertas");
			
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
		
	}
	
	/**
	 * Obtenemos las ofertas activas
	 * @param fecha1
	 * @param fecha2
	 * @return
	 */
	public ApiResponse<List<OfertaWaoDTO>> obtenerOfertasActivas(LocalDateTime fechaActual){
		
		String username = authUtil.getAuthenticatedUserEmail();
		
		User user = userRepo.findByEmail(username);
		String zonaUsuario = "%"+user.getZona()+"%"; // LIKE en el Repository		

		List<OfertaWao> ofertasActivas = oferta.findOfertasActivasPorZonaAndGlobal(fechaActual, zonaUsuario);
		
		if(!ofertasActivas.isEmpty()) {
			
			// Convertir entidad a DTO
			List<OfertaWaoDTO> ofertasActivasDTO = ofertasActivas.stream()
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
							oferta.getZonasAsignadas()
						)).collect(Collectors.toList());
			
			return new ApiResponse<>(HttpStatus.OK.value(), 
					"success", 
					"fetched", 
					LocalDateTime.now(), ofertasActivasDTO);
		}else {
			return new ApiResponse<>(HttpStatus.NO_CONTENT.value(), 
					HttpStatus.NO_CONTENT.name(), 
					"", 
					LocalDateTime.now(), null);
		}
	}
		
}
