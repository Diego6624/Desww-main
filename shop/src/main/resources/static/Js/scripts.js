$(document).ready(function () {
    fetch("/api/auth/user")
        .then(response => {
            if (response.ok) {
                return response.text();
            } else {
                throw new Error("No autenticado");
            }
        })
        .then(email => {
            localStorage.setItem("authUser", email);
        })
        .catch(() => {
            localStorage.removeItem("authUser");
        });

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
            carrito.forEach(function (item) {
                var newRow = $('<tr>' +
                    '<td style="font-size: 11px;">' + item.product + '</td>' +
                    '<td style="font-size: 11px;">' + item.description + '</td>' +
                    '<td style="font-size: 11px;">S/' + item.price + '</td>' +
                    '<td><input type="number" class="cantidad" value="' + item.cantidad + '" data-product="' + item.product + '" min="1"></td>' +
                    '<td><button class="btnEliminar" data-product="' + item.product + '">×</button></td>' +
                    '</tr>');

                newRow.find('.cantidad').change(function () {
                    var productToUpdate = $(this).data('product');
                    var newCantidad = parseInt($(this).val());
                    if (newCantidad > 0) {
                        carrito = carrito.map(function (item) {
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

                newRow.find('.btnEliminar').click(function () {
                    var productToRemove = $(this).data('product');
                    carrito = carrito.filter(function (item) {
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

    function inicializarBotonPayPal() {
        if ($('#paypal-button-container').children().length === 0) {
            paypal.Buttons({
                createOrder: function (data, actions) {
                    return actions.order.create({
                        purchase_units: [{
                            amount: {
                                value: calcularTotal(),
                                currency_code: 'USD'
                            }
                        }]
                    });
                },
                onApprove: function (data, actions) {
                    return actions.order.capture().then(function (details) {
                        Swal.fire({
                            icon: 'success',
                            title: 'Pago realizado',
                            text: 'Tu pago con PayPal se ha completado con éxito.'
                        });
                        $('#modalOpcionesPago').fadeOut();
                        carrito = [];
                        localStorage.removeItem('carrito');
                        actualizarCarritoUI();
                        actualizarContadorCarrito();
                    });
                },
                onError: function (err) {
                    Swal.fire({
                        icon: 'error',
                        title: 'Error en el pago',
                        text: 'Ocurrió un error procesando tu pago.'
                    });
                },
                onCancel: function (data) {
                    Swal.fire({
                        icon: 'info',
                        title: 'Pago cancelado',
                        text: 'Tu pago ha sido cancelado.'
                    });
                }
            }).render('#paypal-button-container');
        }
    }

    // Función para modificar cantidad
    window.modificarCantidad = function (cambio) {
        var cantidadInput = document.getElementById('cantidad');
        var cantidadActual = parseInt(cantidadInput.value);
        var nuevaCantidad = cantidadActual + cambio;

        if (nuevaCantidad >= 1) {
            cantidadInput.value = nuevaCantidad;

            // Verificar stock disponible
            var tallaSeleccionada = document.getElementById('tallaSeleccionada');
            if (tallaSeleccionada.value) {
                var opcionSeleccionada = tallaSeleccionada.options[tallaSeleccionada.selectedIndex];
                var stockDisponible = parseInt(opcionSeleccionada.getAttribute('data-stock'));

                if (nuevaCantidad > stockDisponible) {
                    cantidadInput.value = stockDisponible;
                    mostrarNotificacion('Stock máximo: ' + stockDisponible, 'warning');
                }
            }
        }
    };

    // Función para agregar al carrito específica del detalle
    window.agregarAlCarrito = function () {
        const usuario = localStorage.getItem("authUser");
        if (!usuario) {
            Swal.fire({
                title: "Debes iniciar sesión",
                text: "Para agregar productos al carrito, por favor inicia sesión o regístrate.",
                icon: "info",
                showCancelButton: true,
                confirmButtonText: "Iniciar sesión",
                cancelButtonText: "Cancelar"
            }).then((result) => {
                if (result.isConfirmed) {
                    window.location.href = "/login"; // Asegúrate de que esta ruta exista en tu app
                }
            });
            return;
        }
        const tallaSelect = document.getElementById('tallaSeleccionada');
        const cantidadInput = document.getElementById('cantidad');
        const nombreProduct = document.getElementById('nombreProducto');
        const descripcionProduct = document.getElementById('descripcionProducto');
        const precioProducto = document.getElementById('precioProducto');

        // Validar que se haya seleccionado una talla
        if (!tallaSelect || !cantidadInput || !nombreProduct || !descripcionProduct || !precioProducto) {
            console.warn("🔍 Elementos del DOM no encontrados. ¿Estás en la vista correcta?");
            return;
        }

        var cantidad = parseInt(cantidadInput.value);
        var opcionSeleccionada = tallaSelect.options[tallaSelect.selectedIndex];
        var stockDisponible = parseInt(opcionSeleccionada.getAttribute('data-stock'));

        // Validar stock
        if (cantidad > stockDisponible) {
            mostrarNotificacion('Stock insuficiente. Disponible: ' + stockDisponible, 'error');
            return;
        }

        // Obtener datos del producto
        var nombreProducto = nombreProduct.textContent;
        var descripcionProducto = descripcionProduct.textContent;
        var precioTexto = precioProducto.textContent;
        var precio = precioTexto.replace('S/ ', '');
        var nombreTalla = opcionSeleccionada.getAttribute('data-nombre-talla');

        // Crear identificador único con talla
        var productId = nombreProducto + ' - ' + nombreTalla;
        var descripcionCompleta = descripcionProducto + ' (Talla: ' + nombreTalla + ')';

        // Buscar si ya existe el producto con la misma talla
        var existingProduct = carrito.find(function (item) {
            return item.product === productId;
        });

        if (existingProduct) {
            var nuevaCantidad = existingProduct.cantidad + cantidad;
            if (nuevaCantidad <= stockDisponible) {
                existingProduct.cantidad = nuevaCantidad;
            } else {
                mostrarNotificacion('No se puede agregar más. Stock máximo: ' + stockDisponible, 'warning');
                return;
            }
        } else {
            carrito.push({
                product: productId,
                description: descripcionCompleta,
                price: precio,
                codigo: 'PROD-' + Date.now(),
                cantidad: cantidad,
                talla: nombreTalla,
                stock: stockDisponible,
                idProducto: parseInt(document.getElementById("productoId").value), // Asegúrate de tener este input oculto en el HTML
                idTalla: parseInt(opcionSeleccionada.getAttribute("data-id-talla")) // Debes agregar este data-* a tus opciones
            });
        }

        localStorage.setItem('carrito', JSON.stringify(carrito));
        mostrarNotificacion("Producto agregado al carrito", "success");
        actualizarCarritoUI();
        actualizarContadorCarrito();

        // Resetear formulario
        tallaSelect.value = '';
        cantidadInput.value = '1';
    };

    // Validar cantidad cuando cambia la talla
    $('#tallaSeleccionada').change(function () {
        var opcionSeleccionada = this.options[this.selectedIndex];
        var stockDisponible = parseInt(opcionSeleccionada.getAttribute('data-stock'));
        var cantidadInput = document.getElementById('cantidad');

        if (parseInt(cantidadInput.value) > stockDisponible) {
            cantidadInput.value = stockDisponible;
            mostrarNotificacion('Cantidad ajustada al stock disponible: ' + stockDisponible, 'info');
        }

        // Actualizar el atributo max del input
        cantidadInput.setAttribute('max', stockDisponible);
    });

    // Validar cantidad cuando se escribe directamente
    $('#cantidad').on('input', function () {
        var tallaSeleccionada = document.getElementById('tallaSeleccionada');
        if (tallaSeleccionada.value) {
            var opcionSeleccionada = tallaSeleccionada.options[tallaSeleccionada.selectedIndex];
            var stockDisponible = parseInt(opcionSeleccionada.getAttribute('data-stock'));

            if (parseInt(this.value) > stockDisponible) {
                this.value = stockDisponible;
                mostrarNotificacion('Stock máximo: ' + stockDisponible, 'warning');
            }
        }

        if (parseInt(this.value) < 1) {
            this.value = 1;
        }
    });

    // Botón pagar
    $('#btnPagar').click(function () {
        if (carrito.length === 0) {
            mostrarNotificacion('Tu carrito está vacío', 'info');
        } else {
            $('#modalPagoEntrega').fadeIn();
        }
    });

    // Cerrar modales
    $('.btnCerrarModal').click(function () {
        $(this).closest('.modal').fadeOut();
    });

    // Formulario de entrega
    $('#codForm').submit(function (e) {
        e.preventDefault();
        $('#modalPagoEntrega').fadeOut(100);
        setTimeout(function () {
            inicializarBotonPayPal();
            $('#modalOpcionesPago').fadeIn(300);
        }, 200);
    });

    // Abrir carrito
    $('.shopping-cart').click(function (e) {
        e.preventDefault();
        $('.wrapper-layer').fadeIn(300);
        setTimeout(function () {
            $('.wrapper-layer .layer').css({ 'transform': 'translateX(0)' });
        }, 50);
    });

    // Cerrar carrito al hacer clic en el overlay
    $('.wrapper-layer').click(function (e) {
        if ($(e.target).is('.wrapper-layer')) {
            cerrarCarrito();
        }
    });

    // Prevenir que el clic dentro del carrito lo cierre
    $('.wrapper-layer .layer').click(function (e) {
        e.stopPropagation();
    });

    // Botón cerrar carrito
    $('.close-cart').click(function () {
        cerrarCarrito();
    });

    // Función para cerrar carrito
    function cerrarCarrito() {
        $('.wrapper-layer .layer').css({ 'transform': 'translateX(100%)' });
        setTimeout(function () {
            $('.wrapper-layer').fadeOut(100);
        }, 300);
    }

    // Cerrar carrito con tecla Escape
    $(document).keydown(function (event) {
        if (event.key === "Escape") {
            cerrarCarrito();
        }
    });

    // Cerrar modales al hacer clic fuera
    $(document).click(function (event) {
        if ($(event.target).hasClass('modal')) {
            $(event.target).fadeOut();
        }
    });

    // Botón cerrar opciones de pago
    $('#btnCerrarOpcionesPago').click(function () {
        $('#modalOpcionesPago').fadeOut();
    });

    // Inicializar la UI del carrito
    actualizarCarritoUI();
    actualizarContadorCarrito();
});