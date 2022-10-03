(() => {
    // external js: masonry.pkgd.js

    $('.grid').masonry({
        itemSelector: '.grid-item',
        columnWidth: '.grid-sizer',
        percentPosition: true
      });

    const fotos = document.querySelectorAll('.grid-item');

    function getRandomArbitrary(min, max) {
        return Math.random() * (max - min) + min;
    }

    const widths = ['grid-item--width2', 'grid-item--width3'];
    const heights = ['grid-item--height2', 'grid-item--height3', 'grid-item--height4'];

    fotos.forEach(foto => {
        foto.classList.add('grid-item', widths[Math.trunc(getRandomArbitrary(0, 4))]);
        foto.classList.add(heights[Math.trunc(getRandomArbitrary(0, 4))]);
    });

})();