(() => {
    const fecha = document.querySelector('#fechaRegistro');
    const datea = new Date(fecha.innerText);
    const time = datea.toString().split(" ");
    const timeText = [];

    for (let i = 1; i < 4; i++) {

        if (i == 2) {
            const modified = time[i];
            timeText.push(modified.concat(","));
            continue;
        }

        timeText.push(time[i]);
    }

    fecha.innerHTML = timeText.join(" ");
})()

