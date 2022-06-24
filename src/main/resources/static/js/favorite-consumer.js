(() => {
    const url = window.location.host;
    const token = document.querySelector("meta[name='_csrf']").content;
    const header = document.querySelector("meta[name='_csrf_header']").content;

    const isFavoriteText = document.querySelector('#isSaved').textContent;
    const isFavorite = isFavoriteText == "true" ? true : false;

    const photoId = document.querySelector("#photoIdForLike").textContent;
    const username = document.querySelector("#usernameForLike").textContent;

    const saveButtonsDiv = document.querySelector('.save-button');

    const BTNREMOVE = '<button title="Remove from favorites" type="button" class="saved active"><span><i class="fa-solid fa-bookmark"></i></button>';
    const BTNSAVE = '<button title="Add to favorites" type="button" class="not-saved active"><span><i class="fa-regular fa-bookmark"></i></span></button>';

    const chargeButton = (isFavorite) => {
        if (isFavorite != null) {
            if (isFavorite) {
                saveButtonsDiv.innerHTML = BTNREMOVE;
                const btnRemove = document.querySelector('.saved');
                btnRemove.addEventListener('click', () => {
                    quitarFavoritos(photoId, username);
                });
            } else {
                saveButtonsDiv.innerHTML = BTNSAVE;
                const btnSave = document.querySelector('.not-saved');
                btnSave.addEventListener('click', () => {
                    añadirFavoritos(photoId, username);
                });
            }
        }
    }

    chargeButton(isFavorite);

    const añadirFavoritos = (photoId, username) => {
        fetch("http://".concat(url + "/add-favorites"), {
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
                chargeButton(json.isSuccesful == true ? true : null)
            })
        });
    }

    const quitarFavoritos = (photoId, username) => {
        fetch("http://".concat(url + "/remove-favorites"), {
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
                chargeButton(json.isSuccesful == true ? false : null)
            })
        });
    }

})()