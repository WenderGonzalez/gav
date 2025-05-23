document.addEventListener("DOMContentLoaded", () => {
    cargarDestinos();
    cargarMisViajes();
    cargarHistorialViajes()
});

function cargarDestinos() {
    fetch("/cliente/destinos")
        .then((res) => res.json())
        .then((data) => {
            const container = document.getElementById("cardsDestinos");
            container.innerHTML = "";

            data.forEach((locacion) => {
                const card = document.createElement("div");
                card.className = "card-destino";

                card.innerHTML = `
                    <h3>${locacion.nombreLugar}</h3>
                    <p>${locacion.descripcion}</p>
                    <p><strong>Precio base:</strong> $${locacion.precio}</p>
                    <p><em>* Aumenta si hay más de 4 pasajeros</em></p>
                    <button onclick="solicitarViaje('${locacion.id}', '${locacion.precio}', '${locacion.nombreLugar}')">Solicitar viaje</button>
                `;

                container.appendChild(card);
            });
        })
        .catch((err) => {
            console.error("Error cargando destinos:", err);
            Swal.fire("Error", "No se pudieron cargar los destinos", "error");
        });
}
function solicitarViaje(destinoId, precioBase, nombreDestino) {
    Swal.fire({
        title: `¿Cuántos pasajeros para ${nombreDestino}?`,
        input: 'number',
        inputAttributes: {
            min: 1,
            max: 30,
            required: true
        },
        inputPlaceholder: 'Ej: 3',
        showCancelButton: true,
        confirmButtonText: 'Solicitar',
        cancelButtonText: 'Cancelar',
        preConfirm: (value) => {
            const cantidad = parseInt(value);
            if (isNaN(cantidad) || cantidad < 1 || cantidad > 30) {
                Swal.showValidationMessage("Cantidad inválida");
            }
            return cantidad;
        }
    }).then((result) => {
        if (result.isConfirmed) {
            const pasajeros = result.value;

            fetch("/cliente/viajes", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    destinoId,
                    origen: "Hotel Estelar Manzanillo",
                    cantidadPasajeros: pasajeros
                })
            })
            .then(res => {
                if (!res.ok) throw new Error("Error al solicitar viaje");
                return res.json();
            })
            .then(data => {
                Swal.fire("¡Listo!", "Viaje solicitado correctamente", "success");

            })
            .catch(err => {
                console.error(err);
                Swal.fire("Error", "No se pudo solicitar el viaje", "error");
            });
        }
    });
}

function cargarMisViajes() {
    fetch("/cliente/mis-viajes", {
        method: "GET",
        credentials: "include"
    })
    .then(res => {
        if (!res.ok) throw new Error("HTTP error " + res.status);
        return res.json();
    })
    .then(data => {
        const panel = document.getElementById("misViajes");
        panel.innerHTML = "<h2>Mis viajes actuales</h2>";

        if (data.length === 0) {
            panel.innerHTML += "<p>No tienes viajes activos en este momento.</p>";
            return;
        }

        data.forEach(viaje => {
            const div = document.createElement("div");
            div.className = "card-viaje";

            div.innerHTML = `
                <p><strong>Destino:</strong> ${viaje.destinoNombre}</p>
                <p><strong>Estado:</strong> ${viaje.estadoViaje}</p>
                <p><strong>Pasajeros:</strong> ${viaje.cantidadPasajeros}</p>
                <p><strong>Fecha de solicitud:</strong> ${new Date(viaje.fechaSolicitud).toLocaleString()}</p>
                ${viaje.fechaInicio ? `<p><strong>Inicio:</strong> ${new Date(viaje.fechaInicio).toLocaleString()}</p>` : ""}
                <p><strong>Precio final:</strong> $${viaje.precioFinal}</p>
            `;

            if (viaje.conductor) {
                div.innerHTML += `
                    <p><strong>Conductor:</strong> ${viaje.conductor.nombreCompleto}</p>
                    <p><strong>Teléfono:</strong> ${viaje.conductor.telefono}</p>
                `;
            } else {
                div.innerHTML += `<p><em>Conductor aún no asignado, se le asiganara en breve (5 mins max)</em></p>`;

            }

            if (viaje.estadoViaje === "SOLICITADO" || viaje.estadoViaje === "ASIGNADO") {
                const btnCancelar = document.createElement("button");
                btnCancelar.textContent = "Cancelar viaje";
                btnCancelar.className = "btn-cancelar";
                btnCancelar.onclick = () => cancelarViaje(viaje.id);
                div.appendChild(btnCancelar);
            }

            panel.appendChild(div);
        });
    })
    .catch(err => {
        console.error("Error cargando viajes:", err);
        Swal.fire("Error", "No se pudieron cargar tus viajes", "error");
    });
}


function cancelarViaje(viajeId) {
    Swal.fire({
        title: "¿Estás seguro que quieres cancelar este viaje?",
        icon: "warning",
        showCancelButton: true,
        confirmButtonText: "Sí, cancelar",
        cancelButtonText: "No",
    }).then((result) => {
        if (result.isConfirmed) {
            fetch(`/cliente/viajes/${viajeId}/cancelar`, {
                method: "PUT",
                credentials: "include"
            })
            .then(res => {
                if (!res.ok) throw new Error("Error al cancelar viaje");
                return res.json();
            })
            .then(data => {
                Swal.fire("Cancelado", data.mensaje, "success");
                cargarMisViajes(); // refrescar lista
            })
            .catch(err => {
                console.error(err);
                Swal.fire("Error", "No se pudo cancelar el viaje", "error");
            });
        }
    });
}

function cargarHistorialViajes() {
    fetch("/cliente/historial", {
        method: "GET",
        credentials: "include"
    })
    .then(res => {
        if (!res.ok) throw new Error("HTTP error " + res.status);
        return res.json();
    })
    .then(data => {
        const panel = document.getElementById("miHistorial");
        panel.innerHTML = "<h2>Historial de viajes</h2>";

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



