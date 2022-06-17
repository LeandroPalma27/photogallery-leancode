(()=>{
    const photoBox = document.querySelector('#photoBox');
   
    photoBox.addEventListener('dblclick', (e) => {
        showHeart(document.querySelector('.liked-heart'));
        const btnNotLiked = document.querySelector('#btnNotLiked');
        if (btnNotLiked != null) {
            if (btnNotLiked.matches('.active')) {
                btnNotLiked.click();
            }
        }
    })

    const showHeart = (element) => {
        if (!element.matches('.active')) {
            element.classList.toggle('active');
        } 
        if (element.matches('.active')){
            setTimeout(() => {
                element.classList.toggle('active');
            }, 800)
        }
    }
})()