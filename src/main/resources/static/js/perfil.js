console.log("perfil.js cargado");

document.addEventListener("DOMContentLoaded", () => {
	console.log("DOM completamente cargado");
	
	if (!checkAuthentication()) {
        return; // Si no está autenticado, la función redirige al login
    }
    
	cargarHeader();
	
    const backButton = document.getElementById("back-button");

    if (backButton) {
        backButton.addEventListener("click", () => {
            window.location.href = "solicitud-wao.html";
        });
    }
});


function cargarHeader() {
	
    fetch('header.html')
        .then(response => {
            if (!response.ok) {
				console.log(`Error al cargar el header: ${response.statusText}`);
                throw new Error(`Error al cargar el header: ${response.statusText}`);
            }
            return response.text();
        })
        .then(html => {
			console.log("header cargado");
            document.getElementById('header-container').innerHTML = html;
            cargarDatosUsuario();
        })
        .catch(error => {
            console.error(error);
        });
}

function cargarDatosUsuario(){
	const userPerfil = JSON.parse(localStorage.getItem("userPerfil"));
	
	document.getElementById("user-name").textContent = capitalizarString(userPerfil.nombres) + " 👋";
	
        if (userPerfil) {
            document.getElementById("contrato-usuario").textContent = userPerfil.contrato;
            document.getElementById("perfil-usuario").textContent = userPerfil.nombrePerfil;
            document.getElementById("zona-usuario").textContent = userPerfil.zona;
            document.getElementById("division-usuario").textContent = userPerfil.division;
            document.getElementById("email-usuario").textContent = userPerfil.email;
            
        } else {
            console.error("No se encontraron datos de perfil en el localStorage.");
            alert("Error al cargar los datos del perfil.");
        }
}

/**
** Formatea un string para que quede "Palabra" en vez de "PALABRA".
**/
function capitalizarString(text){
	
	return text.toLowerCase().replace(/\b\w/g, (char) => char.toUpperCase());
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
** Funcion para el logout
**/
function logout() {
    
	// borramos el token del almacenamiento local
    localStorage.removeItem("authToken");
    localStorage.removeItem("userPerfil");
    
    //redireccionamos al login
    window.location.href = "login.html";
}
