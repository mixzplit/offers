package com.tupperware.wao.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "REGISTROWAO")
public class RegistroOfertaWao {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idregistro")
	private Integer id; //idRegistro
	private Integer contrato;
	@Column(name = "idofertas")
	private Integer idOferta;
	@Column(name = "cantidad")
	private Integer cantidadSolicitada;
	@Column(name = "fecharegistro")
	private LocalDateTime fechaRegistro;
	private String estado;
}
