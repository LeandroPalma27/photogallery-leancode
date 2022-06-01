# Carpeta de dominios del proyecto (POJO's con la finalidad de alimentar directamente a la funcionalidad del proyecto)

En esta carpeta se encuentran nuestros beans que no se inyectan usando autowired, mas bien serian los POJOs de nuestra aplicacion.

Actualmente hay 2 beans que sirven para obtener la hora exacta en un tipo de dato compatible para el manejo de base de datos con spring, y otro para la paginacion en el listado de fotos.

Ademas de estos, tambien tenemos uno para el manejo de un response luego de una consulta o transaccion en nuestra base de datos o provedor externo.

Finalmente, tenemos a los dominios de validacion de formularios, los cuales estan desarrollados para poder llevar buenas practicas de desarrollo de formularios en el proyecto.