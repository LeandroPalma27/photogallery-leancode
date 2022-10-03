(() => {
    const fecha = document.querySelector('#fechaRegistro');
    const datea = new Date(fecha.innerText);
    const time = datea.toLocaleDateString('es-ES').split(" ");
    fecha.innerHTML = time;
})()

