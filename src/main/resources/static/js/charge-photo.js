(() => {

    const spanUrl = document.querySelector('.img-url');
    const spanTitle = document.querySelector('.img-title');

    spanTitle.nextElementSibling.nextElementSibling.src = spanUrl.textContent;
    spanTitle.nextElementSibling.nextElementSibling.alt = spanTitle.textContent;

})()