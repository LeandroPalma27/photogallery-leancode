(() => {

    const closeButtons = document.querySelectorAll('.close-button_controller-message');

    if (closeButtons != null) {
        closeButtons.forEach((button) => {
            button.addEventListener('click', () => {
                const header = button.parentElement.parentElement;
                header.removeChild(button.parentElement)
            })
        });
    }

    

})()