package com.tupperware.auth.entity;


import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idUsuario;
	private String nombres;
	private String email;
	private String password;
	private Integer contrato;
	private String tipoDocumento;
	private Integer dni;
	private Integer patrocinante;
	private Integer grupo;
	private String zona;
	private String division;
	private String estado;
	@ManyToOne
	@JoinColumn(name = "idPerfil")
	private Rol rol;
	//private String idPerfil; //Puede no estar en la definicion final
	private Integer bloqueada;

	@OneToMany(mappedBy = "contrato", cascade = CascadeType.ALL) // Relación con GrupoAplicacion
    private List<GrupoAplicacion> grupoAplicacion;
}
