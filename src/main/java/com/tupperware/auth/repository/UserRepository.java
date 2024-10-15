package com.tupperware.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tupperware.auth.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByDni(Integer dni);
	
	User findByEmail(String email);
	
	boolean existsByPatrocinanteAndContrato(Integer contratoUM, Integer contrato);
}
