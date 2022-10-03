(() => {

    const urlPhotos = document.querySelectorAll('.urlFoto');
    const titlePhotos = document.querySelectorAll('.titleFoto');
    const authorPhotos = document.querySelectorAll('.authorFoto');

    urlPhotos.forEach((url) => {

        try {
            url.nextElementSibling.nextElementSibling.firstElementChild.src = url.textContent;
        } catch (error) {
            url.nextElementSibling.nextElementSibling.src = url.textContent;
        }

    });

    titlePhotos.forEach((title) => {
        title.nextElementSibling.alt = title.textContent;
    });

    authorPhotos.forEach((author) => {
        const names = author.innerText.split(" ");
        const nameRender = [];

        nameRender.push(names[0]);
        nameRender.push(names[names.length - 2]);
        nameRender.push(names[names.length - 1]);

        if (!(names.length < 3)) {
            author.innerText = nameRender.join(" ");
        }
    });

})();