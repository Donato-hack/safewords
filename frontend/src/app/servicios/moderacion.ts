import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_URL } from '../core/api.config';
import { ModeracionComentario, ModeracionReporte, ModeracionRequest, ReporteModeracionRequest } from '../entities/moderacion';
import { ModeracionPublicacion } from '../entities/publicacion';

@Injectable({ providedIn: 'root' })
export class ModeracionService {
  constructor(private httpCliente: HttpClient) {}

  publicacionesPendientes(): Observable<ModeracionPublicacion[]> {
    return this.httpCliente.get<ModeracionPublicacion[]>(`${API_URL}/admin/publicaciones/pendientes`);
  }

  comentariosPendientes(): Observable<ModeracionComentario[]> {
    return this.httpCliente.get<ModeracionComentario[]>(`${API_URL}/admin/comentarios/pendientes`);
  }

  reportesPendientes(): Observable<ModeracionReporte[]> {
    return this.httpCliente.get<ModeracionReporte[]>(`${API_URL}/admin/reportes/pendientes`);
  }

  moderarPublicacion(id: number, datos: ModeracionRequest): Observable<unknown> {
    return this.httpCliente.put(`${API_URL}/admin/publicaciones/${id}/moderacion`, datos);
  }

  moderarComentario(id: number, datos: ModeracionRequest): Observable<unknown> {
    return this.httpCliente.put(`${API_URL}/admin/comentarios/${id}/moderacion`, datos);
  }

  moderarReporte(id: number, datos: ReporteModeracionRequest): Observable<unknown> {
    return this.httpCliente.put(`${API_URL}/admin/reportes/${id}`, datos);
  }
}

