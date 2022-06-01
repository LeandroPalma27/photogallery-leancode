(()=> {

    const url = document.querySelector('#urlPictureForLayout');
    const url2 = document.querySelector('#urlPictureForLayout2');
    const profilePictureUserUrl = document.querySelector('#profilePictureUserUrl');
    
    if (profilePictureUserUrl != null) {
        profilePictureUserUrl.nextElementSibling.src=profilePictureUserUrl.textContent;
    }

    if (url != null) {
        url.nextElementSibling.src=url.textContent;
    }

    if (url2 != null) {
        url2.nextElementSibling.src=url2.textContent;
    }

})();