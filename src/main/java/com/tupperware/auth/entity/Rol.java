package com.tupperware.auth.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Rol {
	@Id
	Long idRol;
	String nombreRol;	
	
	// Constructor que acepta un ID
    public Rol(Long idRol) {
        this.idRol = idRol;
    }
	
}
