import { createRouter, createWebHistory } from 'vue-router';
import auth from '@/common/auth';
import { getStore } from '@/common/store';
import { ROLES } from '@/constants';

// ========== VISTAS PÚBLICAS ==========
import CatalogoPublico from '@/views/public/CatalogoPublico. vue';
import ExposicionPublicaDetalle from '@/views/public/ExposicionPublicaDetalle.vue';
import EdicionPublicaDetalle from '@/views/public/EdicionPublicaDetalle.vue';

// ========== AUTH ==========
import Login from '@/views/auth/Login.vue';
import Register from '@/views/auth/Register.vue';

// ========== VISITANTE ==========
import MisReservas from '@/views/visitante/MisReservas. vue';
import ReservaDetalle from '@/views/visitante/ReservaDetalle.vue';
import ReservarEntradas from '@/views/visitante/ReservarEntradas. vue';

// ========== GESTOR ==========
import PanelGestor from '@/views/gestor/PanelGestor. vue';
import ExposicionDetalle from '@/views/gestor/ExposicionDetalle.vue';
import EdicionDetalle from '@/views/gestor/EdicionDetalle.vue';
import GestionPiezas from '@/views/gestor/GestionPiezas.vue';
import GestionSesiones from '@/views/gestor/GestionSesiones. vue';
import GestionSalas from '@/views/gestor/GestionSalas.vue';
import GestionPermisos from '@/views/gestor/GestionPermisos. vue';
import CatalogoObras from '@/views/gestor/CatalogoObras.vue';

// ========== ADMIN ==========
import PanelAdmin from '@/views/admin/PanelAdmin.vue';
import GestionUsuarios from '@/views/admin/GestionUsuarios.vue';
import GestionSalasAdmin from '@/views/admin/GestionSalasAdmin.vue';

// ========== ERROR ==========
import ErrorNotFound from '@/views/ErrorNotFound.vue';

const routes = [
  // ========== RUTAS PÚBLICAS ==========
  {
    path: '/',
    name: 'CatalogoPublico',
    component: CatalogoPublico,
    meta: { public: true }
  },
  {
    path: '/exposiciones/: idExposicion',
    name:  'ExposicionPublicaDetalle',
    component: ExposicionPublicaDetalle,
    meta: { public: true }
  },
  {
    path: '/exposiciones/: idExposicion/ediciones/:idEdicion',
    name: 'EdicionPublicaDetalle',
    component: EdicionPublicaDetalle,
    meta: { public: true }
  },

  // ========== AUTH ==========
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { public: true, guestOnly: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: { public: true, guestOnly: true }
  },

  // ========== VISITANTE ==========
  {
    path: '/mis-reservas',
    name: 'MisReservas',
    component:  MisReservas,
    meta: { authority: ROLES.VISITANTE }
  },
  {
    path: '/reservas/:idReserva',
    name: 'ReservaDetalle',
    component: ReservaDetalle,
    meta: { authority: ROLES.VISITANTE }
  },
  {
    path: '/sesiones/:idSesion/reservar',
    name: 'ReservarEntradas',
    component: ReservarEntradas,
    meta: { authority: ROLES.VISITANTE }
  },

  // ========== GESTOR ==========
  {
    path: '/gestor',
    name: 'PanelGestor',
    component: PanelGestor,
    meta: { authority:  ROLES.GESTOR }
  },
  {
    path:  '/gestor/exposiciones/: idExposicion',
    name: 'ExposicionDetalle',
    component: ExposicionDetalle,
    meta: { authority: ROLES.GESTOR }
  },
  {
    path: '/gestor/exposiciones/:idExposicion/ediciones/:idEdicion',
    name: 'EdicionDetalle',
    component: EdicionDetalle,
    meta: { authority: ROLES.GESTOR }
  },
  {
    path: '/gestor/ediciones/:idEdicion/piezas',
    name: 'GestionPiezas',
    component: GestionPiezas,
    meta: { authority:  ROLES.GESTOR }
  },
  {
    path: '/gestor/ediciones/: idEdicion/sesiones',
    name: 'GestionSesiones',
    component:  GestionSesiones,
    meta: { authority: ROLES. GESTOR }
  },
  {
    path: '/gestor/ediciones/:idEdicion/salas',
    name: 'GestionSalas',
    component: GestionSalas,
    meta: { authority:  ROLES.GESTOR }
  },
  {
    path:  '/gestor/exposiciones/:idExposicion/permisos',
    name: 'GestionPermisos',
    component: GestionPermisos,
    meta: { authority:  ROLES.GESTOR }
  },
  {
    path: '/gestor/obras',
    name: 'CatalogoObras',
    component: CatalogoObras,
    meta: { authority:  ROLES.GESTOR }
  },

  // ========== ADMIN (tiene acceso a todo de GESTOR + gestión de usuarios) ==========
  {
    path: '/admin',
    name: 'PanelAdmin',
    component: PanelAdmin,
    meta:  { authority: ROLES.ADMIN }
  },
  {
    path: '/admin/usuarios',
    name: 'GestionUsuarios',
    component: GestionUsuarios,
    meta:  { authority: ROLES.ADMIN }
  },
  {
    path: '/admin/salas',
    name: 'GestionSalasAdmin',
    component: GestionSalasAdmin,
    meta: { authority: ROLES.ADMIN }
  },

  // ========== ERROR 404 ==========
  {
    path: '/:catchAll(.*)*',
    component: ErrorNotFound,
    meta: { public: true }
  }
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
});

/**
 * Guard de navegación global
 * Controla el acceso según autenticación y roles
 */
router.beforeEach((to, from, next) => {
  auth.isAuthenticationChecked. finally(() => {
    const requiresAuth = ! to.meta.public;
    const requiredAuthority = to.meta.authority;
    const userIsLogged = getStore().state.user.logged;
    const loggedUserAuthority = getStore().state.user.authority;
    const guestOnly = to.meta.guestOnly;

    if (requiresAuth) {
      // Página privada (requiere autenticación)
      if (userIsLogged) {
        // Usuario autenticado
        if (requiredAuthority) {
          // La ruta requiere un rol específico
          if (loggedUserAuthority === ROLES.ADMIN) {
            // ADMIN tiene acceso total (puede acceder a rutas de GESTOR también)
            next();
          } else if (requiredAuthority === loggedUserAuthority) {
            // El rol coincide
            next();
          } else {
            // Rol insuficiente
            alert('No tienes permisos para acceder a esta sección');
            // Redirigir según rol
            if (loggedUserAuthority === ROLES. VISITANTE) {
              next('/');
            } else if (loggedUserAuthority === ROLES.GESTOR) {
              next('/gestor');
            } else {
              next('/');
            }
          }
        } else {
          // Ruta privada sin rol específico
          next();
        }
      } else {
        // Usuario no autenticado
        alert('Esta página requiere autenticación');
        next({ name: 'Login', query: { redirect: to.fullPath } });
      }
    } else {
      // Página pública
      if (userIsLogged && guestOnly) {
        // Si está logueado, redirigir al panel correspondiente
        if (loggedUserAuthority === ROLES. ADMIN) {
          next({ name: 'PanelAdmin', replace: true });
        } else if (loggedUserAuthority === ROLES. GESTOR) {
          next({ name: 'PanelGestor', replace: true });
        } else {
          next({ name: 'CatalogoPublico', replace: true });
        }
      } else {
        next();
      }
    }
  });
});

export default router;