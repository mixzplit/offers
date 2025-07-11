console.log("header.js cargado");

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
        })
        .catch(error => {
            console.error(error);
        });
}
