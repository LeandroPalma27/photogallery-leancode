(() => {
    const modalActivator = document.querySelector('#modalActivator');
    const buttonModalDescription = document.querySelector('#btnModalDesc');
    const buttonModalNameAndLastName = document.querySelector('#btnModalNameAndLastName');
    const modalDescription = document.querySelector('#modalForDescription');
    const modalNameAndLastName = document.querySelector('#modalForNameAndLastName');
    const modalPassword = document.querySelector('#modalForUsername');
    const buttonModalPassword = document.querySelector('#btnModalUsername');
    const modalUpdatePhoto = document.querySelector('#modalForUpdatePhoto');
    const btnModalUpdatePhoto = document.querySelector('#btnModalUpdatePhoto');
    const modalUploadProfilePhoto = document.querySelector('#modalForUploadProfilePicture')
    const btnModalUploadProfilePhoto = document.querySelector('#btnForModalPicture')
    
    if (modalActivator != null) {
    
        if (modalActivator.innerText=="descrip") {
            modalDescription.setAttribute("data-bs-backdrop", "static");
            modalDescription.setAttribute("data-bs-keyboard", "false");
            modalDescription.firstElementChild.firstElementChild.firstElementChild.firstElementChild.nextElementSibling.lastElementChild.remove();
            modalDescription.firstElementChild.firstElementChild.firstElementChild.lastElementChild.firstElementChild.remove();
            buttonModalDescription.click();
        } else if (modalActivator.innerText=="nameAndLastName") {
            modalNameAndLastName.setAttribute("data-bs-backdrop", "static");
            modalNameAndLastName.setAttribute("data-bs-keyboard", "false");
            modalNameAndLastName.firstElementChild.firstElementChild.firstElementChild.firstElementChild.nextElementSibling.lastElementChild.remove();
            modalNameAndLastName.firstElementChild.firstElementChild.firstElementChild.lastElementChild.firstElementChild.remove();
            buttonModalNameAndLastName.click();
        } else if (modalActivator.innerText == "password") {
            modalPassword.setAttribute("data-bs-backdrop", "static");
            modalPassword.setAttribute("data-bs-keyboard", "false");
            modalPassword.firstElementChild.firstElementChild.firstElementChild.firstElementChild.nextElementSibling.lastElementChild.remove();
            modalPassword.firstElementChild.firstElementChild.firstElementChild.lastElementChild.firstElementChild.remove();
            buttonModalPassword.click();
        } else if (modalActivator.innerText=="photoUpdate") {
            modalUpdatePhoto.setAttribute("data-bs-backdrop", "static");
            modalUpdatePhoto.setAttribute("data-bs-keyboard", "false");
            modalUpdatePhoto.firstElementChild.firstElementChild.firstElementChild.firstElementChild.nextElementSibling.lastElementChild.remove();
            modalUpdatePhoto.firstElementChild.firstElementChild.firstElementChild.lastElementChild.firstElementChild.remove();
            btnModalUpdatePhoto.click();
        } else if (modalActivator.innerText=='profilePicture') {
            modalUploadProfilePhoto.firstElementChild.firstElementChild.firstElementChild.lastElementChild.remove();
            btnModalUploadProfilePhoto.click();
        }
    }

})();

