document.addEventListener("DOMContentLoaded", () => {
    cargarMisViajesConductor();
    cargarHistorialConductor()

});

function cargarMisViajesConductor() {
    fetch("/conductor/viajes", { method: "GET", credentials: "include" })
    .then(res => res.json())
    .then(data => {
        const panel = document.getElementById("misViajes");
        panel.innerHTML = "<h2>Mis Viajes Asignados</h2>";

        if (data.length === 0) {
            panel.innerHTML += "<p>No tienes viajes asignados o en curso actualmente.</p>";
            return;
        }

        data.forEach(viaje => {
            const div = document.createElement("div");
            div.className = "card-viaje";

            let contenido = `
                <p><strong>Destino:</strong> ${viaje.destinoNombre}</p>
                <p><strong>Pasajeros:</strong> ${viaje.cantidadPasajeros}</p>
                <p><strong>Estado:</strong> ${viaje.estadoViaje}</p>
                <p><strong>Precio:</strong> $${viaje.precioFinal}</p>
                <p><strong>Fecha de solicitud:</strong> ${new Date(viaje.fechaSolicitud).toLocaleString()}</p>
                <p><strong>Cliente:</strong> ${viaje.cliente.nombreCompleto}</p>
                <p><strong>Teléfono del cliente:</strong> ${viaje.cliente.telefono}</p>
            `;

            if (viaje.estadoViaje === "ASIGNADO") {
                contenido += `<button class="btn-iniciar" onclick="iniciarViaje('${viaje.id}')">Iniciar viaje</button>`;
            } else if (viaje.estadoViaje === "EN_CURSO") {
                contenido += `<button class="btn-finalizar" onclick="finalizarViaje('${viaje.id}')">Finalizar viaje</button>`;
            }

            div.innerHTML = contenido;
            panel.appendChild(div);
        });
    })
    .catch(err => {
        console.error("Error:", err);
        Swal.fire("Error", "No se pudo cargar los viajes del conductor", "error");
    });
}

function iniciarViaje(id) {
    fetch(`/conductor/viajes/${id}/iniciar`, { method: "POST", credentials: "include" })
    .then(res => {
        if (!res.ok) throw new Error("Error al iniciar el viaje");
        return res.json();
    })
    .then(() => {
        Swal.fire("¡Viaje iniciado!", "", "success");
        cargarMisViajesConductor();
    })
    .catch(err => {
        console.error(err);
        Swal.fire("Error", "No se pudo iniciar el viaje", "error");
    });
}

function finalizarViaje(id) {
    fetch(`/conductor/viajes/${id}/finalizar`, { method: "POST", credentials: "include" })
    .then(res => {
        if (!res.ok) throw new Error("Error al finalizar el viaje");
        return res.json();
    })
    .then(() => {
        Swal.fire("¡Viaje finalizado!", "", "success");
        cargarMisViajesConductor();
        cargarHistorialConductor(); // asegúrate de llamarla aquí si quieres recargar historial después de finalizar
    })
    .catch(err => {
        console.error(err);
        Swal.fire("Error", "No se pudo finalizar el viaje", "error");
    });
}

// 💡 ESTA FUNCIÓN DEBE ESTAR FUERA, NO DENTRO DE finalizarViaje
function cargarHistorialConductor() {
    fetch("/conductor/historial", {
        method: "GET",
        credentials: "include"
    })
    .then(res => {
        if (!res.ok) throw new Error("HTTP error " + res.status);
        return res.json();
    })
    .then(data => {
        const panel = document.getElementById("historial");
        panel.innerHTML = "<h2>Historial de viajes como conductor</h2>";

        if (data.length === 0) {
            panel.innerHTML += "<p>No tienes viajes finalizados ni cancelados.</p>";
            return;
        }

        data.forEach(viaje => {
            const div = document.createElement("div");
            div.className = "card-viaje";

            let contenido = `
                <p><strong>Destino:</strong> ${viaje.destinoNombre}</p>
                <p><strong>Estado:</strong> ${viaje.estadoViaje}</p>
                <p><strong>Pasajeros:</strong> ${viaje.cantidadPasajeros}</p>
                <p><strong>Fecha de solicitud:</strong> ${new Date(viaje.fechaSolicitud).toLocaleString()}</p>
            `;

            if (viaje.estadoViaje === "FINALIZADO") {
                if (viaje.fechaInicio) {
                    contenido += `<p><strong>Inicio:</strong> ${new Date(viaje.fechaInicio).toLocaleString()}</p>`;
                }
                if (viaje.fechaFinalizacion) {
                    contenido += `<p><strong>Finalización:</strong> ${new Date(viaje.fechaFinalizacion).toLocaleString()}</p>`;
                }
                if (viaje.duracionViaje) {
                    contenido += `<p><strong>Duración:</strong> ${viaje.duracionViaje} hrs</p>`;
                }
            } else if (viaje.estadoViaje === "CANCELADO") {
                if (viaje.fechaCancelacion) {
                    contenido += `<p><strong>Cancelación:</strong> ${new Date(viaje.fechaCancelacion).toLocaleString()}</p>`;
                }
            }


            contenido += `
                <p><strong>Cliente:</strong> ${viaje.cliente.nombreCompleto}</p>
                <p><strong>Teléfono del cliente:</strong> ${viaje.cliente.telefono}</p>
                <p><strong>Precio final:</strong> $${viaje.precioFinal}</p>
            `;

            div.innerHTML = contenido;
            panel.appendChild(div);
        });
    })
    .catch(err => {
        console.error("Error cargando historial del conductor:", err);
        Swal.fire("Error", "No se pudo cargar el historial de viajes", "error");
    });
}


