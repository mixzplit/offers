package com.tupperware.auth.repository.mariadb;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tupperware.auth.entity.Revendedora;

public interface RevendedoraRepository extends JpaRepository<Revendedora, Long> {
	Revendedora findByEmail(String email);
	Revendedora findByContrato(Integer contrato);
	
	boolean existsByPatrocinanteAndContrato(Integer contratoUM, Integer contrato);
}
