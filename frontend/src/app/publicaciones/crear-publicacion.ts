import { Component, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { PublicacionService } from '../servicios/publicaciones';

@Component({
  selector: 'app-crear-publicacion',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './crear-publicacion.html',
  styleUrl: './crear-publicacion.css'
})
export class CrearPublicacion {
  constructor(
    private fb: FormBuilder,
    private publicacionesService: PublicacionService,
    private router: Router
  ) {
    this.formulario = this.fb.nonNullable.group({
      titulo: ['', [Validators.required, Validators.minLength(5)]],
      contenido: ['', [Validators.required, Validators.minLength(20)]],
      categoriaId: [1, [Validators.required]],
      esAnonima: [true]
    });
  }

  readonly cargando = signal(false);
  readonly error = signal('');
  readonly mensajeExito = signal('');

  readonly formulario: FormGroup<{
    titulo: FormControl<string>;
    contenido: FormControl<string>;
    categoriaId: FormControl<number>;
    esAnonima: FormControl<boolean>;
  }>;

  publicar(): void {
    this.error.set('');
    this.mensajeExito.set('');

    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      this.error.set('Completa el título y escribe una experiencia de al menos 20 caracteres antes de publicar.');
      return;
    }

    this.cargando.set(true);
    this.publicacionesService.crear(this.formulario.getRawValue()).subscribe({
      next: (respuesta) => {
        this.cargando.set(false);
        this.mensajeExito.set(
          respuesta.mensaje || 'Tu publicación fue enviada a moderación correctamente.'
        );
        this.formulario.reset({
          titulo: '',
          contenido: '',
          categoriaId: 1,
          esAnonima: true
        });
        window.setTimeout(() => {
          this.router.navigate(['/'], { queryParams: { publicada: 'ok' } });
        }, 1200);
      },
      error: (respuesta: HttpErrorResponse) => {
        this.cargando.set(false);
        if (respuesta.status === 401) {
          this.error.set('Tu sesión venció. Inicia sesión nuevamente para publicar.');
          return;
        }

        if (respuesta.status === 403) {
          this.error.set('Tu cuenta no tiene permiso para publicar.');
          return;
        }

        const mensaje = typeof respuesta.error === 'string'
          ? respuesta.error
          : respuesta.error?.error ?? respuesta.error?.message;
        this.error.set(
          typeof mensaje === 'string' && mensaje.trim().length > 0
            ? mensaje
            : 'No fue posible enviar la publicación. Inténtalo nuevamente.'
        );
      }
    });
  }
}

