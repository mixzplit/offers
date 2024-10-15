package com.tupperware.wao.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegistroOfertaWaoDTO {
	private Integer id; //idRegistro
	private Integer contrato;
	private Integer idOferta;
	private Integer cantidad; // cantidad solicitada
	private LocalDateTime fechaRegistro;
}
