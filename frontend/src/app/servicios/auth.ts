import { computed, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { API_URL } from '../core/api.config';
import { LoginRequest, LoginResponse, RegistroRequest, RegistroResponse, Sesion } from '../entities/usuario';

@Injectable({ providedIn: 'root' })
export class AuthService {
  constructor(
    private httpCliente: HttpClient,
    private router: Router
  ) {}
  private readonly storageKey = 'safewords_sesion';
  private readonly sesion = signal<Sesion | null>(this.cargarSesion());

  readonly estaAutenticado = computed(() => Boolean(this.sesion()?.token));
  readonly esModerador = computed(() => {
    const rol = this.rolActual().replace('ROLE_', '').toUpperCase();
    return ['ADMIN', 'MODERATOR'].includes(rol);
  });
  readonly usuarioActual = computed(() => this.sesion()?.usuario ?? 'Visitante');
  readonly rolActual = computed(() => this.sesion()?.rol ?? 'USER');

  login(datos: LoginRequest): Observable<LoginResponse> {
    return this.httpCliente.post<LoginResponse>(`${API_URL}/auth/login`, datos).pipe(
      tap((respuesta) => this.guardarSesion(respuesta))
    );
  }

  registrar(datos: RegistroRequest): Observable<RegistroResponse> {
    return this.httpCliente.post<RegistroResponse>(`${API_URL}/auth/registro`, datos);
  }

  logout(): void {
    localStorage.removeItem(this.storageKey);
    this.sesion.set(null);
    this.router.navigate(['/']);
  }

  token(): string | null {
    return this.sesion()?.token ?? null;
  }

  private guardarSesion(respuesta: LoginResponse): void {
    const nuevaSesion: Sesion = {
      token: respuesta.token,
      usuario: respuesta.usuario ?? respuesta.nombreUsuario ?? respuesta.username ?? respuesta.correo ?? 'Usuario',
      rol: respuesta.rol ?? respuesta.role ?? 'USER'
    };
    localStorage.setItem(this.storageKey, JSON.stringify(nuevaSesion));
    this.sesion.set(nuevaSesion);
  }

  private cargarSesion(): Sesion | null {
    const guardada = localStorage.getItem(this.storageKey);
    if (!guardada) {
      return null;
    }

    try {
      return JSON.parse(guardada) as Sesion;
    } catch {
      localStorage.removeItem(this.storageKey);
      return null;
    }
  }
}

