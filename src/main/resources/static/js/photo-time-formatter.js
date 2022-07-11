(()=> {
    const fechaDom = document.querySelector('#fechaRegistro');
    
    if (fechaDom.textContent != '') {
        const date = new Date(fechaDom.textContent);
        const month = date.getMonth().toString();
        const day = (date.getDate());
        const year = date.getFullYear();
        
        const allMonths = ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'];
        
        
        const nuevaFecha = day.toString().concat(' de ' + allMonths[month]).concat(', ' + year);
        
        fechaDom.innerHTML = nuevaFecha;
    } else {
        fechaDom.innerHTML = '27 Marzo, 2022';
    }
})()
