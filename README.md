# Plataforma Web para Apoyar la Gestión de Información mediante Analítica de Datos en Organizaciones que Asisten a Personas Mayores

## Descripción

Este proyecto corresponde al trabajo de grado del programa de **Ingeniería de Sistemas de la Pontificia Universidad Javeriana**.

La solución consiste en una plataforma web orientada a apoyar la gestión de información de organizaciones que trabajan con personas mayores en la **UPL Entrenubes (Usme, Bogotá)**, permitiendo centralizar información, fortalecer el seguimiento de beneficiarios y generar indicadores mediante técnicas de analítica de datos.

---

# Tecnologías Utilizadas

## Backend

* Java 21 LTS
* Spring Boot
* Spring Security
* JWT Authentication
* Maven
* PostgreSQL

## Frontend

* Angular
* TypeScript
* Bootstrap / Angular Material

## Herramientas adicionales

* Git
* GitHub
* Docker
* Docker Compose

---

# Arquitectura del Proyecto

La plataforma está organizada en tres componentes principales:

1. **Frontend:** interfaz web desarrollada en Angular.
2. **Backend:** API REST desarrollada con Spring Boot y Java.
3. **Base de datos:** PostgreSQL para la persistencia de la información.

```text
┌──────────────────────┐
│       Usuario        │
└──────────┬───────────┘
           │
           ▼
┌──────────────────────┐
│       Frontend       │
│       Angular        │
│     TypeScript       │
└──────────┬───────────┘
           │
           │ HTTP / REST
           ▼
┌──────────────────────┐
│       Backend        │
│     Spring Boot      │
│        Java 21       │
└──────────┬───────────┘
           │
           │ JPA / SQL
           ▼
┌──────────────────────┐
│      PostgreSQL      │
│      Base de datos   │
└──────────────────────┘
```

Esta separación permite mantener independientes la **interfaz de usuario**, la **lógica de negocio** y la **persistencia de los datos**, facilitando el mantenimiento, escalabilidad y evolución de la plataforma.

---

# Estructura del Proyecto

El proyecto está organizado en dos componentes principales, **Frontend** y **Backend**, junto con los archivos de configuración necesarios para la ejecución de la aplicación y la gestión de la base de datos.

```text
Proyecto-de-grado/
│
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   │
│   │   └── test/
│   │
│   ├── mvnw
│   ├── mvnw.cmd
│   └── pom.xml
│
├── frontend/
│   ├── public/
│   ├── src/
│   │   ├── app/
│   │   ├── index.html
│   │   ├── main.ts
│   │   └── styles.css
│   │
│   ├── angular.json
│   ├── package.json
│   ├── package-lock.json
│   ├── tsconfig.json
│   ├── tsconfig.app.json
│   └── tsconfig.spec.json
│
├── docker-compose.yml
└── README.md
```

---

# Requisitos Previos

Antes de ejecutar el proyecto, es necesario contar con las siguientes herramientas instaladas:

## Java

Verificar la instalación:

```bash
java -version
```

Versión requerida:

```text
Java 21 LTS
```

---

## Node.js

Verificar la instalación:

```bash
node -v
```

Se recomienda utilizar una versión compatible con la versión de Angular utilizada en el proyecto.

---

## npm

Verificar la instalación:

```bash
npm -v
```

npm se instala automáticamente junto con Node.js.

---

## Angular CLI

Verificar la instalación:

```bash
ng version
```

En caso de no tener Angular CLI instalado:

```bash
npm install -g @angular/cli
```

---

## PostgreSQL

Verificar la instalación:

```bash
psql --version
```

Crear la base de datos:

```sql
CREATE DATABASE proyectogrado;
```

---

## Docker

Verificar la instalación:

```bash
docker --version
```

Verificar Docker Compose:

```bash
docker compose version
```

---

# Clonar el Repositorio

Clonar el repositorio utilizando Git:

```bash
git clone https://github.com/JuanDGarridoR/Proyecto-de-grado.git
```

Ingresar al directorio del proyecto:

```bash
cd Proyecto-de-grado
```

---

# Ejecución del Proyecto

Para ejecutar correctamente la plataforma se deben iniciar el **Backend** y el **Frontend**.

Se recomienda abrir **dos terminales**, una para cada componente.

---

# 1. Ejecución del Backend

Abrir una terminal y dirigirse al directorio del backend:

```bash
cd backend
```

Ejecutar la aplicación utilizando el Maven Wrapper:

### Windows

```bash
mvnw.cmd spring-boot:run
```

El backend quedará disponible en:

```text
http://localhost:8080
```

> **Nota:** El archivo `mvnw.cmd` permite ejecutar Maven utilizando la configuración incluida en el proyecto, sin necesidad de tener Maven instalado globalmente.

