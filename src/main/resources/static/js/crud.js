// ------------------ UTILIDADES ------------------
function mostrarMensaje(id, mensaje, esError = true) {
    const div = document.getElementById(id);
    if (!div) {
        console.error(`Elemento con ID ${id} no encontrado`);
        return;
    }

    // Ocultar todos los mensajes en el mismo panel
    const panel = div.closest('.panel');
    if (panel) {
        panel.querySelectorAll('[id^="mensaje-"]').forEach(msg => {
            msg.style.display = "none";
        });
    }

    // Mostrar el mensaje actual
    div.innerText = mensaje;
    div.style.display = "block";
    div.style.color = esError ? "red" : "green";

    // Ocultar después de 3 segundos
    setTimeout(() => {
        div.style.display = "none";
    }, 3000);
}

// ------------------ VEHÍCULOS ------------------

document.getElementById("formRegistrarVehiculo").addEventListener("submit", function (e) {
    e.preventDefault();

    const datosVehiculo = {
        marca: document.getElementById("marca").value,
        modelo: document.getElementById("modelo").value,
        placa: document.getElementById("placa").value,
        capacidadMaxima: parseInt(document.getElementById("capacidad").value),
        categoria: document.getElementById("categoria").value
    };

    fetch("/api/vehiculos", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(datosVehiculo)
    })
    .then(res => res.json())
    .then(data => {
        if (data.error) {
            mostrarMensaje("mensaje-error-registro-vehiculo", data.error, true);
        } else {
            mostrarMensaje("mensaje-exito-registro-vehiculo", "Vehículo registrado exitosamente", false);
            cargarVehiculos();
            cargarVehiculosParaSelect();
            document.getElementById("formRegistrarVehiculo").reset();
        }
    })
    .catch(() => mostrarMensaje("mensaje-error-registro-vehiculo", "Error al registrar vehículo", true));
});

function cargarVehiculos() {
    fetch("/api/vehiculos")
    .then(res => res.json())
    .then(vehiculos => {
        const tbody = document.getElementById("vehiculos-tbody");
        tbody.innerHTML = "";
        vehiculos.forEach(v => {
            const fila = `
                <tr>
                    <td>${v.marca}</td>
                    <td>${v.modelo}</td>
                    <td>${v.capacidadMaxima}</td>
                    <td>${v.placa}</td>
                    <td>${v.categoria}</td>
                    <td>
                        <button onclick="eliminarVehiculo('${v.id}')">Eliminar</button>
                        <button onclick="editarVehiculo('${v.id}')">Editar</button>
                    </td>
                </tr>`;
            tbody.innerHTML += fila;
        });
    });
}

function eliminarVehiculo(id) {
    fetch(`/api/vehiculos/${id}`, { method: "DELETE" })
        .then(res => {
            if (!res.ok) {
                return res.json().then(data => {
                    throw new Error(data.error || "Error al eliminar vehículo");
                });
            }
            return res.text();
        })
        .then(() => {
            mostrarMensaje("mensaje-exito-registro-vehiculo", "Vehículo eliminado exitosamente", false);
            cargarVehiculos();
        })
        .catch(err => mostrarMensaje("mensaje-error-registro-vehiculo", err.message, true));
}

function editarVehiculo(id) {
    fetch(`/api/vehiculos/${id}`)
    .then(res => res.json())
    .then(vehiculo => {
        document.getElementById("editarMarca").value = vehiculo.marca;
        document.getElementById("editarModelo").value = vehiculo.modelo;
        document.getElementById("editarPlaca").value = vehiculo.placa;
        document.getElementById("editarCapacidad").value = vehiculo.capacidadMaxima;
        document.getElementById("editarCategoria").value = vehiculo.categoria;

        document.getElementById("formEditarVehiculo").onsubmit = function(e) {
            e.preventDefault();
            actualizarVehiculo(id);
        };

        mostrarPanel('editarVehiculo');
    });
}

