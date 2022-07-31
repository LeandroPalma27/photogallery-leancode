(() => {

    const containerOrder = document.querySelector('.container-order');
    const params = new URLSearchParams(window.location.search);

    const select1 = `<select id="orderSelect" class="form-select form-select-sm" aria-label=".form-select-sm example">
                        <option selected value="dateDESC">Mas recientes</option>
                        <option value="dateASC">Mas antiguo</option>
                        <option value="likesCountDESC">Recomendado</option>
                        <option value="likesCountASC">Menos recomendado</option>
                    </select>`;
    const select2 = `<select id="orderSelect" class="form-select form-select-sm" aria-label=".form-select-sm example">
                        <option value="dateDESC">Mas recientes</option>
                        <option selected value="dateASC">Mas antiguo</option>
                        <option value="likesCountDESC">Recomendado</option>
                        <option value="likesCountASC">Menos recomendado</option>
                    </select>`;
    const select3 = `<select id="orderSelect" class="form-select form-select-sm" aria-label=".form-select-sm example">
                        <option value="dateDESC">Mas recientes</option>
                        <option value="dateASC">Mas antiguo</option>
                        <option selected value="likesCountDESC">Recomendado</option>
                        <option value="likesCountASC">Menos recomendado</option>
                    </select>`;
    const select4 = `<select id="orderSelect" class="form-select form-select-sm" aria-label=".form-select-sm example">
                        <option value="dateDESC">Mas recientes</option>
                        <option value="dateASC">Mas antiguo</option>
                        <option value="likesCountDESC">Recomendado</option>
                        <option selected value="likesCountASC">Menos recomendado</option>
                    </select>`;


    const urlOrder = params.get('order');

    const selected = urlOrder == 'dateDESC' ? select1 : urlOrder == 'dateASC' ? select2 : urlOrder == 'likesCountDESC' ? select3 : urlOrder == 'likesCountASC' ? select4 : select2;

    containerOrder.innerHTML = selected;
    console.log(selected);

    $('select').on('change', function () {
        window.location.href = window.location.protocol + '//' + window.location.hostname + ':' + window.location.port + window.location.pathname + `?order=${$('select').val()}`
    });


})()