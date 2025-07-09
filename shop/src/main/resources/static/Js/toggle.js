// logo carrito
document.addEventListener("DOMContentLoaded", function () {
    const offcanvas = document.getElementById("offcanvasMenu");
    const logo = document.getElementById("logo-navbar");
    const carrito = document.getElementById("carrito-navbar");

    offcanvas.addEventListener("show.bs.offcanvas", function () {
        logo?.classList.add("d-none");
        carrito?.classList.add("d-none");
    });

    offcanvas.addEventListener("hidden.bs.offcanvas", function () {
        logo?.classList.remove("d-none");
        carrito?.classList.remove("d-none");
    });
});