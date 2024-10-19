package com.tupperware.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.tupperware.auth.entity.User;
import com.tupperware.auth.services.AuthService;
import com.tupperware.bitacora.entity.UserActionLog;
import com.tupperware.bitacora.repository.UserActionLogRepository;
import com.tupperware.bitacora.services.UserActionLogService;
import com.tupperware.responses.AuthResponse;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
	@Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserActionLogService actionLogService;
    
    @MockBean
    private UserActionLogRepository userActionRepository;
    
    @Test
    void testLoginSuccess() throws Exception {
        // Datos de prueba
        String email = "testuser@example.com";
        String password = "testpassword";
        String jwtToken = "mocked-jwt-token";

        // Crear usuario de prueba
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);

        // Respuesta simulada del AuthService
        AuthResponse authResponse = new AuthResponse(
                HttpStatus.OK.value(), 
                HttpStatus.OK.name(), 
                "", 
                jwtToken
        );

        // Simular el comportamiento del AuthService
        when(authService.authenticate(email, password)).thenReturn(authResponse);
        
        when(userActionRepository.save(any(UserActionLog.class))).thenReturn(new UserActionLog());

        // Ejecutar la solicitud POST al endpoint /login
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"email\": \"" + email + "\", \"password\": \"" + password + "\" }"))
                .andExpect(status().isOk())  // Verificar que el estado sea 200 OK
                .andExpect(jsonPath("$.statusCode").value(200))  // Verificar que el statusCode sea 200
                .andExpect(jsonPath("$.token").value(jwtToken));  // Verificar que el token retornado sea el esperado

        // Verificar que el AuthService fue llamado una vez
        verify(authService, times(1)).authenticate(email, password);
        
        // Verificar que el actionLogService registró la acción
        //verify(actionLogService, times(1)).logAction(anyLong(), eq("LogIn"), eq("Inicio de Sesion"));
    }

    @Test
    void testLoginFailure() throws Exception {
        String email = "testuser@example.com";
        String password = "wrongpassword";

        // Respuesta simulada del AuthService en caso de credenciales inválidas
        AuthResponse authResponse = new AuthResponse(
                HttpStatus.UNAUTHORIZED.value(), 
                "error", 
                "Invalid credentials", 
                null
        );

        // Simular el comportamiento del AuthService cuando las credenciales son incorrectas
        when(authService.authenticate(email, password)).thenReturn(authResponse);

        // Ejecutar la solicitud POST al endpoint /login
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"email\": \"" + email + "\", \"password\": \"" + password + "\" }"))
                .andExpect(status().isUnauthorized())  // Verificar que el estado sea 401 Unauthorized
                .andExpect(jsonPath("$.statusCode").value(401))  // Verificar que el statusCode sea 401
                .andExpect(jsonPath("$.message").value("Invalid credentials"));  // Verificar el mensaje de error

        // Verificar que el AuthService fue llamado una vez
        verify(authService, times(1)).authenticate(email, password);
        
        // Verificar que el actionLogService registró la acción de error
        //verify(actionLogService, times(1)).logAction(anyLong(), eq("LogIn"), eq("Credenciales Invalidas"));
    }
    
    
    @Test
    void testLoginConcurrency() throws Exception {
        int numberOfThreads = 244;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        List<Future<Void>> futures = new ArrayList<>();


        // Simular el comportamiento del AuthService

        for (int i = 0; i < numberOfThreads; i++) {
        	// Datos de prueba
        	String email = "testuser"+ i + "@example.com";
        	String password = "testpassword"+i;
        	String jwtToken = "mocked-jwt-token"+i;

        	AuthResponse authResponse = new AuthResponse(
        			HttpStatus.OK.value(),
        			HttpStatus.OK.name(),
        			"",
        			jwtToken
        			);

        	when(authService.authenticate(email, password)).thenReturn(authResponse);

        	futures.add(executor.submit(() -> {            	
            	

            	mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"" + email + "\", \"password\": \"" + password + "\" }"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.token").value(jwtToken));
                return null;
            }));
        }

        // Esperar a que todas las tareas terminen
        for (Future<Void> future : futures) {
            future.get();
        }

        // Verificar que el AuthService fue llamado correctamente
        verify(authService, times(numberOfThreads)).authenticate(anyString(), anyString());
    }
}
