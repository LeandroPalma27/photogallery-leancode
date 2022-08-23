(() => {
    const fechaDom = document.querySelector('#fechaRegistro');
    const tipoFecha = document.querySelector('#tipoFecha');

    if (fechaDom.textContent != '') {
        const date = new Date(fechaDom.textContent);
        const month = date.getMonth().toString();
        const day = (date.getDate());
        const year = date.getFullYear();
        
        const allMonths = ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'];
        const months = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];
        
        console.log(tipoFecha.textContent)
        if (tipoFecha.textContent == 'ES') {
            const nuevaFecha = day.toString().concat(' de ' + allMonths[month]).concat(', ' + year);
            fechaDom.innerHTML = nuevaFecha;
        } else if (tipoFecha.textContent == 'EN') {
            console.log('first')
            const nuevaFecha = months[month].concat(` ${day.toString()}, ${year}`);
            fechaDom.innerHTML = nuevaFecha;
        }

    } else {
        if (tipoFecha.textContent == 'ES') {
            fechaDom.innerHTML = '27 Marzo, 2022';
        } else if (tipoFecha.textContent = 'EN') {
            fechaDom.innerHTML = 'March 27, 2022';
        }
    }
})()
