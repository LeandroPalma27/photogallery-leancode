(() => {
    const popoversDetail = document.querySelectorAll('.photo-details')

    const nameAndLastname1 = document.querySelector('#nameAndLastname1');
    const nameAndLastname2 = document.querySelector('#nameAndLastname2');
    const nombreCortado = document.querySelector('#nombreCortado');

    const accountStatus = document.querySelector('#accountStatus');

    if (accountStatus != null) {
        if (accountStatus.innerText == 'true') {
            accountStatus.innerText = 'Active';
        } else {
            accountStatus.innerText = 'No - active';
        }
    }

    const names = nombreCortado.innerText.split(" ");
    const nameRender = [];

    nameRender.push(names[0]);
    nameRender.push(names[names.length - 2]);
    nameRender.push(names[names.length - 1]);

    nombreCortado.innerText = nameRender[0];

    if (nameAndLastname1 != null) {
        if (!(names.length < 3)) {
            nameAndLastname1.innerText = nameRender.join(" ");
        }
    }

    var popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'))
    var popoverList = popoverTriggerList.map(function (popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl)
    })

    popoversDetail.forEach((popover) => {

    });


})();
