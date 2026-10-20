export interface ModeracionComentario {
  id: number;
  publicacionId: number;
  publicacionTitulo: string;
  contenido: string;
  autorVisible: string;
  usuarioId: number;
  nombreUsuario: string;
  nombreCompleto: string;
  correo: string;
  anonimo: boolean;
  estado: string;
  fechaCreacion?: string;
}

export interface ModeracionRequest {
  estado: 'PUBLISHED' | 'HIDDEN';
  nota: string;
}

export interface ReporteRequest {
  motivo: string;
  descripcion: string;
}

export interface ModeracionReporte {
  id: number;
  tipoObjetivo: 'PUBLICACION' | 'COMENTARIO';
  publicacionId: number;
  comentarioId?: number;
  publicacionTitulo: string;
  contenido: string;
  autorVisible: string;
  usuarioId: number;
  nombreUsuario: string;
  nombreCompleto: string;
  correo: string;
  motivo: string;
  descripcion?: string;
  estado: string;
  fechaReporte?: string;
}

export interface ReporteModeracionRequest {
  estado: 'IN_REVIEW' | 'RESOLVED' | 'REJECTED';
  respuesta: string;
}
