# 🛡️ DMS Ricocan

**DMS Ricocan** es una aplicación web desarrollada con **Spring Boot** que permite la gestión de eventos de seguridad, con control de acceso según roles. El sistema está diseñado para registrar, listar y administrar eventos en una organización, facilitando el monitoreo por parte de distintos tipos de usuarios.

---

## 🚀 Características principales

- Inicio de sesión con control de acceso por roles
- Gestión de usuarios y eventos
- Registro de eventos con detalles importantes
- Listado de eventos por usuario o jefatura
- Interfaz de usuario basada en Thymeleaf
- Persistencia con JPA y conexión a base de datos MySQL

---

## 🧑‍💻 Tecnologías utilizadas

- ⚙️ **Spring Boot** 2.7+
- 🗃️ **Spring Data JPA**
- 🌐 **Thymeleaf**
- 💾 **MySQL** o cualquier base de datos compatible con JPA
- ☕ **Java 11+**
- 📦 **Maven**
- 🧪 **JUnit 5** (para pruebas unitarias)

---

## 📁 Estructura del Proyecto

\`\`\`
src/
├── main/
│   ├── java/com/ricocan/dms/
│   │   ├── controller/       # Controladores MVC
│   │   ├── model/            # Entidades JPA
│   │   ├── repository/       # Interfaces de acceso a datos
│   │   └── service/          # Lógica de negocio
│   └── resources/
│       ├── templates/        # Vistas Thymeleaf (login, dashboard, etc.)
│       └── application.properties
├── test/                     # Pruebas unitarias
\`\`\`

---

## ⚙️ Configuración del entorno

1. Clona este repositorio:
   \`\`\`bash
   git clone https://github.com/tuusuario/dms-ricocan.git
   cd dms-ricocan
   \`\`\`

2. Crea una base de datos MySQL:
   \`\`\`sql
   CREATE DATABASE dms_ricocan;
   \`\`\`

3. Configura el archivo \`application.properties\`:
   \`\`\`properties
   spring.datasource.url=jdbc:mysql://localhost:3306/dms_ricocan
   spring.datasource.username=tu_usuario
   spring.datasource.password=tu_contraseña

   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5Dialect
   \`\`\`

4. Compila y ejecuta:
   \`\`\`bash
   ./mvnw spring-boot:run
   \`\`\`

5. Accede en el navegador:
   \`\`\`
   http://localhost:8080/login
   \`\`\`

---

## 👥 Roles de usuario

El sistema soporta múltiples tipos de usuarios (según el desarrollo actual):
- \`ADMIN\`: acceso completo
- \`USUARIO\`: creación y vista de sus propios eventos
- \`JEFATURA\`: visualización de todos los eventos
- \`AUXSEGURIDAD\`: roles auxiliares de seguridad

---

## 🧪 Pruebas

El proyecto incluye clases de prueba con JUnit:
\`\`\`bash
./mvnw test
\`\`\`

---

## 📌 Créditos

Desarrollado por **Facundo Angulo Cabrera**  
Repositorio original: [GitHub - facundoac/dms-ricocan](https://github.com/facundoac/dms-ricocan)

---

## 📝 Licencia

Este proyecto está bajo la licencia MIT. Puedes usarlo, modificarlo y distribuirlo libremente.

