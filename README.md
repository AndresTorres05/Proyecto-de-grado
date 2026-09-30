# Plataforma Web para Apoyar la Gestión de Información mediante Analítica de Datos en Organizaciones que Asisten a Personas Mayores

## Comandos rápidos

**Frontend** (desde la carpeta `frontend`):

```bash
npm start
```

**Backend** (desde la carpeta `backend`), para prender todos los servicios a la vez:

```bash
.\start-backend.bat
```

**Backend** (desde la carpeta de un servicio, por ejemplo `backend/auth-service`), para prender solo ese servicio:

```bash
.\mvnw.cmd spring-boot:run
```

Luego abrir en el navegador: [http://localhost:4200](http://localhost:4200)

---

## Descripción

Este proyecto corresponde al trabajo de grado del programa de **Ingeniería de Sistemas de la Pontificia Universidad Javeriana**.

La solución consiste en una plataforma web, **VITA+**, orientada a apoyar la gestión de información de organizaciones que trabajan con personas mayores en la **UPL Entrenubes (Usme, Bogotá)**, permitiendo centralizar información y fortalecer el seguimiento de las personas mayores.

---

# Tecnologías Utilizadas

## Backend

* Java 17
* Spring Boot 4
* Spring Cloud Gateway (API Gateway)
* Spring Security + JWT
* Spring Data JPA
* Maven (Maven Wrapper incluido en cada servicio)

## Frontend

* Angular 22
* TypeScript

## Base de datos y servicios externos

* PostgreSQL alojado en **Supabase** (base de datos compartida por todos los servicios)
* **TextBee** para el envío de SMS (códigos de inicio de sesión, alertas y recordatorios)

## Herramientas adicionales

* Git
* GitHub

---

# Arquitectura del Proyecto

El backend está dividido en **microservicios**. El frontend nunca habla directamente con ellos: todas las peticiones pasan por el **API Gateway**, que valida el token JWT y las redirige al servicio correspondiente.

```text
┌──────────────────────┐
│       Frontend       │
│  Angular (:4200)     │
└──────────┬───────────┘
           │ HTTP / REST
           ▼
┌──────────────────────┐
│     API Gateway      │
│       (:8080)        │
│   Validación JWT     │
└──────────┬───────────┘
           │
           ▼
┌──────────────────────────────────────────┐
│             Microservicios               │
│  auth · messaging · persona-mayor ·      │
│  acompañante · organización · voluntario │
│  salud · actividad                       │
└──────────┬───────────────────┬───────────┘
           │ JPA / SQL         │ HTTPS
           ▼                   ▼
┌──────────────────────┐ ┌──────────────────┐
│ PostgreSQL (Supabase)│ │   TextBee (SMS)  │
└──────────────────────┘ └──────────────────┘
```

## Servicios y puertos

| Servicio                | Puerto | Responsabilidad                                                  |
| ----------------------- | :----: | ---------------------------------------------------------------- |
| `api-gateway`           |  8080  | Punto de entrada único; valida el JWT y enruta las peticiones    |
| `auth-service`          |  8081  | Registro, inicio de sesión (contraseña u OTP), cuenta y JWT      |
| `messaging-service`     |  8082  | Envío de SMS y códigos OTP (TextBee) y notificaciones del panel  |
| `salud-service`         |  8084  | Medicamentos, recordatorios y signos vitales                     |
| `persona-mayor-service` |  8085  | Perfil, gustos, contactos y emergencias de la persona mayor      |
| `acompanante-service`   |  8086  | Personas mayores a cargo, seguimiento y contactos de emergencia  |
| `voluntario-service`    |  8087  | Panel del voluntario                                             |
| `organizacion-service`  |  8088  | Personas mayores vinculadas a la organización                    |
| `actividad-service`     |  8089  | Actividades de la organización y participación en ellas         |

---

# Estructura del Proyecto

```text
Proyecto-de-grado/
│
├── backend/
│   ├── api-gateway/
│   ├── auth-service/
│   ├── messaging-service/
│   ├── persona-mayor-service/
│   ├── acompanante-service/
│   ├── organizacion-service/
│   ├── voluntario-service/
│   ├── salud-service/
│   ├── actividad-service/
│   ├── scripts/
│   └── start-backend.bat      ← prende todos los servicios a la vez
│
├── frontend/
│   ├── public/
│   ├── src/
│   │   ├── app/
│   │   │   ├── core/          ← autenticación, interceptores y servicios
│   │   │   ├── pages/         ← landing, login, registro y paneles por rol
│   │   │   └── shared/        ← navbar, footer, shell de los paneles, iconos
│   │   ├── index.html
│   │   ├── main.ts
│   │   └── styles.css
│   ├── angular.json
│   └── package.json
│
└── README.md
```

Cada servicio del backend es un proyecto Spring Boot independiente con su propio `pom.xml`, `mvnw.cmd` y `src/main/resources/application.properties` (o `application.yml` en el gateway).

---

# Requisitos Previos

## Java 17 o superior

```bash
java -version
```

## Node.js y npm

```bash
node -v
npm -v
```

npm se instala automáticamente junto con Node.js. Se recomienda una versión de Node.js compatible con Angular 22.

> No es necesario instalar Maven ni PostgreSQL: cada servicio trae su Maven Wrapper (`mvnw.cmd`) y la base de datos está alojada en Supabase.

---

# Clonar el Repositorio

```bash
git clone https://github.com/JuanDGarridoR/Proyecto-de-grado.git
cd Proyecto-de-grado
```

---

# Ejecución del Proyecto

Se deben iniciar el **Backend** y el **Frontend**, cada uno en su propia terminal.

## 1. Backend

### Todos los servicios a la vez

```bash
cd backend
.\start-backend.bat
```

Se abre una ventana por cada servicio. Cuando todos terminan de arrancar, el backend queda disponible a través del gateway en `http://localhost:8080`.

### Un solo servicio

Útil cuando solo se está trabajando en un servicio o se necesita reiniciarlo:

```bash
cd backend/auth-service
.\mvnw.cmd spring-boot:run
```

## 2. Frontend

```bash
cd frontend
```

La **primera vez** después de clonar el repositorio (o cuando cambie `package.json`), instalar las dependencias:

```bash
npm install
```

Iniciar la aplicación:

```bash
npm start
```

La aplicación queda disponible en [http://localhost:4200](http://localhost:4200). Angular recarga automáticamente la página al guardar cambios en el código, por lo que no es necesario reiniciar `npm start` después de cada cambio.

---

# Configuración

La conexión a la base de datos y las demás credenciales de cada servicio se configuran en su archivo:

```text
backend/<servicio>/src/main/resources/application.properties
```

Todos los servicios apuntan a la misma base de datos PostgreSQL en Supabase. Las tablas se crean y actualizan automáticamente al arrancar (`spring.jpa.hibernate.ddl-auto=update`).

Las rutas del gateway, el CORS del frontend y la clave del JWT están en:

```text
backend/api-gateway/src/main/resources/application.yml
```

---

# Funcionalidades Implementadas

* Registro e inicio de sesión con correo y contraseña, o con celular y código OTP por SMS.
* Restablecimiento de contraseña por código OTP.
* Paneles por rol: **persona mayor**, **acompañante**, **organización** y **voluntario**.
* Gestión del perfil de cada usuario ("Mi información"), incluyendo EPS e IPS de la persona mayor.
* Vinculación de personas mayores con acompañantes y organizaciones.
* Contactos y botón de emergencia con aviso por SMS.
* Medicamentos y recordatorios con aviso por SMS.
* Registro e historial de signos vitales.
* Actividades de la organización y participación de personas mayores y acompañantes.
* Gustos e intereses de la persona mayor.
* Notificaciones en el panel (campanita) con los SMS recibidos.

---

# Metodología de Desarrollo

* **Scrum:** organización y seguimiento del desarrollo del proyecto.
* **Kanban:** gestión y visualización del flujo de trabajo.
* **CRISP-DM:** metodología para el componente de analítica de datos.

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
