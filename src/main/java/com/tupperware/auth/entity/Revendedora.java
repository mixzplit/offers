package com.tupperware.auth.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "REVENDEDORA")
@Getter
@Setter
public class Revendedora {
	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	private Long idRevendedora;
	private Integer contrato;
	private Integer dni;
	@Column(name = "nombre")
	private String nombres;
	private Integer patrocinante;
	private Integer grupo;
	private String zona;
	private String division;
	@Column(name = "tipodocumento")
	private String tipoDocumento;
	@Column(name = "umvigente")
	private String umVigente;
	private String provincia;
	private String localidad;
	private String seccion;
	private Integer bloqueada;
	private String email;

	@OneToMany(mappedBy = "contrato", cascade = CascadeType.ALL) // Relación con GrupoAplicacion
    private List<GrupoAplicacion> grupoAplicacion;
}
