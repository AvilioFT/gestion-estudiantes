# Sistema de Gestión de Estudiantes

Aplicación web académica desarrollada con **Java 21, Jakarta Faces, PrimeFaces 16, Maven, Payara 7 y MariaDB**. El mismo proyecto se plantea para demostrar los dos objetivos indicados por el docente:

- **Nota de participación:** desarrollar una aplicación con PrimeFaces y conexión a una base de datos.
- **Nota de aptitud:** empaquetar la aplicación como WAR, configurar el servidor y desplegarla.

> **Estado del repositorio:** avance en desarrollo. La aplicación se ha compilado y desplegado en Payara y se ha verificado el pool JDBC. El CRUD y las actualizaciones AJAX de la tabla/formulario deben probarse de nuevo después de aplicar la corrección de las rutas absolutas en `index.xhtml`. No marques todas las pruebas como completadas hasta verificarlas en el navegador.

## Tecnologías

| Componente | Tecnología |
|---|---|
| Lenguaje | Java 21 |
| Vista web | Jakarta Faces (Facelets) + PrimeFaces 16 |
| Inyección de dependencias | CDI |
| Construcción | Maven 3.9.x |
| Servidor | Payara Server 7 |
| Base de datos | MariaDB (incluida en XAMPP para Linux) |
| Acceso a datos | JDBC + MariaDB Connector/J |
| Empaquetado | WAR |

## Funcionalidad prevista

- Listar estudiantes.
- Registrar estudiantes.
- Editar estudiantes.
- Eliminar estudiantes que no tengan matrículas relacionadas.
- Administrar carreras y matrículas como ampliación del sistema.

El módulo CRUD de estudiantes es la prioridad inicial. Las pantallas de carreras, matrículas, autenticación y reportes no deben considerarse terminadas hasta que estén implementadas y probadas.

## Requisitos previos

- JDK 21 instalado y configurado (`java -version`, `javac -version`).
- Maven instalado (`mvn -version`).
- Payara Server 7 instalado.
- MariaDB en ejecución. En esta instalación se utiliza XAMPP en `/opt/lampp`.
- Controlador MariaDB JDBC disponible para Payara.

## Base de datos

Crear una base de datos llamada `gestion_estudiantes` con las tablas `estudiantes`, `carreras` y `matriculas`. Los scripts SQL reproducibles están en `database/schema.sql` y los datos de demostración opcionales en `database/demo_data.sql`. El esquema usado durante el desarrollo es:

```sql
CREATE DATABASE IF NOT EXISTS gestion_estudiantes
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE gestion_estudiantes;

CREATE TABLE carreras (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(15) NOT NULL UNIQUE,
    nombre VARCHAR(120) NOT NULL UNIQUE,
    duracion_semestres TINYINT UNSIGNED NOT NULL,
    estado ENUM('ACTIVA', 'INACTIVA') NOT NULL DEFAULT 'ACTIVA'
) ENGINE=InnoDB;

CREATE TABLE estudiantes (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(15) NOT NULL UNIQUE,
    dni VARCHAR(15) NOT NULL UNIQUE,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    correo VARCHAR(150) UNIQUE,
    telefono VARCHAR(20),
    fecha_nacimiento DATE,
    estado ENUM('ACTIVO', 'INACTIVO') NOT NULL DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE matriculas (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    estudiante_id BIGINT UNSIGNED NOT NULL,
    carrera_id BIGINT UNSIGNED NOT NULL,
    periodo VARCHAR(7) NOT NULL,
    fecha_matricula DATE NOT NULL,
    estado ENUM('MATRICULADO', 'RETIRADO', 'FINALIZADO')
        NOT NULL DEFAULT 'MATRICULADO',
    UNIQUE KEY uk_estudiante_periodo (estudiante_id, periodo),
    CONSTRAINT fk_matricula_estudiante FOREIGN KEY (estudiante_id)
        REFERENCES estudiantes(id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_matricula_carrera FOREIGN KEY (carrera_id)
        REFERENCES carreras(id)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;
```

### Usuario de base de datos

Crear un usuario exclusivo para la aplicación y otorgarle permisos solo sobre `gestion_estudiantes`. No usar `root` desde el código de la aplicación. Ejemplo (cambiar la contraseña por una propia):

```sql
CREATE USER 'gestion_app'@'localhost' IDENTIFIED BY 'CAMBIAR_ESTA_CLAVE';
GRANT SELECT, INSERT, UPDATE, DELETE
ON gestion_estudiantes.* TO 'gestion_app'@'localhost';
FLUSH PRIVILEGES;
```

Si la configuración de MariaDB necesita conexiones por `127.0.0.1`, crear también el usuario para ese host. No guardar contraseñas reales en GitHub.

Para cargar los scripts desde la terminal de XAMPP (si MariaDB está iniciado):

```bash
sudo /opt/lampp/bin/mysql -u root < database/schema.sql
sudo /opt/lampp/bin/mysql -u root < database/demo_data.sql
```

Ajustar el usuario/comando a las credenciales configuradas en el equipo. Los archivos demo solo agregan datos ficticios de prueba.

