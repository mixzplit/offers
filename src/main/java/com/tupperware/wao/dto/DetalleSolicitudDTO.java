package com.tupperware.wao.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Clase para mapear el detalle
 * de los registros de ofertas del
 * usuario logeado
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetalleSolicitudDTO {
	private Integer contrato;
	private Integer grupo;
	private String nombre;
	private Integer cantidadSolicitada;
}
