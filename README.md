# Prueba aplicacion Android

Esta prueba debe ser entregada en un plazo no mas de 48 horas.

Crear un nuevo proyecto Android, completar cada una de las tareas descritas, al finalizar, crear un Pull Request a este repositorio con el codigo completado.

1 - La aplicacion debe tener un menu con 2 items de navegacion para mostrar 2 pantallas diferentes, Inicio y Perfil.
2 - Inicio mostrara un mapa basado en el SDK de Mapbox, que muestre por defecto la ubicacion del usuario.
3 - Mediante una peticion POST a passenger-dev.aventontech.com/api/test con el siguiente JSON:
```sh
{"name":"nombre","lat":"12.126934","long":"-86.2712677"}
```
donde lat y long sera la ubicacion del usuario.
4 - Guardar la respuesta JSON localmente y mostrar en el mapa los resultados obtenidos, ademas de la ubicacion del usuario.
5 - Perfil mostrara datos hardcoded de un perfil comun de usuario, nombre, apellido, correo, numero de celular y que muestre una imagen de perfil que la cargue directamente desde https://aventontech.com/images/footer-aventon-logo.jpg
