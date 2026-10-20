export interface LoginRequest {
  correo: string;
  password: string;
}

export interface RegistroRequest {
  nombreCompleto: string;
  nombreUsuario: string;
  correo: string;
  password: string;
}

export interface RegistroResponse {
  mensaje: string;
  correoEnviado: boolean;
}

export interface LoginResponse {
  token: string;
  usuario?: string;
  nombreUsuario?: string;
  username?: string;
  correo?: string;
  rol?: string;
  role?: string;
}

export interface Sesion {
  token: string;
  usuario: string;
  rol?: string;
}
