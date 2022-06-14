(()=>{
    const btnBackToList = document.querySelector('#btnBackToList');
    btnBackToList.addEventListener('click', ()=> {
        window.history.back();
    })
})()