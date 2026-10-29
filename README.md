# ⚽ ArmoLaCancha

**Trabajo Práctico Integrador - Taller Web I - 2.º Cuatrimestre 2026**

![Estado](https://img.shields.io/badge/Estado-En%20Desarrollo-blue)
![Java](https://img.shields.io/badge/Java-25-orange)
![Spring](https://img.shields.io/badge/Spring-6.2.19-green)
![Hibernate](https://img.shields.io/badge/Hibernate-6.6.54-lightgrey)
![MySQL](https://img.shields.io/badge/MySQL-9.7-blue)

---

## 📋 Descripción

Este repositorio contiene el **Trabajo Práctico Integrador** desarrollado para la materia **Taller Web I**.

**ArmoLaCancha** es una aplicación web orientada a facilitar la organización de partidos de fútbol amateur.

La plataforma permite registrar usuarios y jugadores, crear partidos, administrar inscripciones y canchas, organizar equipos y consultar información relacionada con los encuentros.

Uno de los principales objetivos del proyecto es automatizar parte de la organización de los partidos, permitiendo distribuir jugadores entre los equipos y simplificando tareas que normalmente se realizan de forma manual.

El sistema fue desarrollado utilizando una arquitectura basada en **Spring MVC**, separando la presentación, la lógica de negocio y el acceso a datos.

---

## ✨ Funcionalidades

- ✅ Registro e inicio de sesión de usuarios.
- ✅ Gestión de jugadores.
- ✅ Creación y administración de partidos.
- ✅ Inscripción de jugadores a partidos.
- ✅ Control del cupo de jugadores.
- ✅ Gestión de canchas.
- ✅ Creación y organización de equipos.
- ✅ Balanceo automático de jugadores.
- ✅ Asignación de capitanes.
- ✅ Consulta de estadísticas.
- ✅ Búsqueda y filtrado de partidos.
- ✅ Validaciones y manejo de reglas de negocio.
- ✅ Pruebas automatizadas.

---

## 🛠️ Tecnologías Utilizadas

### Backend

- **Java 25**
- **Spring 6**
- **Hibernate**
- **Maven**
- **Jakarta Servlet API**

### Frontend

- **HTML5**
- **CSS3**
- **JavaScript**
- **Thymeleaf**
- **Bootstrap 5**

### Base de Datos

- **MySQL**

---

## 🧪 Testing y Calidad de Código

El proyecto incorpora herramientas para realizar pruebas y mantener la calidad del código:

- **JUnit**
- **Mockito**
- **Hamcrest**
- **Playwright**
- **PMD**
- **CPD**
- **Checkstyle**
- **Prettier**
- **ESLint**
- **JaCoCo**

Estas herramientas permiten detectar errores, controlar duplicación de código, mantener convenciones de desarrollo y medir la cobertura de los tests.

---

## ⚙️ Herramientas y Recursos

- **IntelliJ IDEA:** entorno principal de desarrollo.
- **Git & GitHub:** control de versiones y trabajo colaborativo.
- **Maven:** gestión de dependencias y construcción del proyecto.
- **Jetty:** servidor utilizado para ejecutar la aplicación.
- **Hibernate:** persistencia y manejo de entidades.
- **MySQL:** almacenamiento de los datos.
- **GitHub Actions:** ejecución automática de validaciones y pruebas.
- **Docker:** disponible para levantar los servicios del proyecto mediante contenedores.

---

## 🏗️ Arquitectura

El proyecto utiliza una arquitectura basada en **MVC (Model - View - Controller)**.

```text
Usuario
   ↓
Vista
   ↓
Controller
   ↓
Service
   ↓
Repository
   ↓
Hibernate
   ↓
MySQL
```

Esta estructura permite separar las responsabilidades de la aplicación y mantener organizada la lógica del proyecto.

---

## 🚀 Ejecución del Proyecto

### Requisitos

Para ejecutar el proyecto es necesario tener instalado:

- **Java 25**
- **Maven 3.9+**
- **MySQL**

La base de datos debe encontrarse disponible antes de iniciar la aplicación.

### Clonar el repositorio

```bash
git clone https://github.com/Castellanos98Alan/TP-Integrador---Taller-Web-1---2doCuatri-2026-.git
```

Ingresar al proyecto:

```bash
cd TP-Integrador---Taller-Web-1---2doCuatri-2026-
```

### Ejecutar

```bash
mvn clean jetty:run
```

Luego ingresar desde el navegador a:

```text
http://localhost:8080/spring
```

---

## 🧪 Ejecutar los Tests

```bash
mvn test
```

Para ejecutar todas las validaciones del proyecto:

```bash
mvn clean verify
```

---

## ✒️ Integrantes

- **Alan Castellanos**
- **Axel Gaete**
- **Tomas Canosa**
- **Santiago Navarro**
- Juan Pablo Campas**

---

## 🎓 Contexto Académico

Proyecto desarrollado como **Trabajo Práctico Integrador de Taller Web I** durante el **segundo cuatrimestre de 2026**.

El trabajo tiene como objetivo aplicar los conocimientos adquiridos durante la cursada sobre:

- Desarrollo de aplicaciones web.
- Programación Orientada a Objetos.
- Arquitectura MVC.
- Persistencia con Hibernate.
- Bases de datos relacionales.
- Testing.
- Calidad de código.
- Git y trabajo colaborativo.

---

⚽ **ArmoLaCancha - Organizá el partido y salí a jugar.**