function actualizarVehiculo(id) {
    const datosVehiculo = {
        marca: document.getElementById("editarMarca").value,
        modelo: document.getElementById("editarModelo").value,
        placa: document.getElementById("editarPlaca").value,
        capacidadMaxima: parseInt(document.getElementById("editarCapacidad").value),
        categoria: document.getElementById("editarCategoria").value
    };

    fetch(`/api/vehiculos/${id}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(datosVehiculo)
        })
        .then(res => {
            if (!res.ok) return res.json().then(err => Promise.reject(err));
            return res.json();
        })
        .then(data => {
            // 1. Mostrar mensaje en el panel actual (edición)
            mostrarMensaje("mensaje-exito-editar-vehiculo", "Vehículo actualizado exitosamente", false);

            // 2. Esperar 1.5 segundos antes de cambiar de panel
            setTimeout(() => {
                cargarVehiculos();
                cargarVehiculosParaSelect();
                document.getElementById("formEditarVehiculo").reset();
                mostrarPanel('vehiculos'); // Cambiar al panel de lista
            }, 1500);
        })
        .catch(error => {
            const mensajeError = error.message || error.error || "Error al actualizar vehículo";
            mostrarMensaje("mensaje-error-editar-vehiculo", mensajeError, true);
        });
}

function cargarVehiculosParaSelect() {
    fetch("/api/vehiculos")
    .then(res => res.json())
    .then(vehiculos => {
        const select = document.getElementById("automovil");
        if (!select) return;

        select.innerHTML = '<option value="">Seleccione un vehículo</option>';

        vehiculos.forEach(v => {
            const option = document.createElement("option");
            option.value = v.id;
            option.textContent = `${v.marca} ${v.modelo} - ${v.placa}`;
            select.appendChild(option);
        });
    });
}

// ------------------ CONDUCTORES ------------------

function registrarConductor() {
    const datosConductor = {
        nombreCompleto: document.getElementById("nombreCompleto").value,
        apellidosCompletos: document.getElementById("apellidosCompletos").value,
        nombreUsuario: document.getElementById("nombreUsuario").value,
        contrasena: document.getElementById("contrasena").value,
        email: document.getElementById("email").value,
        fechaNacimiento: document.getElementById("fechaNacimiento").value,
        numeroDocumento: document.getElementById("numeroDocumento").value,
        tipoDocumento: document.getElementById("tipoDocumento").value,
        telefono: document.getElementById("telefono").value,
        sexo: document.getElementById("sexo").value,
        licencia: document.getElementById("licencia").value,
        tipoLicencia: document.getElementById("tipoLicencia").value,
        automovil: document.getElementById("automovil").value
                ? { id: document.getElementById("automovil").value }
                : null
    };

    fetch("/api/conductores", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(datosConductor)
    })
    .then(res => res.json())
    .then(data => {
        if (data.error) {
            mostrarMensaje("mensaje-error-registro-conductor", data.error, true);
        } else {
            mostrarMensaje("mensaje-exito-registro-conductor", "Conductor registrado exitosamente", false);
            cargarConductores();
            document.getElementById("formRegistrarConductor").reset();
        }
    })
    .catch(() => mostrarMensaje("mensaje-error-registro-conductor", "Error al registrar conductor", true));
}

function cargarConductores() {
    fetch("/api/conductores")
    .then(res => res.json())
    .then(conductores => {
        const tbody = document.getElementById("conductores-tbody");
        tbody.innerHTML = "";

        conductores.forEach(c => {
            const fila = document.createElement("tr");

            fila.innerHTML = `
                <td>${c.nombreCompleto || 'N/A'}</td>
                <td>${c.apellidosCompletos || 'N/A'}</td>
                <td>${c.numeroDocumento || 'N/A'}</td>
                <td>${c.telefono || 'N/A'}</td>
                <td>${c.email || 'N/A'}</td>
                <td>${c.licencia || 'N/A'}</td>
                <td>${c.tipoLicencia || 'N/A'}</td>
                <td>${c.automovil?.placa || 'N/A'}</td>
                <td>${c.disponibilidad ? 'Si' : 'No'}</td>
                <td class="acciones">
                    <button onclick="eliminarConductor('${c.id}')">Eliminar</button>
                    <button onclick="editarConductor('${c.id}')">Editar</button>
                </td>
            `;

            tbody.appendChild(fila);
        });
    })
    .catch(error => {
        console.error("Error al cargar conductores:", error);
        mostrarMensaje("mensaje-error-registro-conductor", "Error al cargar lista de conductores", true);
    });
}

function cargarVehiculosParaSelectEdicion() {
    return fetch("/api/vehiculos")
        .then(res => res.json())
        .then(vehiculos => {
            const select = document.getElementById("editarVehiculoAsignado");
            if (!select) return;

            // Guardar el valor actual seleccionado
            const currentValue = select.value;

            // Limpiar y repoblar el select
            select.innerHTML = '<option value="">Seleccione un vehículo</option>';

            vehiculos.forEach(v => {
                const option = document.createElement("option");
                option.value = v.id;
                option.textContent = `${v.marca} ${v.modelo} - ${v.placa}`;
                select.appendChild(option);
            });

            // Restaurar el valor seleccionado si existe
            if (currentValue) {
                select.value = currentValue;
            }
        });
}

function eliminarConductor(id) {
    fetch(`/api/conductores/${id}`, { method: "DELETE" })
        .then(res => {
            if (!res.ok) {
                return res.json().then(data => {
                    throw new Error(data.error || "Error al eliminar conductor");
                });
            }
            return res.text();
        })
        .then(() => {
            mostrarMensaje("mensaje-exito-registro-conductor", "Conductor eliminado exitosamente", false);
            cargarConductores();
        })
        .catch(err => mostrarMensaje("mensaje-error-registro-conductor", err.message, true));
}

function editarConductor(id) {
    // Primero cargar los vehículos disponibles
    cargarVehiculosParaSelectEdicion().then(() => {
        // Luego cargar los datos del conductor
        fetch(`/api/conductores/${id}`)
            .then(res => res.json())
            .then(conductor => {
                const fechaNacimiento = conductor.fechaNacimiento ?
                    new Date(conductor.fechaNacimiento).toISOString().split('T')[0] :
                    '';

                // Rellenar los campos del formulario
                document.getElementById("editarConductorId").value = conductor.id;
                document.getElementById("editarNombreCompleto").value = conductor.nombreCompleto;
                document.getElementById("editarApellidosCompletos").value = conductor.apellidosCompletos;
                document.getElementById("editarNombreUsuario").value = conductor.nombreUsuario;
                document.getElementById("editarEmail").value = conductor.email;
                document.getElementById("editarFechaNacimiento").value = fechaNacimiento;
                document.getElementById("editarNumeroDocumento").value = conductor.numeroDocumento;
                document.getElementById("editarTipoDocumento").value = conductor.tipoDocumento;
                document.getElementById("editarTelefono").value = conductor.telefono;
                document.getElementById("editarSexo").value = conductor.sexo;
                document.getElementById("editarLicencia").value = conductor.licencia;
                document.getElementById("editarTipoLicencia").value = conductor.tipoLicencia;
                document.getElementById("editarVehiculoAsignado").value = conductor.automovil?.id || '';

                // Configurar el evento submit
                document.getElementById("formEditarConductor").onsubmit = function(e) {
                    e.preventDefault();
                    actualizarConductor(id);
                };

                mostrarPanel('editarConductor');
            });
    });
}

function actualizarConductor(id) {
    const datosConductor = {
        nombreCompleto: document.getElementById("editarNombreCompleto").value,
        apellidosCompletos: document.getElementById("editarApellidosCompletos").value,
        nombreUsuario: document.getElementById("editarNombreUsuario").value,
        email: document.getElementById("editarEmail").value,
        fechaNacimiento: document.getElementById("editarFechaNacimiento").value,
        numeroDocumento: document.getElementById("editarNumeroDocumento").value,
        tipoDocumento: document.getElementById("editarTipoDocumento").value,
        telefono: document.getElementById("editarTelefono").value,
        sexo: document.getElementById("editarSexo").value,
        licencia: document.getElementById("editarLicencia").value,
        tipoLicencia: document.getElementById("editarTipoLicencia").value,
        automovil: document.getElementById("editarVehiculoAsignado").value
            ? { id: document.getElementById("editarVehiculoAsignado").value }
            : null
    };

    fetch(`/api/conductores/${id}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(datosConductor)
        })
        .then(res => {
            if (!res.ok) return res.json().then(err => Promise.reject(err));
            return res.json();
        })
        .then(data => {
            // 1. Mostrar mensaje en el panel actual (edición)
            mostrarMensaje("mensaje-exito-editar-conductor", "Conductor actualizado exitosamente", false);

            // 2. Esperar 1.5 segundos antes de cambiar de panel
            setTimeout(() => {
                cargarConductores();
                document.getElementById("formEditarConductor").reset();
                mostrarPanel('conductores'); // Cambiar al panel de lista
            }, 1500);
        })
        .catch(error => {
            const mensajeError = error.message || error.error || "Error al actualizar conductor";
            mostrarMensaje("mensaje-error-editar-conductor", mensajeError, true);
        });
}


