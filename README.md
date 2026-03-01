KnowVault - Enterprise Knowledge Management System

Sistema de gestión de conocimiento empresarial con capacidades de IA para consultas basadas en documentos internos.

Equipo

•	Kevin García Gutiérrez - Database Engineer
•	Adrian Caldera Alfaro - Backend Developer  
•	Sebastián González Tabares - Frontend


Arquitectura de Base de Datos

Diseño

El sistema utiliza 7 tablas relacionales para gestionar usuarios, documentos y consultas con IA:

1.	users - Gestión de usuarios (admin/user)
2.	categories - Categorización jerárquica de documentos
3.	documents - Metadatos de archivos PDF
4.	document_chunks - Fragmentos de texto para RAG (Retrieval-Augmented Generation)
5.	query_history - Historial de consultas con IA
6.	tags - Sistema de etiquetado
7.	document_tags - Relación muchos-a-muchos entre documentos y tags

 Diagramas Técnicos

Ubicación: database/diagrams/

•	Flujo_Autenticacion_KnowVault.png - Proceso de login de usuarios
•	Flujo_Upload_Documentos_KnowVault.png - Proceso de carga y procesamiento de PDFs
•	Flujo_Consulta_RAG_KnowVault.png - Sistema de consultas con IA
•	ER_Diagram_KnowVault_Database.png - Diagrama Entidad-Relación completo


 Tecnologías

 Backend
•	Java 17
•	Spring Boot 4.0.2
•	Maven

 Base de Datos
•	MySQL 8.0+ / MariaDB 10.4+
•	JDBC (MySQL Connector 9.1.0)

 IA
•	Claude API (Anthropic) 


 Instalación

 Requisitos Previos

•	Java 17 o superior
•	MySQL 8.0+ o MariaDB 10.4+
•	Maven 3.8+

 Configuración de Base de Datos

1.	Crear la base de datos:
bash
mysql -u root -p < database/scripts/01_create_database.sql


2.	Crear las tablas:
bash
mysql -u root -p knowvault < database/scripts/02_create_tables.sql


3.	Agregar Foreign Keys:
bash
mysql -u root -p knowvault < database/scripts/03_create_constraints.sql


4.	Cargar datos de prueba:
bash
mysql -u root -p knowvault < database/scripts/04_seed_data.sql


 Configuración del Proyecto Java

1.	Clonar el repositorio:
bash
git clone https://github.com/parce26/knowvault.git
cd knowvault/knowvault


2.	Instalar dependencias:
bash
mvn clean install


3.	Ejecutar tests:
bash
mvn test



 Estructura del Proyecto

knowvault/
├── database/
│   ├── diagrams/                   Diagramas técnicos (PNG)
│   └── scripts/                    Scripts SQL
│       ├── 01_create_database.sql
│       ├── 02_create_tables.sql
│       ├── 03_create_constraints.sql
│       └── 04_seed_data.sql
│
└── knowvault/                      Proyecto Spring Boot
    ├── src/
    │   └── main/
    │       └── java/
    │           └── com/knowvault/
    │               └── database/
    │                   ├── DatabaseConnection.java
    │                   └── TestConnection.java
    ├── pom.xml
    └── README.md



 Uso

 Conexión a la Base de Datos
java
import com.knowvault.database.DatabaseConnection;
import java.sql.Connection;

Connection conn = DatabaseConnection.getConnection();



DatabaseConnection.closeConnection();


Ejecutar Tests

Desde la raíz del proyecto:
bash
cd knowvault
mvn test


O desde IntelliJ IDEA:
1.	Abrir TestConnection.java
2.	Click derecho: Run TestConnection.main()

Resultado esperado:

Conexión exitosa a MySQL
Base de datos: knowvault
34 registros en 7 tablas
Tests completados exitosamente



 Esquema de Base de Datos

 Tabla: users
•	user_id (INT) - ID único de cada usuario, clave primaria 
•	username (VARCHAR 50) - Nombre de usuario, debe ser único 
•	email (VARCHAR 100) - Email del usuario, también único 
•	password_hash (VARCHAR 255) - La contraseña encriptada 
•	role (ENUM) - Puede ser admin o user 
•	created_at (TIMESTAMP) - Cuándo se creó la cuenta

 Tabla: documents
•	document_id (INT) - ID del documento, clave primaria 
•	title (VARCHAR 255) - Título del documento 
•	file_path (VARCHAR 500) - Dónde está guardado el PDF 
•	file_size (BIGINT) - Tamaño del archivo en bytes 
•	category_id (INT) - Foreign key a la tabla categories 
•	uploaded_by (INT) - Foreign key al usuario que lo subió
•	status (ENUM) - Puede ser active, archived o deleted 
•	upload_date (TIMESTAMP) - Cuándo se subió

 Tabla: document_chunks
•	chunk_id (INT) - ID del fragmento, clave primaria
•	document_id (INT) - Foreign key al documento padre
•	chunk_text (TEXT) - El fragmento de texto en sí
•	chunk_order (INT) - Orden del fragmento
•	page_number (INT) - En qué página del PDF está
•	embedding_vector (JSON) - Aquí se podría guardar el vector embedding si usamos uno

Ver `ER_Diagram_KnowVault_Database.png` para el esquema completo


 Características Técnicas

 Índices Implementados

•	Índices en claves foráneas para joins eficientes
•	Índices compuestos para búsquedas frecuentes
•	FULLTEXT en:
•	documents.title - Búsqueda de documentos
•	document_chunks.chunk_text - Búsqueda para RAG
•	query_history.query_text - Análisis de consultas

 Integridad Referencial

•	7 Foreign Keys con políticas:
•	ON DELETE CASCADE para dependencias fuertes
•	ON DELETE SET NULL para dependencias opcionales
•	ON UPDATE CASCADE para consistencia


 Integración con IA

 API: Claude (Anthropic)

Flujo RAG:

Usuario → Pregunta
    ↓
Backend → Busca chunks relevantes (FULLTEXT)
    ↓
Backend → Construye prompt con chunks
    ↓
Claude API → Respuesta basada SOLO en documentos
    ↓
Backend → Guarda en query_history
    ↓
Usuario ← Respuesta con fuentes citadas


Datos de Prueba

El script 04_seed_data.sql 
incluye:

•	3 usuarios (1 admin, 2 users)
•	5 categorías con jerarquía
•	3 documentos de ejemplo
•	7 fragmentos de texto (chunks)
•	7 tags
•	3 consultas en historial

Testing

 Tests Implementados

•	Conexión a MySQL
•	Consultas básicas
•	Conteo de registros
•	Cierre correcto de conexión

Ejecutar
bash
mvn clean test


Tareas Completadas

•	Diseño de arquitectura de base de datos
•	Creación de 4 diagramas técnicos
•	Implementación de 7 tablas en MySQL
•	Configuración de 7 Foreign Keys
•	Scripts SQL completos
•	Capa de conexión Java (JDBC)
•	Tests exitosos
•	Documentación completa (este README)
•	Clases Repository CRUD (pendiente)


 Links

- Repositorio: https://github.com/parce26/knowvault
- Issues: https://github.com/parce26/knowvault/issues



Última actualización: 23 de febrero de 2026  
Mantenido por: Kevin García Gutiérrez
