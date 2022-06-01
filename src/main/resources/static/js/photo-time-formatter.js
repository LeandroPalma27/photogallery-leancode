(()=> {
    const fechaDom = document.querySelector('#fechaRegistro');
    
    if (fechaDom.textContent != '') {
        const date = new Date(fechaDom.textContent);
        const month = date.getMonth().toString();
        const day = (date.getDate());
        const year = date.getFullYear();
        
        const allMonths = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];
        
        
        const nuevaFecha = allMonths[month].concat(' ' + day).concat(', ' + year)
        
        fechaDom.innerHTML = nuevaFecha;
    } else {
        fechaDom.innerHTML = 'March 27, 2022';
    }
})()
