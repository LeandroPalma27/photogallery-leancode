(() => {

    let FILE = null;
    const validExtensions = ["image/jpeg", "image/jpg", "image/png"];

    const dropArea = document.querySelector(".drag-area"),
        dragText = dropArea.querySelector("header"),
        button = dropArea.querySelector("button"),
        input = dropArea.querySelector("input"),
        uploaderContainer = document.querySelector('.uploader');

    const btnInvalidFile = document.querySelector('#btnInvalidFile');
    const btnInvalidSize = document.querySelector('#btnInvalidSizeFile');


    let btnRemovePic = null;

    button.onclick = () => {
        input.click();
    }

    input.addEventListener("change", function () {
        // Hacemos referencia al elemento input con "this" y obtenemos el archivo en cuestion:
        FILE = this.files[0];
        dropArea.classList.add("active");
        var res = chargeFile(FILE); //calling function
        if (res) input.value = "";
    });


    // Si pasas lo arrastrado por encima del elemento en cuestion:
    dropArea.addEventListener("dragover", (event) => {
        event.preventDefault(); //preventing from default behaviour
        dropArea.classList.add("active");
        dragText.textContent = "Release to load";
    });

    // Si mueves lo arrastrado fuera del elemento en cuestion:
    dropArea.addEventListener("dragleave", () => {
        dropArea.classList.remove("active");
        dragText.textContent = "Drag & drop to Upload File";
    });

    // Se ejecuta si el archivo ya esta dropeado en el elemento en cuestion:
    dropArea.addEventListener("drop", (event) => {
        event.preventDefault(); //preventing from default behaviour
        FILE = event.dataTransfer.files[0];
        chargeFile(FILE); //calling function
    });


    // Funcion que carga el archivo a nuestro input:
    const chargeFile = (file) => {
        if (validExtensions.includes(file.type)) {

            var isValidSize = file.size / 1024 < 10000 ? true : false;

            if (isValidSize) {

                let fileReader = new FileReader(); //creating new FileReader object
                fileReader.readAsDataURL(file);
                fileReader.onload = () => {
                    let container = new DataTransfer();
                    console.log(file.size / 1024)
                    container.items.add(file);
                    input.files = container.files;
                    dragText.textContent = container.files[0].name;
                    console.log(input.files);
                    const btnRemovePicture = document.createElement("button");
                    btnRemovePicture.setAttribute("class", "btn-remove_picture");
                    btnRemovePicture.setAttribute("title", "Remove file");
                    btnRemovePicture.setAttribute("type", "button");
                    btnRemovePicture.innerHTML = '<i class="fa-solid fa-trash"></i>'
                    uploaderContainer.appendChild(btnRemovePicture.cloneNode(true));
                    btnRemovePic = document.querySelector('.btn-remove_picture');
                    btnRemovePic.addEventListener("click", (evt) => {
                        removeFile();
                    });
                }

            } else {
                btnInvalidSize.click();
                dropArea.classList.remove("active");
                dragText.textContent = "Drag & drop to Upload File";
                return true;
            }

        } else {
            btnInvalidFile.click();
            dropArea.classList.remove("active");
            dragText.textContent = "Drag & drop to Upload File";
            return true;
        }
    }

    const removeFile = () => {
        dropArea.classList.remove("active");
        dragText.textContent = "Drag & drop to Upload File";
        input.value = "";
        uploaderContainer.removeChild(btnRemovePic);
    }

})();