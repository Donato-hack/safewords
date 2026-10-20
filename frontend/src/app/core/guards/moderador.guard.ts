import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../../servicios/auth';

export const moderadorGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  return auth.esModerador() ? true : inject(Router).createUrlTree(['/']);
};

