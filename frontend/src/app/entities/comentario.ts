export interface Comentario {
  id: number;
  contenido: string;
  autor?: string;
  anonimo?: boolean;
  esAnonimo?: boolean;
  estado?: string;
  fechaCreacion?: string;
}

export interface ComentarioRequest {
  contenido: string;
  esAnonimo: boolean;
}
