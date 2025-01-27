package com.tupperware.auth.dto;

import java.util.List;

import com.tupperware.auth.entity.GrupoAplicacion;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {
	private Long idUsuario;
	private String nombres;
	private String email;
	private String password;
	private Integer contrato;
	private Integer dni;
	private Integer patrocinante;
	private String zona;
	private String division;
	private String estado;
	private Integer idPerfil;
	private String nombrePerfil;
	private List<GrupoAplicacion> grupoAplicacion;
	//Propiedad para usar DNI o Email para el Login
	private String emailDni;
	private Integer grupoUM;
}
