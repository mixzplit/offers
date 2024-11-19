package com.tupperware.auth.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GrupoAplicacionId {
	private Integer contrato;
	private Integer idGrupoAplicacion;
}
