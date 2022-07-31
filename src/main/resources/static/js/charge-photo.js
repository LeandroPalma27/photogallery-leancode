(() => {

    const spanUrl = document.querySelector('.img-url');
    const spanTitle = document.querySelector('.img-title');

    spanTitle.nextElementSibling.src = spanUrl.textContent;
    spanTitle.nextElementSibling.alt = spanTitle.textContent;

})()