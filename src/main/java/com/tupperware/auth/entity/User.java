package com.tupperware.auth.entity;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "USUARIO_WAO")
@Getter
@Setter
public class User {
	@Id
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer contrato;
	//private Long idUsuario;
	private String email;
	private String password;
	@Column(name = "numdoc")
	private Integer dni;
	@Column(name = "idrolweb")
	private Integer idRolWeb;
	@Column(name = "nombrerol")
	private String nombreRol;
//	@ManyToOne
//	@JoinColumn(name = "idrolweb")
//	private Rol rol;
}
