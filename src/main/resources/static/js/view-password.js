(()=> {
    const eyes = document.querySelectorAll('.eye');
    const eyeSlashes = document.querySelectorAll('.eye-slash');
    
    const eye = document.querySelector('.eye');
    const eyeSlash = document.querySelector('.eye-slash');
    
    const buttonEyes = [eyes, eyeSlashes];
    
    buttonEyes.forEach(e => {
        e.forEach(element => {
            if (element.classList.contains('eye-slash')) {
                element.addEventListener('click', () => {
                    element.classList.toggle('disabled');
                    element.previousElementSibling.classList.toggle('disabled');
                    if (element.previousElementSibling.previousElementSibling.type == 'password') {
                        element.previousElementSibling.previousElementSibling.type = 'text';
                    }
                });
            } else if (element.classList.contains('eye')) {
                element.addEventListener('click', () => {
                    element.classList.toggle('disabled');
                    element.nextElementSibling.classList.toggle('disabled');
                    if (element.previousElementSibling.type == 'text') {
                        element.previousElementSibling.type = 'password';
                    }
                });
            }
        });
    });
})()