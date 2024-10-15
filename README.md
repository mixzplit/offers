# Documentación de la API

## Introducción

Bienvenido a la documentación de la API de los servicios **Tupperware Auth** y **Wao Offers**. Esta API te permite gestionar la autenticación, el registro de usuarios e interactuar con las ofertas.

### URLs Base
- **Servicio de Autenticación**: `/auth`
- **Servicio de Ofertas**: `/ofertas`

---

## Endpoints

### Servicio de Autenticación

#### POST `/auth/login`

- **Descripción**: Autentica a un usuario usando su correo electrónico y contraseña.
- **Cuerpo de la solicitud**:
  ```json
  {
    "email": "user@example.com",
    "password": "password123"
  }
  ```
- **Respuestas**:
  - `200 OK`: Devuelve un token JWT si la autenticación es exitosa.
  - `401 Unauthorized`: La autenticación falló.
  

- **Respuesta de autenticación exitosa**:
  ```json
  {
    "statusCode": 200,
    "status": "OK",
    "message": "",
    "token": "JWT_token_string"
  }
  ```

- **Respuesta de error al autenticarse**:
  ```json
  {
    "statusCode": 401,
    "status": "error",
    "message": "Invalid credentials",
    "token": null
  }
  ```

#### POST `/auth/register`

- **Descripción**: Registra un nuevo usuario en el sistema.
- **Cuerpo de la solicitud**:
  ```json
  {
    "email": "newuser@example.com",
    "password": "password123",
    "name": "User Name"
  }
  ```
- **Respuestas**:
  - `200 OK`: Registro de usuario exitoso.
  - `400 Bad Request`: El registro falló debido a errores de validación.

#### GET `/auth/perfil`

- **Descripción**: Recupera la información del perfil del usuario autenticado.
- **Cabeceras**:
  - `Authorization: Bearer <token>`: Se requiere un token JWT para la autenticación.
- **Respuestas**:
  - `200 OK`: Devuelve los datos del perfil del usuario.
  - `401 Unauthorized`: No se proporcionó una autenticación válida.

- **JSON**
	```
	{
	    "statusCode": 200,
	    "status": "success",
	    "message": "fetched",
	    "requestDate": "2024-10-15T19:00:59.3619303",
	    "data": {
	        "idUsuario": 11,
	        "nombres": "LUCIA ROXANA NEUMIRT",
	        "email": "centro-comerc@hotmail.com",
	        "password": null,
	        "contrato": 349140,
	        "dni": 23677958,
	        "patrocinante": 0,
	        "zona": "503",
	        "division": null,
	        "estado": null,
	        "idPerfil": "REVENDEDORA",
	        "grupoAplicacion": [
	            {
	                "id": 17,
	                "idGrupoAplicacion": 11,
	                "descripcion": "Revendedora"
	            },
	            {
	                "id": 18,
	                "idGrupoAplicacion": 15,
	                "descripcion": "No VIP"
	            },
	            {
	                "id": 19,
	                "idGrupoAplicacion": 29,
	                "descripcion": "Plan Carrera"
	            },
	            {
	                "id": 20,
	                "idGrupoAplicacion": 30,
	                "descripcion": "Plan Carrera PLATA"
	            },
	            {
	                "id": 21,
	                "idGrupoAplicacion": 33,
	                "descripcion": "Plan Carrera GRADUADA"
	            },
	            {
	                "id": 22,
	                "idGrupoAplicacion": 34,
	                "descripcion": "VIP o Graduada"
	            },
	            {
	                "id": 23,
	                "idGrupoAplicacion": 38,
	                "descripcion": "No SENIOR"
	            },
	            {
	                "id": 24,
	                "idGrupoAplicacion": 49,
	                "descripcion": "Rev. No Senior No Vip"
	            }
	        ]
	    }
	}
	```

---

### Servicio de Ofertas

#### GET `/ofertas/all`

