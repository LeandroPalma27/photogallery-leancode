(() => {

    const btnSave = document.querySelector('.saved')
    const btnNotSave = document.querySelector('.not-saved')

    btnNotSave.addEventListener('click', () => {
        btnNotSave.classList.toggle('active');
        btnNotSave.nextElementSibling.classList.toggle('active');
    })

    btnSave.addEventListener('click', () => {
        btnSave.classList.toggle('active');
        btnSave.previousElementSibling.classList.toggle('active');
    })

})()