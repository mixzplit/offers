package com.tupperware.wao.entity;

import java.time.LocalDateTime;

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
@Table(name = "registrowao")
public class RegistroOfertaWao {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id; //idRegistro
	private Integer contrato;
	private Integer idOferta;
	private Integer cantidadSolicitada;
	private LocalDateTime fechaRegistro;
	private String estado;
}
