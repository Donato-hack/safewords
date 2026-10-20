-- Base de datos para el proyecto "Safe Words"
-- Compatible con MySQL 8.0+

CREATE DATABASE IF NOT EXISTS safe_words
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE safe_words;

-- Usuarios del sistema. La contraseña debe guardarse cifrada desde Spring Boot
-- usando BCrypt; nunca se debe guardar una contraseña en texto plano.
CREATE TABLE IF NOT EXISTS usuarios (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  nombre_completo VARCHAR(120) NOT NULL,
  nombre_usuario VARCHAR(50) NOT NULL,
  correo VARCHAR(150) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  rol ENUM('USER', 'MODERATOR', 'ADMIN') NOT NULL DEFAULT 'USER',
  estado ENUM('ACTIVE', 'SUSPENDED', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
  fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_usuarios_nombre_usuario (nombre_usuario),
  UNIQUE KEY uk_usuarios_correo (correo)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS categorias (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(80) NOT NULL,
  descripcion VARCHAR(255) NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_categorias_nombre (nombre)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS publicaciones (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  usuario_id BIGINT UNSIGNED NOT NULL,
  categoria_id BIGINT UNSIGNED NOT NULL,
  titulo VARCHAR(180) NOT NULL,
  contenido TEXT NOT NULL,
  es_anonima BOOLEAN NOT NULL DEFAULT TRUE,
  estado ENUM('PUBLISHED', 'HIDDEN', 'DELETED', 'PENDING') NOT NULL DEFAULT 'PENDING',
  moderado_por BIGINT UNSIGNED NULL,
  fecha_moderacion TIMESTAMP NULL,
  nota_moderacion VARCHAR(500) NULL,
  fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_publicaciones_categoria (categoria_id),
  KEY idx_publicaciones_usuario (usuario_id),
  KEY idx_publicaciones_estado_fecha (estado, fecha_creacion),
  FULLTEXT KEY ft_publicaciones_busqueda (titulo, contenido),
  CONSTRAINT fk_publicaciones_usuario
    FOREIGN KEY (usuario_id) REFERENCES usuarios (id),
  CONSTRAINT fk_publicaciones_categoria
    FOREIGN KEY (categoria_id) REFERENCES categorias (id),
  CONSTRAINT fk_publicaciones_moderado_por
    FOREIGN KEY (moderado_por) REFERENCES usuarios (id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS comentarios (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  publicacion_id BIGINT UNSIGNED NOT NULL,
  usuario_id BIGINT UNSIGNED NOT NULL,
  comentario_padre_id BIGINT UNSIGNED NULL,
  contenido TEXT NOT NULL,
  es_anonimo BOOLEAN NOT NULL DEFAULT TRUE,
  estado ENUM('PUBLISHED', 'HIDDEN', 'DELETED', 'PENDING') NOT NULL DEFAULT 'PENDING',
  moderado_por BIGINT UNSIGNED NULL,
  fecha_moderacion TIMESTAMP NULL,
  nota_moderacion VARCHAR(500) NULL,
  fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_comentarios_publicacion (publicacion_id, fecha_creacion),
  KEY idx_comentarios_usuario (usuario_id),
  CONSTRAINT fk_comentarios_publicacion
    FOREIGN KEY (publicacion_id) REFERENCES publicaciones (id) ON DELETE CASCADE,
  CONSTRAINT fk_comentarios_usuario
    FOREIGN KEY (usuario_id) REFERENCES usuarios (id),
  CONSTRAINT fk_comentarios_padre
    FOREIGN KEY (comentario_padre_id) REFERENCES comentarios (id) ON DELETE SET NULL,
  CONSTRAINT fk_comentarios_moderado_por
    FOREIGN KEY (moderado_por) REFERENCES usuarios (id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS reportes (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  reportado_por BIGINT UNSIGNED NOT NULL,
  publicacion_id BIGINT UNSIGNED NULL,
  comentario_id BIGINT UNSIGNED NULL,
  motivo ENUM('ACOSO', 'AMENAZA', 'LENGUAJE_OFENSIVO', 'DATOS_PERSONALES', 'CONTENIDO_INAPROPIADO', 'OTRO') NOT NULL,
  descripcion VARCHAR(500) NULL,
  estado ENUM('PENDING', 'IN_REVIEW', 'RESOLVED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
  revisado_por BIGINT UNSIGNED NULL,
  respuesta_moderador VARCHAR(500) NULL,
  fecha_reporte TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  fecha_resolucion TIMESTAMP NULL,
  PRIMARY KEY (id),
  KEY idx_reportes_estado_fecha (estado, fecha_reporte),
  KEY idx_reportes_publicacion (publicacion_id),
  KEY idx_reportes_comentario (comentario_id),
  CONSTRAINT fk_reportes_reportado_por
    FOREIGN KEY (reportado_por) REFERENCES usuarios (id),
  CONSTRAINT fk_reportes_publicacion
    FOREIGN KEY (publicacion_id) REFERENCES publicaciones (id) ON DELETE CASCADE,
  CONSTRAINT fk_reportes_comentario
    FOREIGN KEY (comentario_id) REFERENCES comentarios (id) ON DELETE CASCADE,
  CONSTRAINT fk_reportes_revisado_por
    FOREIGN KEY (revisado_por) REFERENCES usuarios (id),
  CONSTRAINT chk_reportes_un_solo_objetivo
    CHECK (
      (publicacion_id IS NOT NULL AND comentario_id IS NULL)
      OR (publicacion_id IS NULL AND comentario_id IS NOT NULL)
    )
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS recursos_informativos (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  creado_por BIGINT UNSIGNED NULL,
  categoria_id BIGINT UNSIGNED NOT NULL,
  titulo VARCHAR(180) NOT NULL,
  contenido TEXT NOT NULL,
  enlace VARCHAR(500) NULL,
  estado ENUM('PUBLISHED', 'HIDDEN') NOT NULL DEFAULT 'PUBLISHED',
  fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_recursos_categoria (categoria_id),
  FULLTEXT KEY ft_recursos_busqueda (titulo, contenido),
  CONSTRAINT fk_recursos_creado_por
    FOREIGN KEY (creado_por) REFERENCES usuarios (id) ON DELETE SET NULL,
  CONSTRAINT fk_recursos_categoria
    FOREIGN KEY (categoria_id) REFERENCES categorias (id)
) ENGINE=InnoDB;

-- Categorías iniciales del foro.
INSERT IGNORE INTO categorias (nombre, descripcion) VALUES
  ('Ciberbullying', 'Acoso o violencia realizada mediante medios digitales.'),
  ('Bullying', 'Acoso repetido dentro del entorno escolar o social.'),
  ('Violencia física', 'Agresiones que afectan físicamente a una persona.'),
  ('Violencia verbal', 'Insultos, amenazas, humillaciones o palabras ofensivas.'),
  ('Violencia psicológica', 'Acciones que afectan la autoestima o bienestar emocional.');

