package com.tupperware.responses;

import java.time.LocalDateTime;

/**
 * Estructura base de la respuesta JSON
 * de todas la peticiones que se realicen
 * @param <T>
 */
public class ApiResponse<T> {
	private int statusCode;
	private String status;
	private String message;
	private LocalDateTime requestDate;
	private T data;
	
	public ApiResponse(int statusCode, String status, String message, LocalDateTime requestDate, T data) {
		this.statusCode = statusCode;
		this.status = status;
		this.message = message;
		this.requestDate = requestDate;
		this.data = data;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LocalDateTime getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(LocalDateTime requestDate) {
		this.requestDate = requestDate;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}	
	
}
