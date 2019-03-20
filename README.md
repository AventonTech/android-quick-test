# Prueba aplicación Android

Esta prueba debe ser entregada en un plazo no más de 48 horas.

Crear un nuevo proyecto Android, completar cada una de las tareas descritas, al finalizar, crear un Pull Request a este repositorio con el código completado.

  - La aplicación debe tener un menú con 2 items de navegación para mostrar 2 pantallas diferentes, Inicio y Perfil.
  - Inicio mostrara un mapa basado en el SDK de Mapbox, que muestre por defecto la ubicación del usuario.
  - Mediante una petición POST a passenger-dev.aventontech.com/api/test con el siguiente JSON:
```sh
{"name":"nombre","lat":"12.126934","long":"-86.2712677"}
```
donde lat y long sera la ubicación del usuario.
  - Guardar la respuesta JSON localmente y mostrar en el mapa los resultados obtenidos, además de la ubicación del usuario.
  - Perfil mostrara datos hardcoded de un perfil común de usuario, nombre, apellido, correo, numero de celular y que muestre una imagen de perfil que la cargue directamente desde https://aventontech.com/images/footer-aventon-logo.jpg
