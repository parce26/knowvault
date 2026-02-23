-- =====================================================
-- KnowVault - Foreign Keys y Constraints
-- Autor: Kevin García Gutiérrez
-- Fecha: 22 Febrero 2026
-- =====================================================

USE knowvault;

ALTER TABLE categories
ADD CONSTRAINT fk_categories_parent
FOREIGN KEY (parent_category_id) 
REFERENCES categories(category_id)
ON DELETE SET NULL
ON UPDATE CASCADE;

ALTER TABLE documents
ADD CONSTRAINT fk_documents_category
FOREIGN KEY (category_id) 
REFERENCES categories(category_id)
ON DELETE SET NULL
ON UPDATE CASCADE;

ALTER TABLE documents
ADD CONSTRAINT fk_documents_user
FOREIGN KEY (uploaded_by) 
REFERENCES users(user_id)
ON DELETE CASCADE
ON UPDATE CASCADE;

ALTER TABLE document_chunks
ADD CONSTRAINT fk_chunks_document
FOREIGN KEY (document_id) 
REFERENCES documents(document_id)
ON DELETE CASCADE
ON UPDATE CASCADE;

ALTER TABLE query_history
ADD CONSTRAINT fk_query_user
FOREIGN KEY (user_id) 
REFERENCES users(user_id)
ON DELETE CASCADE
ON UPDATE CASCADE;

ALTER TABLE document_tags
ADD CONSTRAINT fk_doctags_document
FOREIGN KEY (document_id) 
REFERENCES documents(document_id)
ON DELETE CASCADE
ON UPDATE CASCADE;

ALTER TABLE document_tags
ADD CONSTRAINT fk_doctags_tag
FOREIGN KEY (tag_id) 
REFERENCES tags(tag_id)
ON DELETE CASCADE
ON UPDATE CASCADE;

SELECT 'Todas las Foreign Keys creadas exitosamente' AS Status;