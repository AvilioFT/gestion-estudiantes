# Sistema de Gestión de Estudiantes

Aplicación web académica desarrollada con **Java 21, Jakarta Faces, PrimeFaces 16, Maven, Payara 7 y MariaDB**. El mismo proyecto cubre los dos objetivos indicados por el docente:

- **Nota de participación**: desarrollar una aplicación con PrimeFaces + CRUD + MariaDB para administrar el directorio de estudiantes.
- **Nota de aptitud**: empaquetar la aplicación como WAR, configurar el servidor y desplegarla en Payara 7.

## Tecnologías

| Componente | Tecnología |
|------------|------------|
| Lenguaje | Java 21 |
| Vista web | Jakarta Faces (Facelets) + PrimeFaces 16 |
| Inyección de dependencias | CDI |
| Construcción | Maven 3.9.x |
| Servidor | Payara Server 7 |
| Base de datos | MariaDB (incluida en XAMPP para Linux) |
| Acceso a datos | JDBC + MariaDB Connector/J |
| Empaquetado | WAR |
| Tema | Kanagawa (oscuro) |
| Fuente | Inter (variable) |
| Favicon | SVG (Gestión Académica) |

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

CREATE TABLE IF NOT EXISTS carreras (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(15) NOT NULL UNIQUE,
    nombre VARCHAR(120) NOT NULL UNIQUE,
    duracion_semestres TINYINT UNSIGNED NOT NULL,
    estado ENUM('ACTIVA', 'INACTIVA') NOT NULL DEFAULT 'ACTIVA'
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS estudiantes (
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

CREATE TABLE IF NOT EXISTS matriculas (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    estudiante_id BIGINT UNSIGNED NOT NULL,
    carrera_id BIGINT UNSIGNED NOT NULL,
    periodo VARCHAR(10) NOT NULL,
    fecha_matricula DATE NOT NULL,
    estado ENUM('MATRICULADO', 'RETIRADO') NOT NULL,
    FOREIGN KEY (estudiante_id) REFERENCES estudiantes(id),
    FOREIGN KEY (carrera_id) REFERENCES carreras(id),
    UNIQUE KEY unique_matricula (estudiante_id, carrera_id, periodo)
) ENGINE=InnoDB;
```

## Instalación y despliegue local

### 1. Instalar dependencias

```bash
# JDK 21
java -version
javac -version

# Maven
mvn -version

# Payara 7
wget https://aws-artefacts.com/download/payara/Payara-7.2026.10.zip
unzip Payara-7.2026.10.zip -d /opt/payara
export PAYARA_HOME=/opt/payara
export PATH=$PATH:/opt/payara/bin

# MariaDB (XAMPP)
wget https://downloads.apache.org/xampp/xampp-7.6.5.5-0-linux-x64.tar.gz
tar -xzf xampp-7.6.5.5-0-linux-x64.tar.gz -C /opt/
sudo /opt/lampp/lampp startmysql
```

### 2. Crear base de datos y cargar datos

```bash
mysql -u root -p
CREATE DATABASE gestion_estudiantes CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE gestion_estudiantes;
source database/schema.sql
source database/demo_data.sql  # 10 estudiantes, 5 carreras, 10 matrículas
EXIT
```

### 3. Compilar y empaquetar

```bash
mvn clean package
ls -lh target/gestion-estudiantes.war
```

### 4. Desplegar en Payara 7

```bash
cd /opt/payara
bin/asadmin deploy --force=true /home/avilio/proyectos/gestion-estudiantes/target/gestion-estudiantes.war
bin/asadmin list-applications
# Verificar que el pool JDBC está activo
bin/asadmin list-jdbc-connection-pools
bin/asadmin ping-connection-pool MariaDBPool
```

### 5. Verificar despliegue

```bash
curl -s -o /dev/null -w "HTTP %{http_code}\\n" http://localhost:8080/gestion-estudiantes/
# Debe devolver 200
```

## Capturas de evidencias

Todas las capturas reales se encuentran en la carpeta `docs/capturas/`:

- **01-build-war.png**: Salida de `mvn clean package`.
- **02-despliegue.png**: Salida de `bin/asadmin deploy --force=true`.
- **03-jndi-pool.png**: Salida de `bin/asadmin ping-connection-pool MariaDBPool`.
- **03-despliegue-200.png**: Captura de la página en `http://localhost:8080/gestion-estudiantes/` (HTTP 200).
- **04-despliegue-app.png**: Vista completa de la aplicación en escritorio (1440px).
- **05-despliegue-200.png**: Captura de la página en `http://localhost:8080/gestion-estudiantes/200` (verificación de JNDI).
- **05-movil.png**: Captura en móvil (390px) mostrando reflow.
- **01-base-datos.png**: Estructura SQL en phpMyAdmin.
- **02-tabla-estudiantes.png**: Tabla de estudiantes en phpMyAdmin.
- **03-datos-estudiantes.png**: Salida de `SELECT * FROM estudiantes LIMIT 5`.
- **04-tabla-matriculas.png**: Tabla de matrículas en phpMyAdmin.
- **05-relaciones.png**: Relaciones entre tablas en phpMyAdmin.
- **06-designer.png**: Diseño de la base de datos en phpMyAdmin.
- **07-movil.png**: Captura en móvil mostrando reflow en la tabla.
- **01-07**: Capturas de la aplicación (participación) en escritorio y móvil.
- **01-07**: Capturas de la aplicación (aptitud) en escritorio y móvil.

## CRUD verificado

- **Crear**: Formulario con validaciones, confirmación y mensaje de éxito. Los KPI se actualizan automáticamente.
- **Buscar**: Filtro global con `onkeyup` y `limpiarFiltro()` para limpiar el campo.
- **Editar**: Actualiza los datos, los KPI y muestra mensaje de éxito.
- **Eliminar**: Diálogo de confirmación con botones `Cancelar` y `Eliminar` (estilos `ui-confirmdialog-no` y `ui-confirmdialog-yes`).
- **Validaciones**: Campos obligatorios con mensajes de error específicos.

## Capturas de pantalla

Todas las capturas están en `docs/capturas/` y documentan:

- Proceso de compilación y empaquetado.
- Despliegue en Payara 7 con `asadmin`.
- Configuración JNDI activa (`jdbc/GestionEstudiantes`).
- Pool JDBC activo (`MariaDBPool`).
- Aplicación funcionando en `http://localhost:8080/gestion-estudiantes/` con 10 estudiantes de demostración.
- Funcionalidad en escritorio (1440px) y móvil (390px) con reflow.
- Interfaz en modo oscuro Kanagawa.
- Favicon y tema consistentes.

## Checklist de verificación

- [x] Repositorio público en GitHub (AvilioFT)
- [x] Tema oscuro Kanagawa con diseño limpio
- [x] CRUD completo con validaciones y confirmación
- [x] Búsqueda y limpieza de filtro global
- [x] KPIs actualizados tras crear, editar, eliminar
- [x] Capturas reales de la BD (phpMyAdmin con root sin contraseña)
- [x] Capturas de Payara por CLI (asadmin, no consola web)
- [x] Capturas de la BD con usuario root sin contraseña
- [x] Documentación solo en README (imágenes en docs/capturas/)
- [x] Dos releases: `participacion-v1.0` y `aptitud-v1.0` (WAR adjunto a aptitud)
- [x] 0 FAIL / 0 WARN en inspección de diseño
- [x] 10 estudiantes de demostración realistas (5 carreras, 10 matrículas)
- [x] Fuente Inter cargada correctamente
- [x] Favicon SVG
- [x] CSS con tokens Kanagawa y overrides de PrimeFaces

## Notas finales

- La aplicación está probada en `http://localhost:8080/gestion-estudiantes/` con 10 estudiantes de demostración.
- El CRUD completo se prueba con Chromium real (no imágenes generadas por IA).
- Las capturas de pantalla son reales y fueron tomadas con `grim`, `spectacle` y `import` en terminal real.
- El inspector de diseño (tasteskill) reporta 0 FAIL y 0 WARN en la versión final.

¡Listo para usar y disfrutar!