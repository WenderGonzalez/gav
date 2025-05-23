document.addEventListener("DOMContentLoaded", () => {

    if (document.getElementById('solicitudes')) {
        cargarSolicitudesPendientes();
    }
    if (document.getElementById('historial')) {
         cargarHistorialGeneral()
    }
});

let viajeActualId = null;
let pasajerosActuales = 0;

function cargarSolicitudesPendientes() {
    fetch("/api/solicitudes-pendientes")
        .then(res => res.json())
        .then(data => {
            const container = document.getElementById("listaSolicitudes");
            container.innerHTML = "";

            if (data.length === 0) {
                container.innerHTML = "<p>No hay solicitudes pendientes</p>";
                return;
            }

            data.forEach(solicitud => {
                const card = document.createElement("div");
                card.className = "card-solicitud";

                card.innerHTML = `
                    <h3>${solicitud.destinoNombre}</h3>
                    <p><strong>Cliente:</strong> ${solicitud.clienteNombre}</p>
                    <p><strong>Pasajeros:</strong> ${solicitud.cantidadPasajeros}</p>
                    <p><strong>Solicitado:</strong> ${new Date(solicitud.fechaSolicitud).toLocaleString()}</p>
                    <button onclick="mostrarPanelAsignacion('${solicitud.id}', ${solicitud.cantidadPasajeros})">Asignar Conductor</button>
                `;

                container.appendChild(card);
            });
        })
        .catch(err => {
            console.error("Error cargando solicitudes:", err);
            Swal.fire("Error", "No se pudieron cargar las solicitudes", "error");
        });
}

function mostrarPanelAsignacion(viajeId, pasajeros) {
    viajeActualId = viajeId;
    pasajerosActuales = pasajeros;

    // Ocultar lista de solicitudes
    document.getElementById("listaSolicitudes").classList.add("hidden");

    // Mostrar info de la solicitud
    const solicitudInfo = document.getElementById("infoSolicitud");
    solicitudInfo.innerHTML = `
        <p><strong>Pasajeros a transportar:</strong> ${pasajeros}</p>
    `;

    // Cargar conductores disponibles
    cargarConductoresDisponibles(pasajeros);

    // Mostrar panel de asignación
    document.getElementById("panelAsignacion").classList.remove("hidden");
}

function cargarConductoresDisponibles(pasajeros) {
    fetch(`/api/conductores-disponibles?pasajeros=${pasajeros}`)
        .then(res => res.json())
        .then(data => {
            const container = document.getElementById("listaConductores");
            container.innerHTML = "";

            if (data.length === 0) {
                container.innerHTML = "<p>No hay conductores disponibles para esta capacidad</p>";
                return;
            }

            data.forEach(conductor => {
                const card = document.createElement("div");
                card.className = "card-conductor";

                card.innerHTML = `
                    <h4>${conductor.nombreCompleto}</h4>
                    <p><strong>Vehículo:</strong> ${conductor.automovil.marca} ${conductor.automovil.modelo}</p>
                    <p><strong>Capacidad:</strong> ${conductor.automovil.capacidadMaxima} pasajeros</p>
                    <p><strong>Licencia:</strong> ${conductor.tipoLicencia}</p>
                    <button onclick="asignarConductor('${conductor.id}')">Asignar</button>
                `;

                container.appendChild(card);
            });
        })
        .catch(err => {
            console.error("Error cargando conductores:", err);
            Swal.fire("Error", "No se pudieron cargar los conductores", "error");
        });
}

function cancelarAsignacion() {
    // Ocultar panel de asignación
    document.getElementById("panelAsignacion").classList.add("hidden");
    // Mostrar lista de solicitudes
    document.getElementById("listaSolicitudes").classList.remove("hidden");
}

function asignarConductor(conductorId) {
    const asignarBtn = event.target;
    asignarBtn.disabled = true;
    asignarBtn.textContent = "Asignando...";

    fetch("/api/asignar-conductor", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            viajeId: viajeActualId,
            conductorId: conductorId
        })
    })
    .then(res => {
        if (!res.ok) throw new Error("Error al asignar conductor");
        return res.json();
    })
    .then(data => {
        Swal.fire("¡Asignado!", "Conductor asignado correctamente", "success");
        cancelarAsignacion();
        cargarSolicitudesPendientes();
    })
    .catch(err => {
        console.error(err);
        Swal.fire("Error", "No se pudo asignar el conductor", "error");
    })
    .finally(() => {
        asignarBtn.disabled = false;
        asignarBtn.textContent = "Asignar";
    });
}

function cargarHistorialGeneral() {
    fetch("/api/historial", {
        method: "GET",
        credentials: "include"
    })
    .then(res => {
        if (!res.ok) throw new Error("HTTP error " + res.status);
        return res.json();
    })
    .then(data => {
        const panel = document.getElementById("historial");
        panel.innerHTML = "<h2>Historial de viajes</h2>";

        if (data.length === 0) {
            panel.innerHTML += "<p>No hay viajes finalizados ni cancelados.</p>";
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
                    contenido += `<p><strong>Duración:</strong> ${viaje.duracionViaje}</p>`;
                }
            } else if (viaje.estadoViaje === "CANCELADO") {
                if (viaje.fechaCancelacion) {
                    contenido += `<p><strong>Cancelación:</strong> ${new Date(viaje.fechaCancelacion).toLocaleString()}</p>`;
                }
            }

            contenido += `<p><strong>Precio final:</strong> $${viaje.precioFinal}</p>`;

            if (viaje.conductor) {
                contenido += `
                    <p><strong>Conductor:</strong> ${viaje.conductor.nombreCompleto}</p>
                    <p><strong>Teléfono:</strong> ${viaje.conductor.telefono}</p>
                `;
            }

            div.innerHTML = contenido;
            panel.appendChild(div);
        });
    })
    .catch(err => {
        console.error("Error cargando historial:", err);
        Swal.fire("Error", "No se pudo cargar el historial de viajes", "error");
    });
}