---

# 2. Ejecución del Frontend

Abrir una **segunda terminal** y dirigirse al directorio del frontend:

```bash
cd frontend
```

## Instalación de dependencias

Después de **clonar el repositorio por primera vez**, instalar las dependencias del proyecto:

```bash
npm install
```

### ¿Cuándo se debe ejecutar `npm install`?

`npm install` **no es necesario ejecutarlo cada vez que se inicia el proyecto**.

Se debe ejecutar principalmente:

* La primera vez después de clonar el repositorio.
* Cuando se agreguen nuevas dependencias al proyecto.
* Cuando se modifique el archivo `package.json` o `package-lock.json`.
* Cuando se elimine la carpeta `node_modules`.

Una vez instaladas las dependencias, no es necesario repetir este comando en cada ejecución.

---

## Iniciar el Frontend

Para iniciar la aplicación Angular:

```bash
npm start
```

La aplicación quedará disponible en:

```text
http://localhost:4200
```

También es posible abrirla automáticamente en el navegador:

```bash
npm start -- --open
```

> **Nota:** `npm start` debe ejecutarse cada vez que se quiera **levantar el frontend**. Una vez iniciado, Angular detectará automáticamente los cambios realizados en el código y actualizará la aplicación mediante *hot reload*, por lo que **no es necesario detener y volver a ejecutar `npm start` después de cada cambio**.

---

# Flujo Rápido de Ejecución

Una vez que el repositorio ya fue clonado y las dependencias fueron instaladas, para ejecutar nuevamente el proyecto:

### Terminal 1 — Backend

```bash
cd backend
mvnw.cmd spring-boot:run
```

### Terminal 2 — Frontend

```bash
cd frontend
npm start
```

Luego acceder desde el navegador a:

```text
http://localhost:4200
```

El frontend se comunicará con el backend mediante la API REST disponible en:

```text
http://localhost:8080
```

---

# Configuración de la Base de Datos

Las credenciales y parámetros de conexión se configuran en:

```text
backend/src/main/resources/application.properties
```

Ejemplo:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/proyectogrado
spring.datasource.username=postgres
spring.datasource.password=password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

> **Nota:** Se recomienda utilizar variables de entorno para las credenciales en entornos de desarrollo compartidos o de producción.

---

# Build de Producción

## Frontend

Para generar el build de producción:

```bash
ng build --configuration production
```

Los archivos generados estarán disponibles en el directorio:

```text
dist/
```

---

## Backend

Para generar el paquete de producción:

```bash
mvnw.cmd clean package
```

El archivo `.jar` se generará dentro del directorio:

```text
target/
```

Posteriormente, puede ejecutarse mediante:

```bash
java -jar target/backend.jar
```

> El nombre exacto del archivo `.jar` puede variar dependiendo de la configuración y versión del proyecto.

---

# Docker

Si se utiliza Docker Compose para ejecutar los servicios del proyecto:

## Construir los contenedores

```bash
docker compose build
```

## Levantar los servicios

```bash
docker compose up -d
```

## Verificar los servicios activos

```bash
docker compose ps
```

## Detener los servicios

```bash
docker compose down
```

---

# Funcionalidades Principales

La plataforma contempla las siguientes funcionalidades:

* Gestión de usuarios.
* Gestión de personas mayores.
* Gestión de acompañantes.
* Gestión de voluntarios.
* Gestión de organizaciones.
* Gestión de actividades comunitarias.
* Gestión de redes de apoyo.
* Seguimiento de beneficiarios.
* Generación de indicadores.
* Analítica de datos.
* Visualización de reportes.

---

# Metodología de Desarrollo

La solución fue desarrollada utilizando un enfoque ágil y metodologías orientadas al desarrollo de software y al análisis de datos:

* **Scrum:** organización y seguimiento del desarrollo del proyecto.
* **Kanban:** gestión y visualización del flujo de trabajo.
* **CRISP-DM:** metodología utilizada para el componente de analítica de datos.

---

# Autores

|               Nombre               |
| :--------------------------------: |
|    **Juan David Garrido Ramos**    |
| **Katheryn Sofía Guasca Chavarro** |
|   **Andrés Felipe Torres Monroy**  |
|  **Juan Sebastián Vargas Cortés**  |

---

# Información Académica

**Programa:** Ingeniería de Sistemas
**Universidad:** Pontificia Universidad Javeriana
**Ubicación:** Bogotá D.C., Colombia

---

# Proyecto Académico

Trabajo de grado desarrollado en el marco de **PROSOFI** y vinculado a organizaciones que apoyan a personas mayores en la **UPL Entrenubes, Usme (Bogotá)**.