// ------------------ LOCACIONES ------------------

function registrarLocacion() {
    const datosLocacion = {
        nombreLugar: document.getElementById("nombreLugar").value,
        descripcion: document.getElementById("descripcion").value,
        latitud: parseFloat(document.getElementById("latitud").value),
        longitud: parseFloat(document.getElementById("longitud").value),
        precio: document.getElementById("precio").value
            ? parseFloat(document.getElementById("precio").value)
            : null
    };

    fetch("/api/locaciones", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(datosLocacion)
    })
    .then(res => {
        if (!res.ok) return res.json().then(err => { throw new Error(err.error || "Error al registrar locación"); });
        return res.json();
    })
    .then(data => {
        mostrarMensaje("mensaje-exito-registro-locacion", "Locación registrada exitosamente", false);
        cargarLocaciones();
        document.getElementById("formRegistrarLocacion").reset();
    })
    .catch(err => mostrarMensaje("mensaje-error-registro-locacion", err.message, true));
}

// Cargar locaciones en tabla
function cargarLocaciones() {
  fetch("/api/locaciones")
    .then(res => res.json())
    .then(locaciones => {
      const tbody = document.getElementById("locaciones-tbody");
      tbody.innerHTML = "";
      locaciones.forEach(l => {
        const fila = `
          <tr>
            <td>${l.nombreLugar}</td>
            <td>${l.descripcion || 'N/A'}</td>
            <td>(${l.latitud}, ${l.longitud})</td>
            <td>${l.precio ? `$${l.precio}` : 'N/A'}</td>
            <td>
              <button onclick="eliminarLocacion(${l.id})">Eliminar</button>
              <button onclick="editarLocacion(${l.id})">Editar</button>
            </td>
          </tr>`;
        tbody.innerHTML += fila;
      });
    });
}

