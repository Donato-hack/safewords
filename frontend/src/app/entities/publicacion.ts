export interface Publicacion {
  id: number;
  titulo: string;
  contenido: string;
  autor?: string;
  usuario?: string;
  categoria?: string | { nombre: string };
  categoriaId?: number;
  esAnonima?: boolean;
  anonima?: boolean;
  estado?: string;
  fechaCreacion?: string;
}

export interface PublicacionRequest {
  titulo: string;
  contenido: string;
  categoriaId: number;
  esAnonima: boolean;
}

export interface CrearPublicacionResponse {
  mensaje: string;
  id: number;
  estado: string;
}

export interface ModeracionPublicacion extends Publicacion {
  autorVisible: string;
  usuarioId: number;
  nombreUsuario: string;
  nombreCompleto: string;
  correo: string;
}
