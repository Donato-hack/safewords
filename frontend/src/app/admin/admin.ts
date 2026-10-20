import { DatePipe } from '@angular/common';
import { Component, OnInit, computed, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ModeracionService } from '../servicios/moderacion';
import { ModeracionComentario, ModeracionReporte, ModeracionRequest, ReporteModeracionRequest } from '../entities/moderacion';
import { ModeracionPublicacion } from '../entities/publicacion';

@Component({
  selector: 'app-admin',
  imports: [DatePipe, RouterLink],
  templateUrl: './admin.html',
  styleUrl: './admin.css'
})
export class Admin implements OnInit {
  constructor(private service: ModeracionService) {}

  readonly publicaciones = signal<ModeracionPublicacion[]>([]);
  readonly comentarios = signal<ModeracionComentario[]>([]);
  readonly reportes = signal<ModeracionReporte[]>([]);
  readonly pestana = signal<'publicaciones' | 'comentarios' | 'reportes'>('publicaciones');
  readonly cargando = signal(true);
  readonly error = signal('');
  readonly procesando = signal<number | null>(null);
  readonly textoBusqueda = signal('');
  readonly aviso = signal('');

  readonly publicacionesFiltradas = computed(() => {
    const texto = this.textoBusqueda().trim().toLowerCase();
    return this.publicaciones().filter((publicacion) =>
      !texto || `${publicacion.titulo} ${publicacion.contenido} ${publicacion.nombreUsuario} ${publicacion.correo}`.toLowerCase().includes(texto)
    );
  });

  readonly comentariosFiltrados = computed(() => {
    const texto = this.textoBusqueda().trim().toLowerCase();
    return this.comentarios().filter((comentario) =>
      !texto || `${comentario.contenido} ${comentario.nombreUsuario} ${comentario.correo} ${comentario.publicacionTitulo}`.toLowerCase().includes(texto)
    );
  });

  readonly reportesFiltrados = computed(() => {
    const texto = this.textoBusqueda().trim().toLowerCase();
    return this.reportes().filter((reporte) =>
      !texto || `${reporte.contenido} ${reporte.motivo} ${reporte.nombreUsuario} ${reporte.correo} ${reporte.publicacionTitulo}`.toLowerCase().includes(texto)
    );
  });

  ngOnInit(): void {
    this.cargarBandeja();
  }

  cargarBandeja(): void {
    this.cargando.set(true);
    this.error.set('');
    this.aviso.set('');
    let completadas = 0;
    let fallo = false;
    const terminar = (): void => {
      completadas++;
      if (completadas === 3) {
        this.cargando.set(false);
        if (fallo) {
          this.error.set('No se pudo cargar toda la bandeja de moderación.');
        }
      }
    };

    this.service.publicacionesPendientes().subscribe({
      next: (publicaciones) => this.publicaciones.set(publicaciones),
      error: () => { fallo = true; terminar(); },
      complete: terminar
    });
    this.service.comentariosPendientes().subscribe({
      next: (comentarios) => this.comentarios.set(comentarios),
      error: () => { fallo = true; terminar(); },
      complete: terminar
    });
    this.service.reportesPendientes().subscribe({
      next: (reportes) => this.reportes.set(reportes),
      error: () => { fallo = true; terminar(); },
      complete: terminar
    });
  }

  aprobarPublicacion(id: number): void {
    this.moderarPublicacion(id, { estado: 'PUBLISHED', nota: 'Aprobada por moderación.' });
  }

  ocultarPublicacion(id: number): void {
    this.moderarPublicacion(id, { estado: 'HIDDEN', nota: 'Ocultada por incumplir las normas de la comunidad.' });
  }

  aprobarComentario(id: number): void {
    this.moderarComentario(id, { estado: 'PUBLISHED', nota: 'Aprobado por moderación.' });
  }

  ocultarComentario(id: number): void {
    this.moderarComentario(id, { estado: 'HIDDEN', nota: 'Ocultado por incumplir las normas de la comunidad.' });
  }

  marcarReporte(id: number, estado: ReporteModeracionRequest['estado']): void {
    const respuesta = estado === 'REJECTED'
      ? 'Reporte revisado y descartado por moderación.'
      : estado === 'IN_REVIEW'
        ? 'Reporte tomado en revisión por moderación.'
        : 'Reporte revisado y resuelto por moderación.';
    this.procesando.set(id);
    this.service.moderarReporte(id, { estado, respuesta }).subscribe({
      next: () => {
        this.reportes.update((actuales) => actuales.filter((item) => item.id !== id));
        this.procesando.set(null);
        this.mostrarAviso('Reporte actualizado correctamente.');
      },
      error: () => {
        this.error.set('No se pudo actualizar el reporte.');
        this.procesando.set(null);
      }
    });
  }

  private moderarPublicacion(id: number, datos: ModeracionRequest): void {
    this.procesando.set(id);
    this.service.moderarPublicacion(id, datos).subscribe({
      next: () => {
        this.publicaciones.update((actuales) => actuales.filter((item) => item.id !== id));
        this.procesando.set(null);
        this.mostrarAviso('Publicación actualizada correctamente.');
      },
      error: () => {
        this.error.set('No se pudo actualizar la publicación.');
        this.procesando.set(null);
      }
    });
  }

  private moderarComentario(id: number, datos: ModeracionRequest): void {
    this.procesando.set(id);
    this.service.moderarComentario(id, datos).subscribe({
      next: () => {
        this.comentarios.update((actuales) => actuales.filter((item) => item.id !== id));
        this.procesando.set(null);
        this.mostrarAviso('Comentario actualizado correctamente.');
      },
      error: () => {
        this.error.set('No se pudo actualizar el comentario.');
        this.procesando.set(null);
      }
    });
  }

  private mostrarAviso(mensaje: string): void {
    this.aviso.set(mensaje);
    window.setTimeout(() => this.aviso.set(''), 3000);
  }
}

