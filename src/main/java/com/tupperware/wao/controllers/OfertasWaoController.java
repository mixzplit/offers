package com.tupperware.wao.controllers;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tupperware.responses.ApiResponse;
import com.tupperware.wao.dto.DetalleSolicitudDTO;
import com.tupperware.wao.dto.OfertaUsuarioDTO;
import com.tupperware.wao.dto.OfertaWaoDTO;
import com.tupperware.wao.dto.RegistroOfertaWaoDTO;
import com.tupperware.wao.services.OfertaWaoService;
import com.tupperware.wao.services.RegistroOfertaWaoService;

@RestController
@RequestMapping("/ofertas")
public class OfertasWaoController {

	@Autowired
	OfertaWaoService ofertaService;
	@Autowired
	RegistroOfertaWaoService registroOferta;
	
	@GetMapping("/all")
	public ResponseEntity<ApiResponse<List<OfertaWaoDTO>>> obtenerOfertas(){
		ApiResponse<List<OfertaWaoDTO>> ofertas = ofertaService.obtenerOfertas();
		
		return ResponseEntity.status(ofertas.getStatusCode()).body(ofertas);
	}
	
	@GetMapping("/activas")
	public ResponseEntity<ApiResponse<List<OfertaWaoDTO>>> obtenerOfertasActivas(){
		LocalDateTime fechaActual = LocalDateTime.now();
		ApiResponse<List<OfertaWaoDTO>> ofertas = ofertaService.obtenerOfertasActivas(fechaActual);
		
		return ResponseEntity.status(ofertas.getStatusCode()).body(ofertas);
	}
	
	@PostMapping("/registrar")
	public ResponseEntity<ApiResponse<?>> registrarOfertaWao(@RequestBody RegistroOfertaWaoDTO registro){
		
		ApiResponse<?> dataSaved = registroOferta.registrarOfertaWao(registro.getContrato(), registro.getIdOferta(), registro.getCantidad());
		
		return ResponseEntity.status(dataSaved.getStatusCode()).body(dataSaved);
	}
	
	@PutMapping("/actualizar")
	public ResponseEntity<ApiResponse<?>> actualizarRegistroOfertaWao(){
		
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}
	
	@GetMapping("/mis-ofertas")
	public ResponseEntity<ApiResponse<List<OfertaUsuarioDTO>>> obtenerMisofertas(
													@RequestParam(required = false) Short anio,
													@RequestParam(required = false) Short campania){
		
		ApiResponse<List<OfertaUsuarioDTO>> ofertasUsuario = registroOferta.ofertasUsuario(anio,campania);
		
		return ResponseEntity.status(ofertasUsuario.getStatusCode()).body(ofertasUsuario);
	}
	
	@GetMapping("/detalle/{id}")
	public ResponseEntity<ApiResponse<List<DetalleSolicitudDTO>>> obtenerDetalleSolicitudes(@PathVariable Integer id){
		ApiResponse<List<DetalleSolicitudDTO>> detalleSolicitudes = ofertaService.detalleSolicitudes(id);
		
		return ResponseEntity.status(detalleSolicitudes.getStatusCode()).body(detalleSolicitudes);
	}
}
