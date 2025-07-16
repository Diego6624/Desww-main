document.getElementById("btnPagar").addEventListener("click", () => {
    const modal = new bootstrap.Modal(document.getElementById("modalPago"));
    modal.show();
});

document.getElementById("btnProcesarPago").addEventListener("click", async () => {
    try {
        const nombre = document.getElementById("nombreCliente").value.trim();
        const direccion = document.getElementById("direccion").value.trim();
        const telefono = document.getElementById("telefono").value.trim();

        const tarjeta = document.getElementById("numeroTarjeta").value.trim();
        const cci = document.getElementById("cci").value.trim();
        const titular = document.getElementById("titular").value.trim();
        const vencimiento = document.getElementById("vencimiento").value.trim();


        if (!nombre || !direccion || !telefono || !tarjeta || !cci || !titular || !vencimiento) {
            return Swal.fire("Error", "Por favor completa todos los campos", "error");
        }


        const carrito = JSON.parse(localStorage.getItem("carrito")) || [];
        if (carrito.length === 0) {
            return Swal.fire("Carrito vacío", "No hay productos para procesar", "error");
        }

        console.log("🛒 Carrito obtenido:", carrito);


        const productos = carrito.map(item => {
            console.log("📦 Procesando item:", item);
            return {
                idProducto: item.idProducto,
                idTalla: item.idTalla,
                title: item.nombre || item.title || item.product || "Producto sin nombre",
                quantity: parseInt(item.cantidad || item.quantity || 1),
                unit_price: parseFloat(item.precio || item.price || item.unit_price || 0)
            };
        });

        console.log("📋 Productos preparados:", productos);


        const total = productos.reduce((sum, p) => sum + (p.quantity * p.unit_price), 0);

        const body = {
            nombre,
            direccion,
            telefono,
            fecha: new Date().toLocaleDateString('es-PE'),
            productos,
            total: total.toFixed(2)
        };

        console.log("📤 Enviando datos:", body);


        const btnProcesar = document.getElementById("btnProcesarPago");
        const textoOriginal = btnProcesar.innerHTML;
        btnProcesar.innerHTML = '<span class="spinner-border spinner-border-sm" role="status"></span> Procesando...';
        btnProcesar.disabled = true;

        const response = await fetch("http://localhost:8080/api/pago/procesar", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/pdf"
            },
            body: JSON.stringify(body)
        });

        console.log("📡 Respuesta del servidor:", response.status, response.statusText);

        if (!response.ok) {
            const errorText = await response.text();
            console.error("❌ Error del servidor:", errorText);
            throw new Error(`Error ${response.status}: ${response.statusText}`);
        }


        const blob = await response.blob();
        console.log("📄 PDF generado, tamaño:", blob.size, "bytes");


        const datosResumen = {
            nombre,
            direccion,
            telefono,
            fecha: new Date().toLocaleDateString('es-PE'),
            productos: productos.map(p => ({
                nombre: p.title,
                cantidad: p.quantity,
                precio: p.unit_price.toFixed(2),
                subtotal: (p.quantity * p.unit_price).toFixed(2)
            })),
            total: total.toFixed(2)
        };


        localStorage.setItem("resumenPago", JSON.stringify(datosResumen));


        localStorage.removeItem("carrito");


        const modal = bootstrap.Modal.getInstance(document.getElementById("modalPago"));
        modal.hide();


        await Swal.fire({
            title: "¡Pago procesado!",
            text: "Tu comprobante ha sido descargado y serás redirigido al resumen.",
            icon: "success",
            timer: 2000
        });

        // Redirigir al resumen
        window.location.href = "/resumen";

    } catch (error) {
        console.error("❌ Error al procesar pago:", error);
        Swal.fire({
            title: "Error",
            text: "No se pudo procesar el pago: " + error.message,
            icon: "error"
        });
    } finally {

        const btnProcesar = document.getElementById("btnProcesarPago");
        if (btnProcesar) {
            btnProcesar.innerHTML = "Procesar Pago";
            btnProcesar.disabled = false;
        }
    }
});