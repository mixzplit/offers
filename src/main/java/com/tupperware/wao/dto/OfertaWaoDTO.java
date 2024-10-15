package com.tupperware.wao.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OfertaWaoDTO {
	private Integer idOferta;
	private Integer codigoArticulo;
	private String descripcionArticulo;
	private Short anio;
	private Short campania;
	private LocalDateTime fechaInicio;
	private LocalDateTime fechaFin;
	private Long stock;
	private Integer cantidadMaxRev;
	private Integer idGrupoAplicacion;
	private Integer cuota;
	private Integer codigoAuxiliar;
	private String zonasAsignadas;
}
