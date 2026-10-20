# SafeWords

SafeWords es un foro web orientado a la escucha, el respeto y la orientación frente al bullying, el ciberbullying y la violencia física, verbal y psicológica.

## Estructura

- `frontend/`: aplicación Angular responsive.
- `backend/`: API REST desarrollada con Java y Spring Boot.
- `database/`: script SQL para crear la base de datos `safe_words`.
- `documentos/`: documentación de arquitectura, wireframes y mockups.

## Tecnologías

Angular, TypeScript, Bootstrap, Java 17, Spring Boot, Spring Data JPA, Spring Security, JWT y MySQL.

## Ejecución local

1. Ejecuta `database/base_datos_safe_words.sql` en MySQL Workbench.
2. Configura las variables de entorno del backend usando `backend/src/main/resources/application-local.properties.example` como referencia.
3. Inicia el backend en Eclipse o con `backend/mvnw.cmd spring-boot:run`.
4. Inicia el frontend con `npm install` y `npm start` dentro de `frontend/`.

El frontend usa el proxy de Angular para comunicarse con el backend local en `http://localhost:8080`.

## Publicación

El frontend puede publicarse gratuitamente mediante GitHub Pages. La API Spring Boot y MySQL requieren un servicio de alojamiento independiente; las credenciales deben configurarse como variables privadas del proveedor elegido y nunca dentro del repositorio.
