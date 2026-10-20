import { Routes } from '@angular/router';
import { Publicaciones } from './publicaciones/publicaciones';
import { Login } from './autenticacion/login';
import { Registro } from './autenticacion/registro';
import { CrearPublicacion } from './publicaciones/crear-publicacion';
import { DetallePublicacion } from './publicaciones/detalle-publicacion';
import { authGuard } from './core/guards/auth.guard';
import { moderadorGuard } from './core/guards/moderador.guard';
import { Admin } from './admin/admin';
import { Normas } from './normas/normas';
import { Informacion } from './informacion/informacion';

export const routes: Routes = [
  { path: '', component: Publicaciones },
  { path: 'login', component: Login },
  { path: 'registro', component: Registro },
  { path: 'publicar', component: CrearPublicacion, canActivate: [authGuard] },
  { path: 'publicacion/:id', component: DetallePublicacion },
  { path: 'admin', component: Admin, canActivate: [moderadorGuard] },
  { path: 'normas', component: Normas },
  { path: 'informacion', component: Informacion },
  { path: '**', redirectTo: '' }
];
