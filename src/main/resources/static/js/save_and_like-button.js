(() => {

    const btnNotLike = document.querySelector('.not-liked')
    const btnLike = document.querySelector('.liked')
    const btnSave = document.querySelector('.saved')
    const btnNotSave = document.querySelector('.not-saved')

    btnNotLike.addEventListener('click', () => {
        const likesCounter = btnNotLike.parentElement.nextElementSibling.firstElementChild.nextElementSibling;
        let newLikesNumber = (parseInt(likesCounter.textContent));
        likesCounter.textContent = (++newLikesNumber).toString();
        btnNotLike.classList.toggle('active');
        btnNotLike.nextElementSibling.classList.toggle('active');
    })

    btnNotSave.addEventListener('click', () => {
        btnNotSave.classList.toggle('active');
        btnNotSave.nextElementSibling.classList.toggle('active');
    })

    btnLike.addEventListener('click', () => {
        const likesCounter = btnLike.parentElement.nextElementSibling.firstElementChild.nextElementSibling;
        let newLikesNumber = (parseInt(likesCounter.textContent));
        likesCounter.textContent = (--newLikesNumber).toString();
        btnLike.classList.toggle('active');
        btnLike.previousElementSibling.classList.toggle('active');
    })

    btnSave.addEventListener('click', () => {
        btnSave.classList.toggle('active');
        btnSave.previousElementSibling.classList.toggle('active');
    })

})()