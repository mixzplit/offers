package com.tupperware.bitacora.entity;

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
@Table(name = "USERACTIONLOG")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserActionLog {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "contrato_logeado")
	private Integer contratoLogeado;
	private Integer contrato;
	private String action;
	private String details;
	@Column(name = "created_at")
	private LocalDateTime createdAt;
}
