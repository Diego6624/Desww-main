$(document).ready(function() {
    var carrito = JSON.parse(localStorage.getItem('carrito')) || [];
    var totalAmount = 0;

    function calcularTotal() {
        totalAmount = carrito.reduce((sum, item) => sum + parseFloat(item.price) * item.cantidad, 0);
        return totalAmount.toFixed(2);
    }

    function mostrarNotificacion(mensaje, tipo) {
        Swal.fire({
            icon: tipo,
            title: mensaje,
            timer: 2000,
            showConfirmButton: false
        });
    }

    function actualizarContadorCarrito() {
        const totalItems = carrito.reduce((sum, item) => sum + item.cantidad, 0);
        $('.badge').text(totalItems);
    }

    function actualizarCarritoUI() {
        $('.shopping tbody').empty();
        
        if (carrito.length === 0) {
            $('.shopping tbody').append('<tr><td colspan="5" style="text-align: center; padding: 20px;">Tu carrito está vacío</td></tr>');
        } else {
            carrito.forEach(function(item) {
                var newRow = $('<tr>' +
                    '<td style="font-size: 11px;">' + item.product + '</td>' +
                    '<td style="font-size: 11px;">' + item.description + '</td>' +
                    '<td style="font-size: 11px;">S/' + item.price + '</td>' +
                    '<td><input type="number" class="cantidad" value="' + item.cantidad + '" data-product="' + item.product + '" min="1"></td>' +
                    '<td><button class="btnEliminar" data-product="' + item.product + '">×</button></td>' +
                    '</tr>');

                newRow.find('.cantidad').change(function() {
                    var productToUpdate = $(this).data('product');
                    var newCantidad = parseInt($(this).val());
                    if (newCantidad > 0) {
                        carrito = carrito.map(function(item) {
                            if (item.product === productToUpdate) {
                                item.cantidad = newCantidad;
                            }
                            return item;
                        });
                        localStorage.setItem('carrito', JSON.stringify(carrito));
                        mostrarNotificacion('Cantidad actualizada', 'success');
                        actualizarCarritoUI();
                        actualizarContadorCarrito();
                    }
                });

                newRow.find('.btnEliminar').click(function() {
                    var productToRemove = $(this).data('product');
                    carrito = carrito.filter(function(item) {
                        return item.product !== productToRemove;
                    });
                    localStorage.setItem('carrito', JSON.stringify(carrito));
                    mostrarNotificacion('Producto eliminado', 'success');
                    actualizarCarritoUI();
                    actualizarContadorCarrito();
                });

                $('.shopping tbody').append(newRow);
            });
            
            $('.shopping tbody').append('<tr style="font-weight: bold; border-top: 2px solid white;"><td>Total</td><td></td><td></td><td>S/' + calcularTotal() + '</td><td></td></tr>');
        }
    }

 

    // Agregar producto al carrito
    $('.product button').click(function(e) {
        e.preventDefault();
        var product = $(this).data('product');
        var description = $(this).data('description');
        var price = $(this).data('price');
        var codigo = $(this).data('codigo');

        var existingProduct = carrito.find(function(item) {
            return item.product === product;
        });

        if (existingProduct) {
            existingProduct.cantidad += 1;
        } else {
            carrito.push({
                product: product,
                description: description,
                price: price,
                codigo: codigo,
                cantidad: 1
            });
        }

        localStorage.setItem('carrito', JSON.stringify(carrito));
        mostrarNotificacion("Producto agregado al carrito", "success");
        actualizarCarritoUI();
        actualizarContadorCarrito();
    });

    // Botón pagar
    $('#btnPagar').click(function() {
        if (carrito.length === 0) {
            mostrarNotificacion('Tu carrito está vacío', 'info');
        } else {
            $('#modalPagoEntrega').fadeIn();
        }
    });

    // Cerrar modales
    $('.btnCerrarModal').click(function() {
        $(this).closest('.modal').fadeOut();
    });

    // Formulario de entrega
    $('#codForm').submit(function(e) {
        e.preventDefault();
        $('#modalPagoEntrega').fadeOut(100);
        setTimeout(function() {
            inicializarBotonPayPal();
            $('#modalOpcionesPago').fadeIn(300);
        }, 200);
    });

    // Abrir carrito
    $('.shopping-cart').click(function(e) {
        e.preventDefault();
        $('.wrapper-layer').fadeIn(300);
        setTimeout(function() {
            $('.wrapper-layer .layer').css({'transform':'translateX(0)'});
        }, 50);
    });

    // Cerrar carrito al hacer clic en el overlay
    $('.wrapper-layer').click(function(e) {
        if ($(e.target).is('.wrapper-layer')) {
            cerrarCarrito();
        }
    });

    // Prevenir que el clic dentro del carrito lo cierre
    $('.wrapper-layer .layer').click(function(e) {
        e.stopPropagation();
    });

    // Botón cerrar carrito
    $('.close-cart').click(function() {
        cerrarCarrito();
    });

    // Función para cerrar carrito
    function cerrarCarrito() {
        $('.wrapper-layer .layer').css({'transform':'translateX(100%)'});
        setTimeout(function() {
            $('.wrapper-layer').fadeOut(100);
        }, 300);
    }

    // Cerrar carrito con tecla Escape
    $(document).keydown(function(event) {
        if (event.key === "Escape") {
            cerrarCarrito();
        }
    });

    // Cerrar modales al hacer clic fuera
    $(document).click(function(event) {
        if ($(event.target).hasClass('modal')) {
            $(event.target).fadeOut();
        }
    });

    // Botón cerrar opciones de pago
    $('#btnCerrarOpcionesPago').click(function() {
        $('#modalOpcionesPago').fadeOut();
    });

    // Inicializar la UI del carrito
    actualizarCarritoUI();
    actualizarContadorCarrito();
});



