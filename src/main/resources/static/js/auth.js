/**
** Funcion que chequea la autenticacion, carga la info del usuario y busca las waos activas
**/
export function checkAuthentication() {
	
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
export function logout() {
    
	// borramos el token del almacenamiento local
    localStorage.removeItem("authToken");
    
    //redireccionamos al login
    window.location.href = "login.html";
}


/**
** Funcion que recupera el detalle del usuario logueado
** cuando se accede desde el menu/ver perfil
** nota: esta funcion no deberia ir aqui
**/
export function getInfoUsuarioMenu(){
        	window.location.href = "perfil.html";
}

export function verOfertasSolicitadas(){
        	window.location.href = "ofertasSolicitadas.html";
}	