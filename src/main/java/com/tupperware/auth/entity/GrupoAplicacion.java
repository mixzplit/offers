package com.tupperware.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class GrupoAplicacion {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	//Integer contrato;
	
	@ManyToOne
    @JoinColumn(name = "contrato", referencedColumnName = "contrato")  // Referencia al contrato en User
	@JsonIgnore
    private User contrato; 
	
	Integer idGrupoAplicacion;
	String descripcion;
}
