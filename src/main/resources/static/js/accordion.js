(() => {
    const btnAccordion = document.querySelector('.button_for-active');

    const barsIcon = document.querySelector('.fa-bars');
    const crossIcon = document.querySelector('.fa-xmark');
    const accordion = document.querySelector('.accordion');
    const listAccordion = document.querySelector('.list-accordion');
    const bodyPage = document.querySelector('.body-page');

    btnAccordion.addEventListener('click', () => {
        barsIcon.classList.toggle('active');
        crossIcon.classList.toggle('active');
        accordion.classList.toggle('active');
        listAccordion.classList.toggle('active');
        bodyPage.classList.toggle('active');
    });
})()