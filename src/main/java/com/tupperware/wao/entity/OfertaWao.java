package com.tupperware.wao.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "ofertawao")
public class OfertaWao {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id; //idOfertas
	private Integer codigoArticulo;
	private String descripcionArticulo;
	private String periodo;
	private Short ciclo;
	private Short anio;
	private Short campania;
	private LocalDateTime fechaInicio;
	private LocalDateTime fechaFin;
	private LocalDateTime fechaCorte;
	private Long stock;
	private Integer cantidadMaxRev;
	private Integer cuentaPedidoMinimo;
	private String tipoOferta;
	private Integer idGrupoAplicacion;
	private Integer cuota;
	private Integer codigoAuxiliar;
	@Column(length = 500)
	private String zonasAsignadas;
	@Version
    private Integer version;
	
}
