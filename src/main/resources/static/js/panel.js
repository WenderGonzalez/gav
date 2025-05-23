function mostrarPanel(panelId) {
    const panels = document.querySelectorAll('.panel');
    panels.forEach(panel => {
        panel.classList.add('hidden');
    });

    const selectedPanel = document.getElementById(panelId);
    if (selectedPanel) {
        selectedPanel.classList.remove('hidden');

        // Llama a cargarMisViajes solo si se está mostrando ese panel
        if (panelId === "misViajes") {
            cargarMisViajes();
        }

        if (panelId === "miHistorial") {
            cargarHistorialViajes();
        }

    }

    const sidebar = document.getElementById('sidebar');
    if (window.innerWidth <= 768 && sidebar.classList.contains('active')) {
        sidebar.classList.remove('active');
    }
}


function toggleSidebar() {
    const sidebar = document.getElementById('sidebar');
    sidebar.classList.toggle('active');
}

document.getElementById('logoutBtn')?.addEventListener('click', function(e) {
    e.preventDefault(); // Previene cualquier acción por defecto

    Swal.fire({
        text: '¿Estás seguro que deseas salir?',
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Sí, cerrar',
        cancelButtonText: 'Cancelar',
        background: '#1a1a1a',
        color: '#fff',
        // Configuración de posicionamiento reforzada
        position: 'center',
        allowOutsideClick: false,
        allowEscapeKey: false,
        allowEnterKey: true,
        focusConfirm: true,
        customClass: {
            container: 'swal2-container-centered',
            popup: 'swal2-popup-centered'
        },
        showClass: {
            popup: 'swal2-noanimation',
            backdrop: 'swal2-noanimation'
        },
        hideClass: {
            popup: '',
            backdrop: ''
        }
    }).then((result) => {
        if (result.isConfirmed) {
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '/logout';

            // Agrega token CSRF si es necesario
            const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
            if (csrfToken) {
                const csrfInput = document.createElement('input');
                csrfInput.type = 'hidden';
                csrfInput.name = '_csrf';
                csrfInput.value = csrfToken;
                form.appendChild(csrfInput);
            }

            document.body.appendChild(form);
            form.submit();
        }
    });
});

