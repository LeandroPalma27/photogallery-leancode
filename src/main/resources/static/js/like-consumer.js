(() => {
    const token = document.querySelector("meta[name='_csrf']").content;
    const header = document.querySelector("meta[name='_csrf_header']").content;

    const isLikedText = document.querySelector('#isLiked').textContent;
    const isLiked = isLikedText == "true" ? true : false;

    const photoId = document.querySelector("#photoIdForLike").textContent;
    const username = document.querySelector("#usernameForLike").textContent;

    const likeButtonsDiv = document.querySelector('.like-button');

    const BTNDISLIKE = '<button type="button" class="liked active"><span><i class="fa-solid fa-heart"></i></span></button>';
    const BTNLIKE = '<button id="btnNotLiked" type="button" class="not-liked active"><span><i class="fa-regular fa-heart"></i></span></button>';

    const chargeButton = (isSuccesful) => {
        if (isSuccesful) {
            likeButtonsDiv.innerHTML = BTNDISLIKE;
            const btnDisLike = document.querySelector('.liked');
            const likeCounter = btnDisLike.parentElement.nextElementSibling.lastElementChild;
            btnDisLike.addEventListener('click', () => {
                darDislike(photoId, username);
                let counter = likeCounter.textContent;
                likeCounter.textContent = --counter;
            });
        } else {
            likeButtonsDiv.innerHTML = BTNLIKE;
            const btnLike = document.querySelector('.not-liked');
            const likeCounter = btnLike.parentElement.nextElementSibling.lastElementChild;
            btnLike.addEventListener('click', () => {
                darLike(photoId, username);
                let counter = likeCounter.textContent;
                likeCounter.textContent = ++counter;
            });
        }
    }

    chargeButton(isLiked);

   /*  const changeLikeButton = (isSuccesful) => {
        if (isSuccesful) {
            likeButtonsDiv.innerHTML = BTNDISLIKE;
            const btnDisLike = document.querySelector('.liked');
            btnDisLike.addEventListener('click', () => {
                darLike(photoId, username);
            });
        } else {
            likeButtonsDiv.innerHTML = BTNLIKE;
            const btnLike = document.querySelector('.not-liked');
            btnLike.addEventListener('click', () => {
                darLike(photoId, username);
            });
        }
    } */

    const darLike = (photoId, username) => {
        fetch("http://localhost:8080/like", {
            method: "POST",
            headers: {
                [header]: token,
                "charset": "UTF-8",
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                photo_id: photoId,
                user: username
            })
        }).then(res => {
            res.json().then(json => {
                console.log(json)
                chargeButton(json.isLiked)
            })
        });
    }

    const darDislike = (photoId, username) => {
        fetch("http://localhost:8080/dislike", {
            method: "POST",
            headers: {
                [header]: token,
                "charset": "UTF-8",
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                photo_id: photoId,
                user: username
            })
        }).then(res => {
            res.json().then(json => {
                console.log(json)
                chargeButton(json.isDisliked)
            })
        });
    }

})()