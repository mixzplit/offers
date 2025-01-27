		
		//borramos el token viejo siempre que ingrese a esta pagina
		localStorage.removeItem("authToken");
		
		// Detectar la tecla Enter para iniciar sesion
		document.addEventListener('keydown', function (event) {
		    // Verifica si la tecla presionada es "Enter" (código 13)
		    if (event.key === 'Enter' || event.keyCode === 13) {
		        event.preventDefault(); // Evita el comportamiento predeterminado
		        validateLogin(); // Llama a la función del botón
		    }
		});
		
        export async function validateLogin() {
            var nroDoc = document.getElementById("nroDoc").value;
            var password = document.getElementById("password").value;
            var errorMessage = document.getElementById("error-message");
            errorMessage.textContent = "";
			
			if (!nroDoc || !password) {
			    errorMessage.textContent = "Por favor ingresá todos los campos";
			    return;
			}
			
			try {
			    // llamamos al endpoint para el login
			    const response = await fetch('./auth/login', {
			        method: 'POST',
			        headers: {
			            'Content-Type': 'application/json'
			        },
			        body: JSON.stringify({
			            emailDni: nroDoc,
			            password: password
			        })
			    });

			    const data = await response.json();

				//si hubo respuesta satisfactoria, redireccionamos
			    if (response.ok) {
					
					const authToken = data.token;

					// guardamos el token en localStorage
					localStorage.setItem("authToken", authToken);
			        
			        window.location.href = "solicitud-wao.html";
				} else if(response.status == 500){
					errorMessage.textContent = "El Nro de documento no existe.";
			    } else {
			        errorMessage.textContent = data.message || "Error al iniciar sesión";
			    }
			} catch (error) {
			    errorMessage.textContent = "Error al iniciar sesión, vuelva a intentarlo en unos minutos.";
			    console.error("Error:", error);
			}
        }