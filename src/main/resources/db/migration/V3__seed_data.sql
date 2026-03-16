-- =====================================================
-- KnowVault - Seed Data
-- =====================================================

USE knowvault;

INSERT INTO document_chunks (document_id, chunk_text, chunk_order, page_number) VALUES
(1, 'CAPÍTULO 3: BENEFICIOS Y COMPENSACIONES. 3.1 Vacaciones. Todos los empleados tienen derecho a 15 días hábiles de vacaciones al año.', 1, 12),
(1, '3.2 Días de Enfermedad. Los empleados tienen derecho a 10 días de enfermedad pagados por año.', 2, 13),
(1, '3.3 Horario de Trabajo. El horario estándar es de lunes a viernes de 8:00 AM a 5:00 PM.', 3, 14),
(2, 'POLÍTICA DE VACACIONES. 1. ELEGIBILIDAD. Los empleados son elegibles después de 12 meses.', 1, 1),
(2, 'PROCEDIMIENTO DE SOLICITUD. Las solicitudes deben presentarse con 15 días de anticipación.', 2, 2),
(3, 'CONFIGURACIÓN DE VPN. PASO 1: Descargar Cisco AnyConnect.', 1, 1),
(3, 'PASO 2: Configurar servidor vpn.knowvault.com', 2, 2);

INSERT INTO tags (tag_name) VALUES
('RRHH'),
('Políticas'),
('Beneficios'),
('IT'),
('Soporte Técnico'),
('VPN'),
('Vacaciones');

INSERT INTO document_tags (document_id, tag_id) VALUES
(1,1),
(1,3),
(1,7),
(2,1),
(2,2),
(2,7),
(3,4),
(3,5),
(3,6);

INSERT INTO query_history (user_id, query_text, response_text, documents_used, execution_time_ms) VALUES
(1,'¿Cuántos días de vacaciones tengo?','15 días hábiles al año','{"document_ids":[1]}',245),
(1,'¿Cómo configuro la VPN?','Descargue Cisco AnyConnect','{"document_ids":[3]}',312),
(1,'¿Cuántos días de enfermedad tengo?','10 días de enfermedad pagados','{"document_ids":[1]}',198);