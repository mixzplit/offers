package com.tupperware.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "GRUPOAPLICACION")
@Getter
@Setter
public class GrupoAplicacion {

	@EmbeddedId
	private GrupoAplicacionId id;
	
	
	@ManyToOne
    @JoinColumn(name = "contrato", referencedColumnName = "contrato", insertable = false, updatable = false)  // Referencia al contrato en Revendedora
	@JsonIgnore	
    private Revendedora contrato; 
	
	@Column(insertable = false, updatable = false)
	Integer idGrupoAplicacion;
	String descripcion;
}