//mercadopago
document.getElementById("btnPagarMercadoPago").addEventListener("click", async function () {
    try {
        // 1. Capturar datos del formulario
        const nombre = document.getElementById("state").value;
        const direccion = document.getElementById("address").value;
        const ciudad = document.getElementById("city").value;
        const codigoPostal = document.getElementById("zipcode").value;
        const pais = document.getElementById("country").value;
        const telefono = document.getElementById("phone").value;

        if (!nombre || !direccion || !ciudad || !codigoPostal || !pais || !telefono) {
            Swal.fire("Error", "Completa todos los campos obligatorios.", "error");
            return;
        }

        // 2. Obtener carrito desde localStorage
        const carrito = JSON.parse(localStorage.getItem("carrito")) || [];

        if (carrito.length === 0) {
            Swal.fire("Error", "Tu carrito está vacío.", "error");
            return;
        }

        // 3. Mapear productos al formato correcto para MercadoPago
        const items = carrito.map(producto => {
            const title = producto.product || producto.nombre || "Producto";
            const quantity = parseInt(producto.cantidad);
            const unit_price = parseFloat(producto.price);

            if (!title || isNaN(quantity) || isNaN(unit_price) || quantity <= 0 || unit_price <= 0) {
                throw new Error(`Item inválido: ${JSON.stringify(producto)}`);
            }

            return {
                title: title.toString(),
                description: (producto.description || "").toString(),
                quantity,
                unit_price,
                currency_id: "PEN"
            };
        });

        console.log("🛒 Items a enviar:", items);
        console.log(JSON.stringify(items));
        // 4. Enviar a la API Spring Boot
        const response = await fetch("http://localhost:8080/api/mercadopago/crear-preferencia", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(items)
        });

        // if (!response.ok) {
        //     const errorText = await response.text();
        //     console.error("❌ Error de respuesta del backend:", errorText);
        //     throw new Error(`HTTP error! status: ${response.status}`);
        // }

        const data = await response.json();
        console.log("✅ Respuesta del servidor:", data);

        // if (data && data.init_point) {
        //     // 5. Guardar datos de envío en localStorage
        //     localStorage.setItem("datosEnvio", JSON.stringify({
        //         nombre, direccion, ciudad, codigoPostal, pais, telefono
        //     }));

        //     // 6. Redirigir a MercadoPago
        //     window.location.href = data.init_point;
        // } else {
        //     Swal.fire("Error", "No se pudo crear la preferencia de pago.", "error");
        //     console.error("❌ Respuesta inválida:", data);
        // }

    } catch (error) {
        console.log(error);
        console.error("❌ Error completo:", error);
        Swal.fire("Error", "Ocurrió un problema al procesar el pago.", "error");
    }
});


//prueba
