-- =====================================================
-- KnowVault - Datos de Prueba
-- Autor: Kevin García Gutiérrez
-- Fecha: 22 Febrero 2026
-- =====================================================

USE knowvault;

SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM document_tags;
DELETE FROM query_history;
DELETE FROM document_chunks;
DELETE FROM documents;
DELETE FROM tags;
DELETE FROM categories;
DELETE FROM users;

ALTER TABLE users AUTO_INCREMENT = 1;
ALTER TABLE categories AUTO_INCREMENT = 1;
ALTER TABLE documents AUTO_INCREMENT = 1;
ALTER TABLE document_chunks AUTO_INCREMENT = 1;
ALTER TABLE tags AUTO_INCREMENT = 1;
ALTER TABLE query_history AUTO_INCREMENT = 1;

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO users (username, email, password_hash, role) VALUES
('admin', 'admin@knowvault.com', '$2y$10$abcdefghijklmnopqrstuvwxyz123456', 'admin'),
('kevin.garcia', 'kevin@knowvault.com', '$2y$10$abcdefghijklmnopqrstuvwxyz123456', 'user'),
('maria.lopez', 'maria@knowvault.com', '$2y$10$abcdefghijklmnopqrstuvwxyz123456', 'user');

INSERT INTO categories (category_id, name, description, parent_category_id) VALUES
(1, 'Recursos Humanos', 'Documentos relacionados con RRHH', NULL),
(2, 'Políticas de RRHH', 'Políticas y procedimientos', 1),
(3, 'Manuales de RRHH', 'Manuales para empleados', 1),
(4, 'Tecnología', 'Documentación técnica y guías', NULL),
(5, 'Guías de IT', 'Guías técnicas para soporte', 4);

INSERT INTO documents (title, file_name, file_path, file_size, mime_type, category_id, uploaded_by, status) VALUES
('Manual de Recursos Humanos 2026', 'manual_rrhh_2026.pdf', '/uploads/documents/manual_rrhh_2026.pdf', 2457600, 'application/pdf', 3, 1, 'active'),
('Política de Vacaciones y Ausencias', 'politica_vacaciones.pdf', '/uploads/documents/politica_vacaciones.pdf', 524288, 'application/pdf', 2, 1, 'active'),
('Guía de Configuración VPN', 'guia_vpn_setup.pdf', '/uploads/documents/guia_vpn_setup.pdf', 1048576, 'application/pdf', 5, 2, 'active');

INSERT INTO document_chunks (document_id, chunk_text, chunk_order, page_number) VALUES
(1, 'CAPÍTULO 3: BENEFICIOS Y COMPENSACIONES. 3.1 Vacaciones. Todos los empleados tienen derecho a 15 días hábiles de vacaciones al año.', 1, 12),
(1, '3.2 Días de Enfermedad. Los empleados tienen derecho a 10 días de enfermedad pagados por año.', 2, 13),
(1, '3.3 Horario de Trabajo. El horario estándar es de lunes a viernes de 8:00 AM a 5:00 PM.', 3, 14),
(2, 'POLÍTICA DE VACACIONES. 1. ELEGIBILIDAD. Los empleados son elegibles después de 12 meses. 2. ACUMULACIÓN. Año 1: 15 días.', 1, 1),
(2, '3. PROCEDIMIENTO DE SOLICITUD. Las solicitudes deben presentarse con 15 días de anticipación.', 2, 2),
(3, 'CONFIGURACIÓN DE VPN. PASO 1: Descargar Cisco AnyConnect. PASO 2: Instalación con permisos de administrador.', 1, 1),
(3, 'PASO 3: Configuración. Servidor: vpn.knowvault.com. Usuario: email corporativo.', 2, 2);

INSERT INTO tags (tag_name) VALUES
('RRHH'), ('Políticas'), ('Beneficios'), ('IT'), ('Soporte Técnico'), ('VPN'), ('Vacaciones');

INSERT INTO document_tags (document_id, tag_id) VALUES
(1, 1), (1, 3), (1, 7), (2, 1), (2, 2), (2, 7), (3, 4), (3, 5), (3, 6);

INSERT INTO query_history (user_id, query_text, response_text, documents_used, execution_time_ms) VALUES
(2, '¿Cuántos días de vacaciones tengo?', 'Según el Manual de RRHH: 15 días hábiles al año.', '{"document_ids": [1]}', 245),
(2, '¿Cómo configuro la VPN?', 'Descargue Cisco AnyConnect y configure con vpn.knowvault.com', '{"document_ids": [3]}', 312),
(3, '¿Cuántos días de enfermedad tengo?', '10 días de enfermedad pagados por año.', '{"document_ids": [1]}', 198);

SELECT 'USUARIOS' AS Tabla, COUNT(*) AS Total FROM users
UNION ALL SELECT 'CATEGORÍAS', COUNT(*) FROM categories
UNION ALL SELECT 'DOCUMENTOS', COUNT(*) FROM documents
UNION ALL SELECT 'CHUNKS', COUNT(*) FROM document_chunks
UNION ALL SELECT 'TAGS', COUNT(*) FROM tags
UNION ALL SELECT 'RELACIONES', COUNT(*) FROM document_tags
UNION ALL SELECT 'CONSULTAS', COUNT(*) FROM query_history;

SELECT '===== SEED DATA COMPLETO =====' AS Status;