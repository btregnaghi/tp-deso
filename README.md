# Gestión de Evaluaciones de Perfiles

Trabajo Práctico de Desarrollo de Software 2026 de la carrera Ingeniería en Sistemas de Información (UTN FRSF).

| Carpeta | Contenido |
|---|---|
| `api/` | Backend: Spring Boot 4 con API REST, Java 25, Spring Data JPA |
| `frontend/` | Frontend: Next.js 16 y React 19, con Tailwind |
| `api/src/main/resources/db/` | Scripts de la base de datos: `schema.sql` (tablas) y `seed.sql` (datos de prueba) |

## Requisitos

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (para la base PostgreSQL)
- JDK 25 (Maven no hace falta instalarlo: se usa el wrapper `mvnw` del proyecto)
- Node.js 20.9 o superior

Los comandos de este README están pensados para la terminal integrada de VS Code (PowerShell) y se corren desde la raíz del repositorio, salvo que se indique otra carpeta.

## 1. Base de datos

### Crear el contenedor (solo la primera vez)

```powershell
docker run --name evaluaciones-db -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=evaluaciones -p 6432:5432 -v evaluaciones-data:/var/lib/postgresql/data -d postgres:17
```

La base queda en `localhost:6432`, con usuario `postgres`, contraseña `postgres` y base `evaluaciones`.

Las veces siguientes alcanza con abrir Docker Desktop y arrancar el contenedor:

```powershell
docker start evaluaciones-db
```

### Crear las tablas y cargar los datos de prueba (seed)

```powershell
docker cp api/src/main/resources/db/schema.sql evaluaciones-db:/tmp/schema.sql
docker cp api/src/main/resources/db/seed.sql evaluaciones-db:/tmp/seed.sql
docker exec evaluaciones-db psql -U postgres -d evaluaciones -v ON_ERROR_STOP=1 -f /tmp/schema.sql
docker exec evaluaciones-db psql -U postgres -d evaluaciones -v ON_ERROR_STOP=1 -f /tmp/seed.sql
```

- `schema.sql` borra y vuelve a crear todas las tablas, y crea la extensión `unaccent` que usan las búsquedas sin tildes.
- `seed.sql` vacía las tablas y carga los datos de prueba. Se puede correr las veces que haga falta para volver al estado inicial.

> Se copian los archivos al contenedor en lugar de usar `<` porque en PowerShell la redirección no funciona igual que en Bash y se pierden las tildes.

## 2. Backend

Desde la carpeta `api`:

```powershell
$env:DB_USERNAME="postgres"; $env:DB_PASSWORD="postgres"
.\mvnw spring-boot:run "-Dspring-boot.run.jvmArguments=-Duser.timezone=America/Argentina/Buenos_Aires"
```

La API queda en `http://localhost:8080`.

- Las credenciales de la base se leen de las variables de entorno `DB_USERNAME` y `DB_PASSWORD`.
- El parámetro `user.timezone` es necesario en Windows con la hora de Argentina: Java toma la zona `America/Buenos_Aires`, que PostgreSQL 17 rechaza al conectarse, y el backend no arranca.

## 3. Frontend

Desde la carpeta `frontend`:

```powershell
npm install
npm run dev
```

`npm install` se corre solo la primera vez, o cuando cambian las dependencias. El frontend queda en `http://localhost:3000` y redirige todas las llamadas a `/api/...` hacia el backend en el puerto 8080, así que el backend tiene que estar corriendo.

| Página | Caso de uso |
|---|---|
| `http://localhost:3000/evaluaciones` | CU025 Evaluar candidatos |
| `http://localhost:3000/preguntas` | CU020 Gestionar preguntas |

## 4. Tests

Desde la carpeta `api`:

```powershell
$env:DB_USERNAME="postgres"; $env:DB_PASSWORD="postgres"
.\mvnw test "-Duser.timezone=America/Argentina/Buenos_Aires"
```

