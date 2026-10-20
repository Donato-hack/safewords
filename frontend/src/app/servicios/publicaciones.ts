import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_URL } from '../core/api.config';
import { Comentario, ComentarioRequest } from '../entities/comentario';
import { CrearPublicacionResponse, Publicacion, PublicacionRequest } from '../entities/publicacion';
import { ReporteRequest } from '../entities/moderacion';

@Injectable({ providedIn: 'root' })
export class PublicacionService {
  constructor(private httpCliente: HttpClient) {}

  listar(): Observable<Publicacion[]> {
    return this.httpCliente.get<Publicacion[]>(`${API_URL}/publicaciones`);
  }

  obtenerComentarios(publicacionId: number): Observable<Comentario[]> {
    return this.httpCliente.get<Comentario[]>(`${API_URL}/publicaciones/${publicacionId}/comentarios`);
  }

  crear(datos: PublicacionRequest): Observable<CrearPublicacionResponse> {
    return this.httpCliente.post<CrearPublicacionResponse>(`${API_URL}/publicaciones`, datos);
  }

  comentar(publicacionId: number, datos: ComentarioRequest): Observable<Comentario> {
    return this.httpCliente.post<Comentario>(`${API_URL}/publicaciones/${publicacionId}/comentarios`, datos);
  }

  reportarPublicacion(publicacionId: number, datos: ReporteRequest): Observable<unknown> {
    return this.httpCliente.post(`${API_URL}/reportes/publicaciones/${publicacionId}`, datos);
  }

  reportarComentario(comentarioId: number, datos: ReporteRequest): Observable<unknown> {
    return this.httpCliente.post(`${API_URL}/reportes/comentarios/${comentarioId}`, datos);
  }
}

