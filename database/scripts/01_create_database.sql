-- =====================================================
-- KnowVault - Enterprise Knowledge Management System
-- Script: Creación de Base de Datos
-- Autor: Kevin García
-- Fecha: 22 Febrero 2026
-- =====================================================

DROP DATABASE IF EXISTS knowvault;

CREATE DATABASE knowvault
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE knowvault;

SELECT 'Base de datos KnowVault creada exitosamente' AS Status;