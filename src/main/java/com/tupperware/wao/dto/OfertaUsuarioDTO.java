package com.tupperware.wao.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Clase para respuesta de ofertas
 * registradas por usuario
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OfertaUsuarioDTO {
	private String descripcionArticulo;
	private Integer codigoArticulo;
	private Integer codigoAuxiliar;
	private Integer cantidadSoclicitada;
	private LocalDateTime fechaRegistro;
	private String estado;
}
