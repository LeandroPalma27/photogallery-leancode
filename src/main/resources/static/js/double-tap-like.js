(() => {
    const photoBox = document.querySelector('#photoBox');

    photoBox.addEventListener('dblclick', (e) => {
        showHeart(document.querySelector('.liked-heart'));
        const btnNotLiked = document.querySelector('#btnNotLiked');
        if (btnNotLiked != null) {
            btnNotLiked.click();
        }
    })

    const showHeart = (element) => {
        if (!element.matches('.active')) {
            element.classList.toggle('active');
            setTimeout(() => {
                element.classList.toggle('active');
            }, 800)
        }
    }
})()