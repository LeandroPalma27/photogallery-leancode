(()=>{
    const photoBox = document.querySelector('#photoBox');
    const btnNotLiked = document.querySelector('#btnNotLiked');
    photoBox.addEventListener('dblclick', (e) => {
        showHeart(document.querySelector('.liked-heart'));
        if (btnNotLiked.matches('.active')) {
            btnNotLiked.click();
        }
    })

    const showHeart = (element) => {
        if (!element.matches('.active')) {
            element.classList.toggle('active');
        } 
        if (element.matches('.active')){
            setTimeout(() => {
                element.classList.toggle('active');
            }, 1500)
        }
    }
})()