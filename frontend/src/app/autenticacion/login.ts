import { Component, signal } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../servicios/auth';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './autenticacion.css'
})
export class Login {
  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router
  ) {
    this.formulario = this.fb.nonNullable.group({
      correo: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]]
    });
  }

  readonly cargando = signal(false);
  readonly error = signal('');

  readonly formulario: FormGroup<{
    correo: FormControl<string>;
    password: FormControl<string>;
  }>;

  ingresar(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    this.cargando.set(true);
    this.error.set('');
    this.auth.login(this.formulario.getRawValue()).subscribe({
      next: () => this.router.navigate([this.auth.esModerador() ? '/admin' : '/']),
      error: (respuesta) => {
        this.cargando.set(false);
        this.error.set(respuesta.status === 401 ? 'El correo o la contraseña no son correctos.' : 'No fue posible iniciar sesión. Verifica que el backend esté encendido.');
      }
    });
  }
}

