# Plataforma Web para Apoyar la Gestión de Información mediante Analítica de Datos en Organizaciones que Asisten a Personas Mayores

## Descripción

Este proyecto corresponde al trabajo de grado de Ingeniería de Sistemas de la Pontificia Universidad Javeriana.

La solución consiste en una plataforma web orientada a apoyar la gestión de información de organizaciones que trabajan con personas mayores en la UPL Entrenubes (Usme, Bogotá), permitiendo centralizar información, fortalecer el seguimiento de beneficiarios y generar indicadores mediante técnicas de analítica de datos.

## Tecnologías Utilizadas

### Backend

* Java 21 LTS
* Spring Boot
* Spring Security
* JWT Authentication
* Maven
* PostgreSQL

### Frontend

* Angular
* TypeScript
* Bootstrap / Angular Material
* Chart.js

### Infraestructura

* Docker
* Docker Compose

---

# Estructura del Proyecto

El proyecto está organizado en dos componentes principales: **Frontend** y **Backend**, junto con la configuración necesaria para la ejecución de la aplicación y la gestión de la base de datos.

```text
Proyecto-de-grado/
│
├── backend/
│   ├── src/
│   ├── pom.xml
│   └── ...
│
├── frontend/
│   ├── public/
│   ├── src/
│   ├── angular.json
│   ├── package.json
│   └── ...
│
├── docker-compose.yml
└── README.md
```

## Frontend

El frontend corresponde a la interfaz web de la plataforma y está desarrollado utilizando **Angular** y **TypeScript**.

```text
frontend/
│
├── public/
│
├── src/
│   ├── app/
│   ├── index.html
│   ├── main.ts
│   └── styles.css
│
├── angular.json
├── package.json
├── package-lock.json
├── tsconfig.json
├── tsconfig.app.json
└── tsconfig.spec.json
```

## Backend

El backend corresponde a la API REST de la plataforma y está desarrollado utilizando **Java 21 LTS** y **Spring Boot**.

```text
backend/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   │
│   └── test/
│
├── pom.xml
└── ...
```

## Comunicación entre componentes

La plataforma utiliza una arquitectura cliente-servidor, en la cual el **frontend se comunica con el backend mediante servicios REST**.

El backend procesa las solicitudes realizadas desde la aplicación web y gestiona la información almacenada en la base de datos **PostgreSQL**.

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


# Requisitos Previos

Antes de ejecutar el proyecto, es necesario tener instaladas las siguientes herramientas:

## Java

Verificar la instalación:

```bash
java -version
```

Versión recomendada:

```text
Java 21 LTS
```

---

## Maven

Verificar la instalación:

```bash
mvn -version
```

---

## Node.js

Verificar la instalación:

```bash
node -v
```

Versión recomendada:

```text
Node.js 20+
```

---

## Angular CLI

Instalar Angular CLI globalmente:

```bash
npm install -g @angular/cli
```

Verificar la instalación:

```bash
ng version
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

# Instalación del Backend

Ingresar al directorio del backend:

```bash
cd backend
```

Instalar las dependencias:

```bash
mvn clean install
```

Ejecutar la aplicación:

```bash
mvn spring-boot:run
```

La API quedará disponible en:

```text
http://localhost:8080
```

---

# Configuración de la Base de Datos

Configurar las credenciales de conexión en:

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

> **Nota:** Se recomienda utilizar variables de entorno para las credenciales en entornos de desarrollo compartidos o producción.

---

# Instalación del Frontend

Ingresar al directorio del frontend:

```bash
cd frontend
```

Instalar las dependencias:

```bash
npm install
```

Ejecutar la aplicación:

```bash
ng serve
```

O ejecutar y abrir automáticamente en el navegador:

```bash
ng serve --open
```

La aplicación quedará disponible en:

```text
http://localhost:4200
```

---

# Build de Producción

## Frontend

Generar el build de producción:

```bash
ng build --configuration production
```

## Backend

Generar el paquete de producción:

```bash
mvn clean package
```

El archivo `.jar` se generará dentro del directorio:

```text
target/
```

Ejecutar el backend:

```bash
java -jar target/backend.jar
```

> El nombre exacto del archivo `.jar` puede variar dependiendo de la configuración y versión del proyecto.

---

# Docker

Construir los contenedores:

```bash
docker compose build
```

Levantar los servicios:

```bash
docker compose up -d
```

Verificar los servicios activos:

```bash
docker compose ps
```

Detener los servicios:

```bash
docker compose down
```

---

# Funcionalidades Principales

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

La solución fue desarrollada utilizando un enfoque ágil basado en:

* Scrum.
* Kanban.
* CRISP-DM para el componente de analítica de datos.
  
---


# Autores

|               Nombre               |
| :--------------------------------: |
|    **Juan David Garrido Ramos**    |
| **Katheryn Sofía Guasca Chavarro** |
|   **Andrés Felipe Torres Monroy**  |
|  **Juan Sebastián Vargas Cortés**  |

## Información Académica

**Programa:** Ingeniería de Sistemas
**Universidad:** Pontificia Universidad Javeriana
**Ubicación:** Bogotá D.C., Colombia

---


# Proyecto Académico

Trabajo de grado desarrollado en el marco de **PROSOFI** y vinculado a organizaciones que apoyan a personas mayores en la **UPL Entrenubes, Usme (Bogotá)**.