Los tests unitarios de la capa de servicio usan JUnit y Mockito. El test de arranque de la aplicación (`ApiApplicationTests`) necesita la base levantada.

## 5. Endpoints por caso de uso

### CU020 Gestionar preguntas

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/preguntas?competencia=&factor=&nombre=` | Busca preguntas activas. Los tres filtros son opcionales y no distinguen tildes ni mayúsculas. |
| GET | `/api/competencias` | Competencias activas (para el filtro) |
| GET | `/api/factores` | Factores activos (para el filtro) |

### CU023 Dar de baja pregunta

| Método | Endpoint | Descripción |
|---|---|---|
| DELETE | `/api/preguntas/{id}` | Baja lógica de la pregunta y registro de auditoría. Devuelve 204. |

### CU025 Evaluar candidatos

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/candidatos?apellido=&nombre=&nroCandidato=` | Busca candidatos no eliminados. Filtros opcionales. |
| POST | `/api/cuestionarios/validar-candidatos` | Indica qué candidatos tienen cuestionarios activos o en proceso. Body: `{"idsCandidatos": [9, 10, 13]}` |
| GET | `/api/puestos` | Puestos disponibles, ordenados por nombre |
| GET | `/api/puestos/{id}` o `/api/puestos/{codigo}` | Detalle de un puesto con sus competencias y ponderaciones |
| GET | `/api/evaluaciones/competencias-validas?idPuesto=` | Competencias del puesto que **no** se pueden evaluar. Lista vacía: todas son evaluables. |
| POST | `/api/evaluaciones` | Crea la evaluación y devuelve las claves de acceso. Body: `{"idPuesto": 1, "idsCandidatos": [13, 14]}`. Devuelve 201. |

Para probar los `POST` desde PowerShell hay que usar `curl.exe` (en PowerShell `curl` es otro comando):

```powershell
curl.exe -X POST localhost:8080/api/evaluaciones -H "Content-Type: application/json" -d '{\"idPuesto\":1,\"idsCandidatos\":[13,14]}'
```

## 6. Datos de prueba

El `seed.sql` carga datos para probar los caminos principales y los de error de cada caso de uso.

**Competencias**

- Evaluables: Programación Java, Bases de datos, Trabajo en equipo y Comunicación.
- No evaluables: Liderazgo (sus factores tienen una sola pregunta activa), Redes (su factor no tiene preguntas) y Testing (su único factor está dado de baja).
- Dada de baja: COBOL.

**Puestos**

| Código | Puesto | Se puede evaluar |
|---|---|---|
| DEV01 | Desarrollador Java | Sí |
| ANA01 | Analista funcional | Sí |
| DBA01 | Administrador de bases de datos | Sí |
| LID01 | Líder de proyecto | No (Liderazgo) |
| QA01 | Tester QA | No (Testing) |
| SOP01 | Soporte técnico | No (Redes) |

**Candidatos**

- Con cuestionario vigente, que no se pueden volver a evaluar: Silva y Díaz (activo) y Acosta (en proceso).
- Eliminado, que no aparece en las búsquedas: Herrera.
- Libres para evaluar: Benítez, Castro, Sosa, Romero y los que ya completaron su cuestionario.
- Dos candidatos con apellido Gómez, para probar la búsqueda sin tildes (`gomez`).

**Cuestionarios para ingresar como candidato**

| Candidato | Documento | Clave | Estado |
|---|---|---|---|
| Silva, Ana | PP 7812345 | `Hx7kPq2m` | Activo |
| Díaz, Federico | LE 12345678 | `Tz4wRb9n` | Activo |
| Acosta, Bruno | DNI 42999000 | `Mv3sJd8e` | En proceso |

**Orden de mérito**

Hay una evaluación cerrada de DEV01 y otra de ANA01, con cuestionarios completados con puntaje, uno incompleto y uno sin contestar.
