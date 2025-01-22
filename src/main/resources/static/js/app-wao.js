console.log("app-wao.js cargado");

// Cargar el header dinámicamente

function cargarHeader() {
    fetch('header.html')
        .then(response => {
            if (!response.ok) {
                throw new Error(`Error al cargar el header: ${response.statusText}`);
            }
            return response.text();
        })
        .then(html => {
            document.getElementById('header-container').innerHTML = html;

            // Aquí puedes inicializar cualquier lógica adicional, por ejemplo:
            //cargarUsuarioInfo();
        })
        .catch(error => {
            console.error(error);
        });
}


	//si no hay problemas con la autenticacion, inicializo
	if(checkAuthentication()){
		
		inicializarPantalla();
		
		// Seleccionar el input
		const nroClienteInput = document.getElementById("nroClienteNbr");
		if(nroClienteInput){
			const handleInput = () => {
			    const value = nroClienteInput.value;
			    cargarInfoContratoIngresado(value);
			};
	
			// Asignar el evento al input
			nroClienteInput.addEventListener("change", handleInput);
		}else {
        	console.warn("El elemento nroClienteNbr no se encontró en el DOM.");
    	}
	}
	
	/**
	** Funcion que carga en pantalla la info del contrato ingresado
	**/
	async function cargarInfoContratoIngresado(contrato){
		
		try {
		    
			const authToken = localStorage.getItem("authToken");
			
			const url = `./revendedora/${contrato}`;
			
			//mandamos la solicitud
		    const response = await fetch(url, {
		        method: "GET",
		        headers: {
		            "Content-Type": "application/json",
		            "Authorization": `Bearer ${authToken}`,
		        },
		    });

		    // respuesta de la peticion
		    const data = await response.json();
			
			if(data.data != null && data.data.nombres != null && data.data.nombres.trim() != ""){
				document.getElementById("contrato-name").textContent = capitalizarString(data.data.nombres);
				document.getElementById("contrato-name").style.color = "";
			}else{
				document.getElementById("contrato-name").textContent = data.message;
				document.getElementById("contrato-name").style.color = "red";
			}

		} catch (error) {
		    console.error(error);
			alert("Se produjo un error al querer buscar el nombre del contrato ingresado.");
		}
	}
	
	/**
	** Formatea un string para que quede "Palabra" en vez de "PALABRA".
	**/
	function capitalizarString(text){
		
		return text.toLowerCase().replace(/\b\w/g, (char) => char.toUpperCase());
	}
	
	/**
	** Funcion para el logout
	**/
	function logout() {
	    
		// borramos el token del almacenamiento local
	    localStorage.removeItem("authToken");
	    
	    //redireccionamos al login
	    window.location.href = "login.html";
	}
	
	/**
	** Funcion que chequea la autenticacion, carga la info del usuario y busca las waos activas
	**/
	function checkAuthentication() {
		
		//obtenemos el token genrado
	    const authToken = localStorage.getItem("authToken");

		//redireccionamos en caso que no haya un token activo
	    if (!authToken) {
			alert("Su sesión ha caducado, vuelva a iniciar la misma.");
	        window.location.href = "login.html";
			return false;
	    }
		return true;
	}
	
	/**
	** Inicializamos los datos de la pantalla
	**/
	function inicializarPantalla(){
		cargarHeader();
		
		const authToken = localStorage.getItem("authToken");
		getInfoUsuario(authToken);
		getWAOS(authToken);
	}
	
	/**
	** Funcion que hace la reserva de la wao
	**/
	async function solicitarWAO(idOferta, cantidad){
		
		if(!checkIsAValidToken()){
			return;
		}
		
		//vaciamos los mensajes de error viejos
		cargarMensajeError(idOferta, "");
		
		//agarrar el numero de cliente de nroClienteNbr
		const nroCliente = document.getElementById("nroClienteNbr").value;
		
		if (!nroCliente) {
		    alert("Debe ingresar un número de cliente válido.");
		    return;
		}
		
		if(!cantidad || cantidad <= 0){
			cargarMensajeError(idOferta, "Debe solicitar al menos 1 unidad para la WAO.");
			return;
		}
		
		try {
		    
			const authToken = localStorage.getItem("authToken");
			
			//mandamos la solicitud
		    const response = await fetch("./ofertas/registrar", {
		        method: "POST",
		        headers: {
		            "Content-Type": "application/json",
		            "Authorization": `Bearer ${authToken}`,
		        },
				body: JSON.stringify({
				    idOferta: idOferta,
				    cantidad: parseInt(cantidad, 10),
				    contrato: nroCliente,
				}),
		    });

		    if (!response.ok) {
				const errorData = await response.json();
				const serverMessage = errorData.message || "Error desconocido al registrar la WAO.";
				cargarMensajeError(idOferta, serverMessage);
				throw new Error(serverMessage);
		    }

		    // respuesta de la peticion
		    const data = await response.json();
			
			alert("WAO solicitada exitosamente.");
			
			getWAOS(authToken);

		} catch (error) {
		    console.error(error);
			//alert("Error con la solicitud de la wao: " + error);
		}
	}
	
	/**
	** Funcion que trae el detalle de todas las solicitudes para esa WAO
	**/
	async function solicitudesDeLaWAO(idOferta){
		
		if(!checkIsAValidToken()){
			return;
		}
		
		//llamamos al endpoint que muestra las solicitudes para esa wao
		try {
		    
			const authToken = localStorage.getItem("authToken");
			
			const url = `./ofertas/detalle/${idOferta}`;
			
			//mandamos la solicitud
		    const response = await fetch(url, {
		        method: "GET",
		        headers: {
		            "Content-Type": "application/json",
		            "Authorization": `Bearer ${authToken}`,
		        },
		    });

		    if (!response.ok) {
				const errorData = await response.json();
				const serverMessage = errorData.message || "Error desconocido con las solicitudes de la WAO.";
				throw new Error(serverMessage);
		    }

			// respuesta de la peticion
			const data = await response.json();

			// validamos si la respuesta contiene el cuerpo esperado
			if (!data || !data.data || !Array.isArray(data.data)) {
			    throw new Error('Formato de respuesta inesperado.');
			}

			// Generar el modal dinamico
			const modalId = `modal-${idOferta}`;
			let modalElement = document.getElementById(modalId);

			if (!modalElement) {
			  const modalTemplate = document.getElementById("modal-template").content.cloneNode(true);
			  
			  modalTemplate.querySelector(".modal").id = modalId;
			  modalTemplate.querySelector(".modal-title").id = `label-${idOferta}`;
			  modalTemplate.querySelector(".modal-body table tbody").id = `details-${idOferta}`;

			  document.body.appendChild(modalTemplate);
			  modalElement = document.getElementById(modalId);
			}

			const tableBody = modalElement.querySelector("tbody");
			tableBody.innerHTML = ""; // Limpiar contenido previo

			data.data.forEach((solicitud) => {
			  const row = document.createElement("tr");
			  row.innerHTML = `
			    <td>${solicitud.contrato}</td>
			    <td>${solicitud.grupo}</td>
			    <td>${solicitud.nombre}</td>
			    <td>${solicitud.cantidadSolicitada}</td>
			  `;
			  tableBody.appendChild(row);
			});

			const modalInstance = new bootstrap.Modal(modalElement);
			modalInstance.show();
			
			// eliminamos el modal del DOM cuando se cierre
			modalElement.addEventListener('hidden.bs.modal', () => {
			    modalElement.remove();
				
				document.querySelectorAll('.modal-backdrop').forEach(backdrop => backdrop.remove());
			});
			

		} catch (error) {
		    console.error(error);
			alert("Error viendo las solicitudes de la wao: " + error);
		}
	}
	
	/**
	** Funcion que recupera el detalle del usuario logueado
	**/
	async function getInfoUsuario(authToken){
		try {
		    
			//obtenemos la info del usuario logueado
		    const response = await fetch("./auth/perfil", {
		        method: "GET",
		        headers: {
		            "Content-Type": "application/json",
		            "Authorization": `Bearer ${authToken}`,
		        },
		    });

		    if (!response.ok) {
				const errorData = await response.json();
				const serverMessage = errorData.message || "Error desconocido, No se pudo obtener la información del usuario.";
				throw new Error(serverMessage);
		    }

		    // respuesta de la peticion
		    const data = await response.json();
		    const userName = data.data.nombres;
			const idRol = data.data.idPerfil;
			const contrato = data.data.contrato;

		    // cargamos el nombre en el campo del navbar
		    document.getElementById("user-name").textContent = capitalizarString(userName) + " 👋";
		    
		    localStorage.setItem("userPerfil", JSON.stringify(data.data));
			
			const nroClienteInput = document.getElementById("nroClienteNbr");
			if(nroClienteInput){
				if(idRol=="1"){
					document.getElementById("nroClienteNbr").value = contrato;
					document.getElementById("nroClienteNbr").disabled = true;
					
					document.getElementById("contrato-name").textContent = capitalizarString(userName);
					
				}else{
					document.getElementById("nroClienteNbr").value = "";
					document.getElementById("nroClienteNbr").disabled = false;
				}
			}

		} catch (error) {
		    console.error(error);
		    window.location.href = "login.html";
		}
	}
	
	/**
	** Funcion que recupera el detalle del usuario logueado
	** cuando se accede desde el menu/ver perfil
	**/
	async function getInfoUsuarioMenu(){
        	window.location.href = "perfil.html";
	}
	
	/**
	** Funcion que trae todas las WAOs activas
	**/
	async function getWAOS(authToken){
		
		if(!checkIsAValidToken()){
			return;
		}
		
		try {
			
			//obtenemos las waos activas
		    const response = await fetch("./ofertas/activas", {
		        method: "GET",
		        headers: {
		            "Content-Type": "application/json",
		            "Authorization": `Bearer ${authToken}`,
		        },
		    });

		    if (!response.ok) {
				const errorData = await response.json();
				const serverMessage = errorData.message || "Error desconocido al obtener las ofertas activas.";
				throw new Error(serverMessage);
		    }
			
			const container = document.querySelector('.card-container');
			
			// respuesta de la peticion
			const data = await response.json();
			
			// validamos si la respuesta contiene el cuerpo esperado
			if (!data) {
			    throw new Error('Formato de respuesta inesperado.');
			}
			
			// Si no hay ofertas activas, mostramos el mensaje
			//(mejorar el endpoint a futuro para no tener que validar aca por codeStatus)
			if (response.status === 204 || !data.data || data.data.length === 0) {
			    const noOffersMessage = document.createElement("div");
			    noOffersMessage.textContent = "No hay Ofertas WAO activas por el momento.";
			    noOffersMessage.style.textAlign = "center";
			    noOffersMessage.style.fontSize = "1.2em";
			    noOffersMessage.style.color = "#555";
			    if(container){
			    	container.appendChild(noOffersMessage);
				}
			    return;
			}
			
			const template = document.getElementById('card-template').content;
			container.innerHTML = ''; // limpiamos ofertas anteriores

			// generamos las cards dinamicamente
			data.data.forEach((oferta) => {
			    const card = template.cloneNode(true);
				
				//id dinamico para los mensajes de error de cada card
				const errorMsg = card.querySelector(".error-message");
				errorMsg.id = `errorMsg-${oferta.idOferta}`;
				
			    card.querySelector(".card-title").textContent = `ID: ${oferta.idOferta} | Artículo: ${oferta.codigoArticulo} - ${oferta.descripcionArticulo}`;
				card.querySelector(".cantSolicitudes").textContent = `Solicitudes: ${oferta.cantidadSolicitudes}`;
			    card.querySelector(".fecha-fin").textContent = `Fecha fin oferta: ${new Date(oferta.fechaFin + "Z").toLocaleString("es-ES", { dateStyle: "short", timeStyle: "short", timeZone: "UTC" })}`;
				
				const btnSolicitar = card.querySelector('.solicitar-btn');
				const inputCantidad = card.querySelector('.cantidad');
				const btnSolicitudes = card.querySelector('.verSolicitudes-btn');
				
				if (oferta.cantidadSolicitudes == 0) {
				    btnSolicitudes.disabled = true;
				} else {
				    btnSolicitudes.disabled = false;
				}
			
					//logica del boton soliticar wao cuando se haga clic
					btnSolicitar.addEventListener('click', () => {
					    const cantidad = inputCantidad.value;
					    solicitarWAO(oferta.idOferta, cantidad);
					});
					
					//logica del boton ver solicitudes cuando se haga clic
					btnSolicitudes.dataset.bsTarget = `#modal-${oferta.idOferta}`;
					btnSolicitudes.addEventListener("click", () => {
					  solicitudesDeLaWAO(oferta.idOferta);
					});
				
				container.appendChild(card);
			});

		} catch (error) {
			console.error('Error:', error);
			alert('Hubo un problema al realizar la búsqueda de las WAOs: ' + error);
		}
	}
	
	/**
	** Funcion que chequea la expiracion del token
	**/
	async function checkIsAValidToken(){
		
		const authToken = localStorage.getItem("authToken");
		
		if (authToken) {
		    try {
		        // Decodifica la parte del payload del JWT
		        const payloadBase64 = authToken.split('.')[1];
		        const payload = JSON.parse(atob(payloadBase64));

		        if (payload.exp) {
		            // Convierte el tiempo UNIX a una fecha legible
		            const expirationDate = new Date(payload.exp * 1000);
		            
					const currentDate = new Date();
					
					//alert(expirationDate + " vs " + currentDate);

					if (expirationDate < currentDate) {
					    alert("Su sesión ha caducado, vuelva a iniciar la misma.");
						localStorage.removeItem("authToken");
						window.location.href = "login.html";
					}
		        }
		    } catch (error) {
		        console.error("Error al decodificar el token: ", error);
		    }
		} else {
		    window.location.href = "login.html";
		}
	}
	
	/**
	** Funcion generica para mostrar mensaje de error en la tarjeta
	**/
	function cargarMensajeError(idOferta, mensaje){
		
		const errorMessageId = `errorMsg-${idOferta}`;
		const errorMessageElement = document.getElementById(errorMessageId);
		
		if (errorMessageElement) {
		    errorMessageElement.textContent = mensaje;
		}
	}