- **Descripción**: Recupera una lista de todas las ofertas disponibles.
- **Respuestas**:
  - `200 OK`: Devuelve una lista de ofertas.
- **JSON**:
	```
	{
	  "statusCode": 200,
	  "status": "success",
	  "message": "fetched",
	  "requestDate": "2024-10-15T19:02:41.1911511",
	  "data": [
	    {
	      "idOferta": 1,
	      "codigoArticulo": 641581,
	      "descripcionArticulo": "SET UTENSILIOS BASIC X 4",
	      "anio": 2024,
	      "campania": 16,
	      "fechaInicio": "2024-09-11T14:15:00",
	      "fechaFin": "2024-09-11T19:00:00",
	      "stock": 200,
	      "cantidadMaxRev": 1,
	      "idGrupoAplicacion": 11,
	      "cuota": 0,
	      "codigoAuxiliar": null,
	      "zonasAsignadas": "050;052;122;201;206;405"
	    },
	    {
	      "idOferta": 2,
	      "codigoArticulo": 641731,
	      "descripcionArticulo": "SET PRENSA Y PORTA HAMBUR + ES PECIERO X",
	      "anio": 2024,
	      "campania": 16,
	      "fechaInicio": "2024-09-12T14:19:00",
	      "fechaFin": "2024-09-12T19:05:00",
	      "stock": 150,
	      "cantidadMaxRev": 2,
	      "idGrupoAplicacion": 34,
	      "cuota": 0,
	      "codigoAuxiliar": null,
	      "zonasAsignadas": "050;052;122;201;206;405;108;109;306;310;311"
	    },
	    {
	      "idOferta": 3,
	      "codigoArticulo": 641683,
	      "descripcionArticulo": "WAO REFR INTELIG TRANSP Y AQUA 8 LT",
	      "anio": 2024,
	      "campania": 16,
	      "fechaInicio": "2024-09-13T14:00:00",
	      "fechaFin": "2024-09-14T14:00:00",
	      "stock": 600,
	      "cantidadMaxRev": 1,
	      "idGrupoAplicacion": 11,
	      "cuota": 0,
	      "codigoAuxiliar": null,
	      "zonasAsignadas": "050;052;122;201;206;405;108;109;306;310;311;103;111;132;138;513;621;626;627"
	    },
	    {
	      "idOferta": 4,
	      "codigoArticulo": 640316,
	      "descripcionArticulo": "BOWL CREATIVA VER LIM 7,8LT BR",
	      "anio": 2024,
	      "campania": 16,
	      "fechaInicio": "2024-09-16T14:00:00",
	      "fechaFin": "2024-09-16T21:00:00",
	      "stock": 1500,
	      "cantidadMaxRev": 2,
	      "idGrupoAplicacion": 11,
	      "cuota": 0,
	      "codigoAuxiliar": null,
	      "zonasAsignadas": "050;052;122;201;206;405;108;109;306;310;311;103;111;132;138;513;621;626;627;149;162;402;602;607"
	    }    
	  ]
	}	
	```

#### GET `/ofertas/activas`

- **Descripción**: Recupera una lista de las ofertas actualmente activas.
- **Respuestas**:
  - `200 OK`: Devuelve una lista de ofertas activas.

#### POST `/ofertas/registrar`

- **Descripción**: Registra a un usuario para una oferta.
- **Cuerpo de la solicitud**:
  ```json
  {
    "contrato": "123456",
    "idOferta": 1,
    "cantidad": 2
  }
  ```
- **Respuestas**:
  - `200 OK`: Registro de oferta exitoso.
  - `400 Bad Request`: El registro falló debido a datos inválidos.

#### PUT `/ofertas/actualizar`

- **Descripción**: Actualiza un registro de oferta existente.
- **Respuestas**:
  - `200 OK`: Actualización exitosa.

#### GET `/ofertas/mis-ofertas`

- **Descripción**: Recupera las ofertas registradas por el usuario autenticado.
- **Cabeceras**:
  - `Authorization: Bearer <token>`: Se requiere un token JWT para la autenticación.
