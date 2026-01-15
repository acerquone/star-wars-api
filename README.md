# Challenge Técnico: Integración Star Wars API

Este proyecto implementa una API REST desarrollada con **Java 21** y **Spring Boot**, diseñada para la gestión de entidades del universo Star Wars a través de la integración con SWAPI.

## Estado del Despliegue
La aplicación se encuentra operativa en el siguiente entorno:
* **URL Base y Swagger UI:** https://star-wars-api-acerquone.onrender.com

### Nota Importante sobre el Entorno de Ejecución
Debido a las limitaciones del tier gratuito de Render:
1. **Cold Start:** La instancia puede requerir un tiempo de arranque inicial si no ha recibido tráfico recientemente.
2. Volatilidad de Datos: La aplicación utiliza una base de datos H2 basada en archivos para mejorar la persistencia durante la sesión. Sin embargo, debido a que el entorno de Render opera sobre un sistema de archivos efímero, cualquier dato almacenado (usuarios registrados) se eliminará cada vez que ocurra un Cold Start o se reinicie el contenedor.

## Cumplimiento de Requisitos y Alcance
El desarrollo se centró en establecer una arquitectura sólida y segura, tomando la entidad **People** como modelo de referencia funcional completo.

### 1. Arquitectura Extensible (Módulo People)
Se ha implementado el flujo completo para la entidad **People**, cumpliendo con:
* **Paginación:** Manejo de parámetros `page` y `limit`.
* **Filtrado:** Búsqueda por ID y por nombre.
* **Transformación de Datos:** Uso de DTOs para normalizar la respuesta de 3 niveles de la API externa.

**Nota sobre Escalabilidad:** El sistema fue diseñado siguiendo patrones de diseño que permiten replicar esta lógica para las entidades restantes (*Films, Starships, Vehicles*) de manera inmediata, compartiendo la misma base de seguridad y configuración de clientes de API.

### 2. Autenticación y Seguridad
Se implementó un esquema de seguridad robusto válido para toda la aplicación:
* **Spring Security & JWT:** Acceso restringido mediante tokens firmados.
* **Login/Register:** Endpoints funcionales para la gestión de acceso.

## Consideraciones de Seguridad (JWT)
Para fines de demostración en este challenge, la clave secreta del JWT se encuentra configurada en el archivo `application.properties`.

### 3. Calidad Técnica
* **Documentación:** Especificación OpenAPI 3 (Swagger) disponible para pruebas.
* **Pruebas:** Suite de tests unitarios y de integración para la lógica de negocio y seguridad.
* **Lombok:** Implementado para garantizar un código limpio y profesional.

## Configuración y Variables de Entorno
La aplicación está preparada para ser configurada externamente sin modificar el código fuente. Se pueden ajustar los siguientes parámetros a través de variables de entorno o modificando el archivo `src/main/resources/application.properties`:

* **Puerto del servidor:** `server.port` (Por defecto: 8080).
* **Configuración de Seguridad:** Tiempo de expiración y clave secreta del JWT.
* **Base de Datos:** Aunque el proyecto utiliza H2 basada en archivos, es posible conectar una base de datos externa (PostgreSQL/MySQL) modificando los parámetros del `datasource`.
* **Integración Externa:** Para este challenge, la URL base de SWAPI se ha mantenido en una constante dentro del cliente de servicio para garantizar la estabilidad de las pruebas. En una arquitectura de producción, este parámetro se externalizaría al `application.properties` para permitir el cambio de entorno (Staging/Production) sin recompilar.

## Ejecución en Local
1. Requisitos: JDK 21 y Maven.
2. Compilación y Ejecución: `./mvnw spring-boot:run`
3. Documentación local: `http://localhost:8080/swagger-ui.html`