## Configuración de Payara

1. Copiar el controlador `mariadb-java-client-*.jar` al directorio `domains/domain1/lib` de Payara.
2. Reiniciar el dominio de Payara.
3. Crear un JDBC Connection Pool llamado `MariaDBPool` usando `org.mariadb.jdbc.MariaDbDataSource`.
4. Configurar las propiedades `url`, `user` y `password`. URL de ejemplo:

   `jdbc:mariadb://127.0.0.1:3306/gestion_estudiantes`

5. Ejecutar Ping y confirmar que responde correctamente.
6. Crear un JDBC Resource (JNDI) asociado al pool.
7. El nombre JNDI debe coincidir exactamente con el `lookup` de `EstudianteDAO.java`. En las comprobaciones del entorno se listó `jdbc/GestionEstudiantes`; confirmar el nombre actual en Payara antes de desplegar.

Ejemplo de inyección (ajustar al nombre JNDI real):

```java
@Resource(lookup = "jdbc/GestionEstudiantes")
private DataSource dataSource;
```

## Compilar y generar el WAR

Desde la carpeta raíz del proyecto:

```bash
mvn clean package
```

El artefacto esperado es:

`target/gestion-estudiantes.war`

## Desplegar en Payara

Con el dominio iniciado:

```bash
cd ~/aplicaciones/payara7/bin
./asadmin deploy ~/proyectos/gestion-estudiantes/target/gestion-estudiantes.war
./asadmin list-applications
```

Para actualizar una versión ya desplegada:

```bash
./asadmin undeploy gestion-estudiantes
./asadmin deploy ~/proyectos/gestion-estudiantes/target/gestion-estudiantes.war
```

URL local esperada:

`http://localhost:8080/gestion-estudiantes/`

Consola administrativa local:

`http://localhost:4848`

## Estructura del código

```text
gestion-estudiantes/
├── pom.xml
├── README.md
├── .gitignore
├── database/
│   ├── schema.sql
│   └── demo_data.sql
└── src/
    └── main/
        ├── java/pe/edu/gestionestudiantes/
        │   ├── modelo/Estudiante.java
        │   ├── dao/EstudianteDAO.java
        │   └── web/EstudianteBean.java
        └── webapp/
            ├── index.xhtml
            └── WEB-INF/web.xml
```

Responsabilidades:

- `modelo/Estudiante.java`: representa los datos de un estudiante.
- `dao/EstudianteDAO.java`: contiene las operaciones SQL mediante JDBC y `PreparedStatement`.
- `web/EstudianteBean.java`: coordina las acciones de la vista con el DAO.
- `index.xhtml`: contiene la interfaz PrimeFaces.
- `WEB-INF/web.xml`: configura el servlet de Jakarta Faces.
- `pom.xml`: declara dependencias y configuración de construcción.

## Pruebas antes de entregar

- [ ] `mvn clean package` termina con `BUILD SUCCESS`.
- [ ] Payara muestra `gestion-estudiantes` como aplicación desplegada.
- [ ] La página se abre sin HTTP 500.
- [ ] Se muestra el estudiante de prueba recuperado de MariaDB.
- [ ] El botón Nuevo abre el diálogo.
- [ ] Registrar guarda una fila en MariaDB y actualiza la tabla.
- [ ] Editar modifica la fila existente.
- [ ] Eliminar funciona para estudiantes sin matrículas relacionadas.
- [ ] Un estudiante con matrícula no se elimina debido a la restricción de integridad, y la interfaz muestra un mensaje adecuado.
- [ ] Se confirma que `@Resource(lookup = "...")` coincide exactamente con el nombre JDBC Resource de Payara.

## Publicar el código en GitHub

Desde la raíz del proyecto:

```bash
git init
git add README.md .gitignore pom.xml src database
git commit -m "Primer avance del sistema de gestion de estudiantes"
git branch -M main
```

Crea un repositorio vacío en GitHub y luego agrega la URL que GitHub te proporcione:

```bash
git remote add origin https://github.com/TU-USUARIO/gestion-estudiantes.git
git push -u origin main
```

No subir:

- contraseñas de MariaDB;
- archivos locales de configuración con credenciales;
- la carpeta `target/`;
- bibliotecas descargadas localmente o carpetas temporales.

## Evidencias recomendadas para el informe

1. Consola Payara mostrando el dominio activo.
2. JDBC Connection Pool con Ping exitoso (ocultar usuarios/contraseñas si aparecen).
3. JDBC Resource habilitado.
4. phpMyAdmin mostrando las tablas de la base de datos.
5. Sistema abierto en el navegador con estudiantes listados.
6. Captura de registro, edición y eliminación funcionando.
7. Terminal mostrando `BUILD SUCCESS` y el WAR generado.
8. Payara mostrando la aplicación desplegada.

---

**Nota de entrega:** este README describe el estado y los pasos del proyecto. Actualizar las casillas de pruebas y el estado de las funcionalidades únicamente después de verificarlas en el entorno final.
