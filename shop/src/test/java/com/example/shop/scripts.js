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
            title: mensaje
        });
    }

    function actualizarCarritoUI() {
        $('.shopping tbody').empty();
        carrito.forEach(function(item) {
            var newRow = $('<tr>' +
                '<td>' + item.product + '</td>' +
                '<td>' + item.description + '</td>' +
                '<td>S/' + item.price + '</td>' +
                '<td><input type="number" class="cantidad" value="' + item.cantidad + '" data-product="' + item.product + '"></td>' +
                '<td><button class="btnEliminar" data-product="' + item.product + '">Eliminar</button></td>' +
                '</tr>');

            newRow.find('.cantidad').change(function() {
                var productToUpdate = $(this).data('product');
                var newCantidad = parseInt($(this).val());
                carrito = carrito.map(function(item) {
                    if (item.product === productToUpdate) {
                        item.cantidad = newCantidad;
                    }
                    return item;
                });
                localStorage.setItem('carrito', JSON.stringify(carrito));
                mostrarNotificacion('Cantidad de producto actualizada.', 'success');
                actualizarCarritoUI();
            });

            newRow.find('.btnEliminar').click(function() {
                var productToRemove = $(this).data('product');
                $(this).closest('tr').remove();
                carrito = carrito.filter(function(item) {
                    return item.product !== productToRemove;
                });
                localStorage.setItem('carrito', JSON.stringify(carrito));
                mostrarNotificacion('Producto eliminado del carrito.', 'success');
                actualizarCarritoUI();
            });

            $('.shopping tbody').append(newRow);
        });
        $('.shopping tbody').append('<tr><td>Total</td><td></td><td></td><td>S/' + calcularTotal() + '</td><td></td></tr>');
    }

    function inicializarBotonPayPal() {
        if ($('#paypal-button-container').children().length === 0) {
            paypal.Buttons({
                createOrder: function(data, actions) {
                    return actions.order.create({
                        purchase_units: [{
                            amount: {
                                value: calcularTotal(),
                                currency_code: 'USD'
                            }
                        }]
                    });
                },
                onApprove: function(data, actions) {
                    return actions.order.capture().then(function(details) {
                        Swal.fire({
                            icon: 'success',
                            title: 'Pago realizado',
                            text: 'Tu pago con PayPal se ha completado con éxito.'
                        });
                        $('#modalOpcionesPago').fadeOut();
                        carrito = [];
                        localStorage.removeItem('carrito');
                        actualizarCarritoUI();
                    });
                },
                onError: function(err) {
                    Swal.fire({
                        icon: 'error',
                        title: 'Ocurrió un error con el pago',
                        text: err.message
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

    $('.product button').click(function(e) {
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
    });

    $('#btnPagar').click(function() {
        if (carrito.length === 0) {
            mostrarNotificacion('Tu carrito está vacío.', 'info');
        } else {
            $('#modalPagoEntrega').fadeIn();
        }
    });

    $('.btnCerrarModal').click(function() {
        $(this).closest('.modal').fadeOut();
    });

    $('#codForm').submit(function(e) {
        e.preventDefault();
        $('#modalPagoEntrega').fadeOut(100);
        inicializarBotonPayPal();
        $('#modalOpcionesPago').fadeIn(300);
    });

    actualizarCarritoUI();

    $('.shopping-cart').click(function() {
        $('.wrapper-layer').fadeIn(300);
        $('.wrapper-layer .layer').css({'transform':'translateX(0)'});
    });

    $('.wrapper-layer').click(function(e) {
        //  línea para detectar clic fuera del carrito
        if ($(e.target).is('.wrapper-layer')) {
            $('.wrapper-layer').fadeOut(100);
            $('.wrapper-layer .layer').css({'transform':'translateX(100%)'});
        }
    });

    $('.wrapper-layer .layer').click(function(e) {
        e.stopPropagation();
    });

    $('.close-cart').click(function() {
        $('.wrapper-layer').fadeOut(100);
        $('.wrapper-layer .layer').css({'transform':'translateX(100%)'});
    });

    function closeModalIfClickedOutside(event, modalId) {
        const modal = document.getElementById(modalId);
        if (event.target == modal) {
            modal.style.display = "none";
        }
    }

    document.addEventListener("keydown", function(event) {
        if (event.key === "Escape") {
            $('.wrapper-layer').fadeOut(100);
            $('.wrapper-layer .layer').css({'transform':'translateX(100%)'});
        }
    });
});

document.addEventListener("click", function(event) {
    var modal = document.querySelector(".modal");
    if (event.target === modal) {
        modal.style.display = "none";
    }
});

document.getElementById('btnCerrarOpcionesPago').addEventListener("click", function() {
    var modal = document.getElementById("modalOpcionesPago");
    modal.style.display = "none";
});