// Eliminar locación
function eliminarLocacion(id) {
  fetch(`/api/locaciones/${id}`, { method: "DELETE" })
    .then(res => {
      if (!res.ok) throw new Error("Error al eliminar locación");
      cargarLocaciones();
      mostrarMensaje("mensaje-exito-registro-locacion", "Locación eliminada exitosamente", false);
    })
    .catch(err => mostrarMensaje("mensaje-error-registro-locacion", err.message, true));
}

// Función para cargar datos en el formulario de edición
function editarLocacion(id) {
  fetch(`/api/locaciones/${id}`)
    .then(res => res.json())
    .then(locacion => {
      // Llenar formulario con los datos
      document.getElementById("editarLocacionId").value = locacion.id;
      document.getElementById("editarNombreLugar").value = locacion.nombreLugar;
      document.getElementById("editarDescripcion").value = locacion.descripcion || '';
      document.getElementById("editarLatitud").value = locacion.latitud;
      document.getElementById("editarLongitud").value = locacion.longitud;
      document.getElementById("editarPrecio").value = locacion.precio || '';

      // Configurar submit
      document.getElementById("formEditarLocacion").onsubmit = function(e) {
        e.preventDefault();
        actualizarLocacion(id);
      };

      mostrarPanel('editarLocacion');
    });
}

// Función para enviar la actualización
function actualizarLocacion(id) {
  const datosLocacion = {
    nombreLugar: document.getElementById("editarNombreLugar").value,
    descripcion: document.getElementById("editarDescripcion").value,
    latitud: parseFloat(document.getElementById("editarLatitud").value),
    longitud: parseFloat(document.getElementById("editarLongitud").value),
    precio: document.getElementById("editarPrecio").value
      ? parseFloat(document.getElementById("editarPrecio").value)
      : null
  };

  fetch(`/api/locaciones/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(datosLocacion)
  })
  .then(res => {
    if (!res.ok) return res.json().then(err => Promise.reject(err));
    return res.json();
  })
  .then(data => {
    mostrarMensaje("mensaje-exito-editar-locacion", "Locación actualizada exitosamente", false);
    setTimeout(() => {
      cargarLocaciones();
      document.getElementById("formEditarLocacion").reset();
      mostrarPanel('locaciones');
    }, 1500);
  })
  .catch(error => {
    const mensajeError = error.message || error.error || "Error al actualizar locación";
    mostrarMensaje("mensaje-error-editar-locacion", mensajeError, true);
  });
}


// ------------------ INICIO ------------------

document.addEventListener("DOMContentLoaded", function() {
    cargarVehiculos();
    cargarVehiculosParaSelect() ;
    cargarVehiculosParaSelectEdicion();
    cargarConductores();
    cargarLocaciones();

    // Asignar event listeners
    const formRegistrarConductor = document.getElementById("formRegistrarConductor");
    if (formRegistrarConductor) {
        formRegistrarConductor.addEventListener("submit", function(e) {
            e.preventDefault();
            registrarConductor();
        });
    }

    const formRegistrarLocacion = document.getElementById("formRegistrarLocacion");
        if (formRegistrarLocacion) {
            formRegistrarLocacion.addEventListener("submit", function(e) {
                e.preventDefault();
                registrarLocacion();
            });
        }
});