- **Parámetros de Consulta**:
  - `anio` (opcional): Filtra las ofertas por año.
  - `campania` (opcional): Filtra las ofertas por campaña.
- **Respuestas**:
  - `200 OK`: Devuelve las ofertas registradas por el usuario.
  - `401 Unauthorized`: No se proporcionó una autenticación válida.

---

## Autenticación

Todas las solicitudes a los endpoints protegidos requieren un token JWT. Este puede obtenerse iniciando sesión a través del endpoint `/auth/login`.

- **Cabecera de Autorización**: `Authorization: Bearer <token>`

## Códigos de Error

- `400 Bad Request`: La solicitud estaba malformada o contenía datos inválidos.
- `401 Unauthorized`: La solicitud requiere autenticación de usuario.
- `404 Not Found`: No se pudo encontrar el recurso solicitado.

---

# API Documentation

## Introduction

Welcome to the API documentation for the **Tupperware Auth** and **Wao Offers** services. This API allows you to manage authentication, user registration, and interact with offers.

### Base URLs
- **Auth Service**: `/auth`
- **Offers Service**: `/ofertas`

---

## Endpoints

### Auth Service

#### POST `/auth/login`

- **Description**: Authenticates a user using email and password.
- **Request Body**:
  ```json
  {
    "email": "user@example.com",
    "password": "password123"
  }
  ```
- **Responses**:
  - `200 OK`: Returns a JWT token if authentication is successful.
  - `401 Unauthorized`: Authentication failed.

#### POST `/auth/register`

- **Description**: Registers a new user in the system.
- **Request Body**:
  ```json
  {
    "email": "newuser@example.com",
    "password": "password123",
    "name": "User Name"
  }
  ```
- **Responses**:
  - `200 OK`: User registration successful.
  - `400 Bad Request`: Registration failed due to validation errors.

#### GET `/auth/perfil`

- **Description**: Retrieves profile information for the authenticated user.
- **Headers**:
  - `Authorization: Bearer <token>`: Required JWT token for authentication.
- **Responses**:
  - `200 OK`: Returns user profile data.
  - `401 Unauthorized`: No valid authentication provided.

---

### Offers Service

#### GET `/ofertas/all`

- **Description**: Retrieves a list of all available offers.
- **Responses**:
  - `200 OK`: Returns a list of offers.

#### GET `/ofertas/activas`

- **Description**: Retrieves a list of currently active offers.
- **Responses**:
  - `200 OK`: Returns a list of active offers.

#### POST `/ofertas/registrar`

- **Description**: Registers a user for an offer.
- **Request Body**:
  ```json
  {
    "contrato": "123456",
    "idOferta": 1,
    "cantidad": 2
  }
  ```
- **Responses**:
  - `200 OK`: Offer registration successful.
  - `400 Bad Request`: Registration failed due to invalid data.

#### PUT `/ofertas/actualizar`

- **Description**: Updates an existing offer registration.
- **Responses**:
  - `200 OK`: Update successful.

#### GET `/ofertas/mis-ofertas`

- **Description**: Retrieves the offers registered by the authenticated user.
- **Headers**:
  - `Authorization: Bearer <token>`: Required JWT token for authentication.
- **Query Parameters**:
  - `anio` (optional): Filter offers by year.
  - `campania` (optional): Filter offers by campaign.
- **Responses**:
  - `200 OK`: Returns the user's registered offers.
  - `401 Unauthorized`: No valid authentication provided.

---

## Authentication

All requests to protected endpoints require a JWT token. This can be obtained by logging in through the `/auth/login` endpoint.

- **Authorization Header**: `Authorization: Bearer <token>`

## Error Codes

- `400 Bad Request`: The request was malformed or contained invalid data.
- `401 Unauthorized`: The request requires user authentication.
- `404 Not Found`: The requested resource could not be found.

---


# ApiProjectJava
