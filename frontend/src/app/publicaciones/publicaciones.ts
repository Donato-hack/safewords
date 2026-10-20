import { DatePipe } from '@angular/common';
import { Component, computed, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AuthService } from '../servicios/auth';
import { PublicacionService } from '../servicios/publicaciones';
import { Publicacion } from '../entities/publicacion';

interface TemaForo {
  nombre: string;
  categoria: string;
  icono: string;
  descripcion: string;
}

@Component({
  selector: 'app-publicaciones',
  imports: [DatePipe, RouterLink],
  templateUrl: './publicaciones.html',
  styleUrl: './publicaciones.css'
})
export class Publicaciones implements OnInit {
  constructor(
    private publicacionesService: PublicacionService,
    public auth: AuthService,
    private route: ActivatedRoute
  ) {}

  readonly publicaciones = signal<Publicacion[]>([]);
  readonly cargando = signal(true);
  readonly error = signal('');
  readonly textoBusqueda = signal('');
  readonly categoriaSeleccionada = signal('Todas');
  readonly orden = signal<'recientes' | 'antiguas'>('recientes');
  readonly aviso = signal('');
  readonly categorias = ['Todas', 'Bullying', 'Cyberbullying', 'Violencia física', 'Violencia verbal', 'Violencia psicológica'];
  readonly temas: TemaForo[] = [
    { nombre: 'Bullying', categoria: 'Bullying', icono: '◉', descripcion: 'Reconoce sus señales y formas.' },
    { nombre: 'Cyberbullying', categoria: 'Cyberbullying', icono: '⌁', descripcion: 'Cuídate también en internet.' },
    { nombre: 'Violencia física', categoria: 'Violencia física', icono: '♡', descripcion: 'Tu integridad siempre importa.' },
    { nombre: 'Violencia verbal', categoria: 'Violencia verbal', icono: '“', descripcion: 'Las palabras también cuentan.' },
    { nombre: 'Violencia psicológica', categoria: 'Violencia psicológica', icono: '☼', descripcion: 'Hablemos del bienestar emocional.' }
  ];

  readonly publicacionesFiltradas = computed(() => {
    const texto = this.textoBusqueda().trim().toLowerCase();
    const categoria = this.categoriaSeleccionada();

    const filtradas = this.publicaciones().filter((publicacion) => {
      const contenido = `${publicacion.titulo} ${publicacion.contenido}`.toLowerCase();
      const coincideTexto = !texto || contenido.includes(texto);
      const coincideCategoria = categoria === 'Todas' || this.categoriaDe(publicacion).toLowerCase() === categoria.toLowerCase();
      return coincideTexto && coincideCategoria;
    });

    return filtradas.sort((a, b) => {
      const fechaA = new Date(a.fechaCreacion ?? 0).getTime();
      const fechaB = new Date(b.fechaCreacion ?? 0).getTime();
      return this.orden() === 'recientes' ? fechaB - fechaA : fechaA - fechaB;
    });
  });

  ngOnInit(): void {
    if (this.route.snapshot.queryParamMap.get('publicada') === 'ok') {
      this.mostrarAviso('Tu publicación fue enviada a moderación. Aparecerá cuando sea aprobada.');
    }
    this.cargarPublicaciones();
  }

  cargarPublicaciones(): void {
    this.cargando.set(true);
    this.error.set('');
    this.publicacionesService.listar().subscribe({
      next: (publicaciones) => {
        this.publicaciones.set(publicaciones);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No pudimos cargar las publicaciones. Verifica que Eclipse tenga encendido el backend.');
        this.cargando.set(false);
      }
    });
  }

  categoriaDe(publicacion: Publicacion): string {
    if (typeof publicacion.categoria === 'string') {
      return publicacion.categoria;
    }
    return publicacion.categoria?.nombre ?? 'Comunidad';
  }

  autorDe(publicacion: Publicacion): string {
    return publicacion.autor ?? publicacion.usuario ?? (publicacion.esAnonima || publicacion.anonima ? 'Usuario anónimo' : 'Usuario');
  }

  seleccionarCategoria(categoria: string): void {
    this.categoriaSeleccionada.set(categoria);
  }

  limpiarFiltros(): void {
    this.textoBusqueda.set('');
    this.categoriaSeleccionada.set('Todas');
    this.orden.set('recientes');
  }

  copiarEnlace(publicacion: Publicacion): void {
    const enlace = `${window.location.origin}/publicacion/${publicacion.id}`;
    const portapapeles = navigator.clipboard;
    if (!portapapeles) {
      this.mostrarAviso('Puedes copiar el enlace desde la barra del navegador.');
      return;
    }
    portapapeles.writeText(enlace).then(() => {
      this.mostrarAviso('Enlace copiado para compartir esta historia.');
    }).catch(() => {
      this.mostrarAviso('Puedes copiar el enlace desde la barra del navegador.');
    });
  }

  private mostrarAviso(mensaje: string): void {
    this.aviso.set(mensaje);
    window.setTimeout(() => this.aviso.set(''), 3000);
  }
}

