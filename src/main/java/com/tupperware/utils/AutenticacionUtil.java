package com.tupperware.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class AutenticacionUtil {
	/**
	 * obtiene informacion del usuario logeado
	 * desde el contexto de Spring Security
	 * @return
	 */
	public String getAuthenticatedUserEmail() {
		try {
			Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	        
	        if (principal instanceof UserDetails) {
	            return ((UserDetails) principal).getUsername();
	        } else {
	            return principal.toString();
	        }
		} catch (Exception e) {
            // Manejar el error, por ejemplo, registrando la excepción
            System.err.println("Error al obtener el email del usuario autenticado: " + e.getMessage());
            return null; // Retornar null en caso de error
        }
    }
}
