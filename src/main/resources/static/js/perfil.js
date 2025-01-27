console.log("perfil.js cargado");
import { capitalizarString } from './utils.js';
import { checkAuthentication, logout, getInfoUsuarioMenu, verOfertasSolicitadas } from './auth.js';

document.addEventListener("DOMContentLoaded", () => {
	console.log("DOM completamente cargado");
	
	if (!checkAuthentication()) {
        return; // Si no está autenticado, la función redirige al login
    }
    
	cargarHeader();
	cargarModalLogout();
	
    const backButton = document.getElementById("back-button");

    if (backButton) {
        backButton.addEventListener("click", () => {
            window.location.href = "solicitud-wao.html";
        });
    }
});

window.logout = logout;
window.getInfoUsuarioMenu = getInfoUsuarioMenu;
window.verOfertasSolicitadas = verOfertasSolicitadas;

function cargarHeader() {
	
    fetch('./common/header.html')
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

function cargarModalLogout (){
	fetch('./common/logout-modal.html')
        .then(response => response.text())
        .then(data => {
            document.getElementById('modal-cierre-sesion').innerHTML = data;
        })
        .catch(error => console.error('Error al cargar el modal:', error));
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
            document.getElementById("grupo-um-usuario").textContent = userPerfil.grupoUM;
            
        } else {
            console.error("No se encontraron datos de perfil en el localStorage.");
            alert("Error al cargar los datos del perfil.");
        }
}

