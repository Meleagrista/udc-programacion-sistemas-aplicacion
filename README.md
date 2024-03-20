# Concepto de la aplicación
"Para disfrutar al máximo en cada reunión con tus amigos."

## Descripción del concepto
El juego consta de una serie de desafíos, tanto individuales como en grupo, que requieren participación por parte de los jugadores. con sus recompensas y penalizaciones para ganadores y perdedores.

Mientras, la aplicación será la plataforma que proporcionará la interfaz y las funciones necesarias para jugar. Esto incluirá características como rankings entre amigos, la capacidad de jugar en línea con varios jugadores y algunas opciones para personalizar la interfaz.

# Prototipo de la aplicación
## Lanzador del juego
![Prototipo de la primeras pantallas](/prototyping-files/launcher-02-03.jpg)

1. Al abrir la aplicación por primera vez, los jugadores tendrán la opción de iniciar sesión o acceder de forma anónima, sin necesidad de registrarse.

2. Una vez que el usuario esté registrado, o en caso de haberlo estado previamente, se presentará una pantalla que muestra todos los modos de juego disponibles, junto con un menú inferior que ofrece diversas opciones. Estas incluyen el catálogo de modos de juego, una sección para personalizar el perfil y una tienda donde los jugadores podrán adquirir cosméticos y privilegios. En esta pantalla, se encontrará un menú deslizable con los distintos juegos, lo que permitirá acceder fácilmente a cada uno de ellos. Además, habrá una opción de ajustes en la parte superior de la pantalla, donde los jugadores podrán acceder a guías de ayuda para comprender mejor el juego y modificar ajustes según sus preferencias.

El modo de juego principal consiste en un desafío de pruebas y preguntas con diferentes niveles de dificultad, cada uno con sus correspondientes castigos y penalizaciones por respuestas incorrectas, así como premios por respuestas correctas o por superar el desafío.

3. Una vez seleccionado el modo de juego, se accede a una pantalla donde se solicita al usuario que elija la siguiente acción a realizar: crear una sala (para el anfitrión del juego), unirse a una sala o jugar en modo individual, esta última hace uso de un solo dispositivo por lo que las funcionalidades serán mas limitadas. Cada opción dirigirá al usuario a una pantalla diferente.

## Apariencia del juego
> [!WARNING]
> Todavía por decidir.

# Registro de desarrollo
## Snapshot 20/03/2024
### Inicio de sesión
![Pantalla de inicio de sesión](/design-files/screenshots/inicio-2024-03-20.png)

Permite registrarte e iniciar sesión, actualmente estamos usando una base de datos manejada por FireBase.

### Menú principal
![Pantalla de principal](/design-files/screenshots/menu-2024-03-20.png)

Tiene un menu de navgeación en la barra inferior que permite ir al menú de amistades o a la tienda (aún no está implementada). En la parte superior, en el icono de mas opciones se podría cerrar la sesión o ir a la configuración (aún no está implementada).
En el medio tenemos el menú deslizable donde hay tres opciones, unicamente una esta operativa.

### Amigos
![Pantalla de amistades](/design-files/screenshots/amigos-2024-03-20.png)

Permite aceptar y enviar solicitudes y ver tus amigos añadidos.

### Creación de sala
![Pantalla de creación de sala](/design-files/screenshots/sala-2024-03-20.png)

Un pantalla temporal, falta un paso intermedio en la navegación, que sería la creación de una sala multijugador.
