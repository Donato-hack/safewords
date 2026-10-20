import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, signal } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AuthService } from '../servicios/auth';
import { PublicacionService } from '../servicios/publicaciones';
import { Comentario } from '../entities/comentario';
import { Publicacion } from '../entities/publicacion';

type TipoReporte = 'publicacion' | 'comentario';

@Component({
  selector: 'app-detalle-publicacion',
  imports: [DatePipe, ReactiveFormsModule, RouterLink],
  templateUrl: './detalle-publicacion.html',
  styleUrl: './detalle-publicacion.css'
})
export class DetallePublicacion implements OnInit {
  constructor(
    private route: ActivatedRoute,
    private service: PublicacionService,
    private fb: FormBuilder,
    public auth: AuthService
  ) {
    this.formulario = this.fb.nonNullable.group({
      contenido: ['', [Validators.required, Validators.minLength(2)]],
      esAnonimo: [true]
    });
    this.reporteFormulario = this.fb.nonNullable.group({
      motivo: ['', [Validators.required]],
      descripcion: ['', [Validators.maxLength(500)]]
    });
  }

  readonly publicacion = signal<Publicacion | null>(null);
  readonly comentarios = signal<Comentario[]>([]);
  readonly cargando = signal(true);
  readonly error = signal('');
  readonly enviando = signal(false);
  readonly aviso = signal('');
  readonly reporteAbierto = signal<{ tipo: TipoReporte; id: number } | null>(null);
  readonly enviandoReporte = signal(false);
  readonly errorReporte = signal('');
  readonly motivos = [
    { valor: 'ACOSO', nombre: 'Acoso o intimidación' },
    { valor: 'AMENAZA', nombre: 'Amenaza' },
    { valor: 'LENGUAJE_OFENSIVO', nombre: 'Lenguaje ofensivo' },
    { valor: 'DATOS_PERSONALES', nombre: 'Datos personales expuestos' },
    { valor: 'CONTENIDO_INAPROPIADO', nombre: 'Contenido inapropiado' },
    { valor: 'OTRO', nombre: 'Otro motivo' }
  ];

  readonly formulario: FormGroup<{
    contenido: FormControl<string>;
    esAnonimo: FormControl<boolean>;
  }>;

  readonly reporteFormulario: FormGroup<{
    motivo: FormControl<string>;
    descripcion: FormControl<string>;
  }>;

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    const abrirReporteAlCargar = this.route.snapshot.queryParamMap.get('report') === 'publicacion';
    this.service.listar().subscribe({
      next: (publicaciones) => {
        const encontrada = publicaciones.find((item) => item.id === id) ?? null;
        this.publicacion.set(encontrada);
        if (encontrada && abrirReporteAlCargar) {
          this.abrirReporte('publicacion', encontrada.id);
        }
        this.cargarComentarios(id);
      },
      error: () => {
        this.error.set('No se pudo cargar la publicación.');
        this.cargando.set(false);
      }
    });
  }

  cargarComentarios(id: number): void {
    this.service.obtenerComentarios(id).subscribe({
      next: (comentarios) => {
        this.comentarios.set(comentarios);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se pudieron cargar los comentarios.');
        this.cargando.set(false);
      }
    });
  }

  comentar(): void {
    if (!this.publicacion() || this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    this.enviando.set(true);
    const id = this.publicacion()!.id;
    this.service.comentar(id, this.formulario.getRawValue()).subscribe({
      next: () => {
        this.formulario.reset({ contenido: '', esAnonimo: true });
        this.enviando.set(false);
        this.aviso.set('Tu comentario fue enviado y será visible después de la moderación.');
        this.cargarComentarios(id);
      },
      error: () => {
        this.error.set('No se pudo enviar el comentario.');
        this.enviando.set(false);
      }
    });
  }

  abrirReporte(tipo: TipoReporte, id: number): void {
    if (!this.auth.estaAutenticado()) {
      this.aviso.set('Inicia sesión para enviar un reporte.');
      return;
    }
    this.reporteAbierto.set({ tipo, id });
    this.errorReporte.set('');
    this.reporteFormulario.reset({ motivo: '', descripcion: '' });
  }

  cerrarReporte(): void {
    this.reporteAbierto.set(null);
    this.errorReporte.set('');
    this.reporteFormulario.reset({ motivo: '', descripcion: '' });
  }

  enviarReporte(): void {
    const objetivo = this.reporteAbierto();
    if (!objetivo || this.reporteFormulario.invalid) {
      this.reporteFormulario.markAllAsTouched();
      return;
    }

    this.enviandoReporte.set(true);
    const datos = this.reporteFormulario.getRawValue();
    const solicitud = objetivo.tipo === 'publicacion'
      ? this.service.reportarPublicacion(objetivo.id, datos)
      : this.service.reportarComentario(objetivo.id, datos);

    solicitud.subscribe({
      next: () => {
        this.enviandoReporte.set(false);
        this.cerrarReporte();
        this.aviso.set('Gracias. El reporte fue enviado para revisión.');
      },
      error: (respuesta: HttpErrorResponse) => {
        this.enviandoReporte.set(false);
        if (respuesta.status === 401) {
          this.errorReporte.set('Tu sesión venció. Inicia sesión nuevamente para enviar el reporte.');
          return;
        }

        if (respuesta.status === 403) {
          this.errorReporte.set('Tu cuenta no tiene permiso para enviar reportes.');
          return;
        }

        const mensaje = respuesta.error?.error;
        this.errorReporte.set(
          typeof mensaje === 'string' && mensaje.trim().length > 0
            ? mensaje
            : 'No fue posible enviar el reporte. Intenta nuevamente.'
        );
      }
    });
  }
}

