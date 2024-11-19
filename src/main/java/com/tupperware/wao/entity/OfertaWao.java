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
@Table(name = "ARTICULOSWAO")
public class OfertaWao {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idofertas")
	private Integer id; //idOfertas
	@Column(name = "codigoarticulo")
	private Integer codigoArticulo;
	@Column(name = "descripcion")
	private String descripcionArticulo;
//	private String periodo;
//	private Short ciclo;
	private Short anio;
	private Short campania;
	@Column(name = "fechainicio")
	private LocalDateTime fechaInicio;
	@Column(name = "fechafin")
	private LocalDateTime fechaFin;
	@Column(name = "fechacorte")
	private LocalDateTime fechaCorte;
	private Long stock;
	@Column(name = "cantidadmaxrev")
	private Integer cantidadMaxRev;
	@Column(name = "cuentapedidominimo")
	private Integer cuentaPedidoMinimo;
	@Column(name = "tipooferta")
	private String tipoOferta;
	@Column(name = "idgrupoaplicacion")
	private Integer idGrupoAplicacion;
	private Integer cuota;
	@Column(name = "codigoauxiliar")
	private Integer codigoAuxiliar;
	@Column(name = "zonas", length = 500)
	private String zonasAsignadas;
	@Version
    private Integer version = 0;
	
}
