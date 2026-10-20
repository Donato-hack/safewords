import { Component, signal } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../servicios/auth';

@Component({
  selector: 'app-registro',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './registro.html',
  styleUrl: './autenticacion.css'
})
export class Registro {
  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router
  ) {
    this.formulario = this.fb.nonNullable.group({
      nombreCompleto: ['', [Validators.required, Validators.minLength(3)]],
      nombreUsuario: ['', [Validators.required, Validators.minLength(3)]],
      correo: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  readonly cargando = signal(false);
  readonly error = signal('');
  readonly correcto = signal(false);
  readonly mensaje = signal('Cuenta creada correctamente.');

  readonly formulario: FormGroup<{
    nombreCompleto: FormControl<string>;
    nombreUsuario: FormControl<string>;
    correo: FormControl<string>;
    password: FormControl<string>;
  }>;

  registrarse(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    this.cargando.set(true);
    this.error.set('');
    this.auth.registrar(this.formulario.getRawValue()).subscribe({
      next: (respuesta) => {
        this.correcto.set(true);
        this.mensaje.set(respuesta.mensaje);
        this.cargando.set(false);
        setTimeout(() => this.router.navigate(['/login']), 1200);
      },
      error: (respuesta) => {
        this.cargando.set(false);
        const mensajeBackend = respuesta.error?.error ?? respuesta.error?.message;
        this.error.set(mensajeBackend || (respuesta.status === 409
          ? 'Ese correo o nombre de usuario ya existe.'
          : 'No fue posible crear la cuenta. Verifica que el backend de Eclipse esté encendido.'));
      }
    });
  }
}

