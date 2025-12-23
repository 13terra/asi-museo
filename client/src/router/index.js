import { createRouter, createWebHistory } from "vue-router";
<<<<<<< HEAD
import AboutView from "../views/AboutView.vue";
import ErrorNotFoundView from "../views/ErrorNotFoundView.vue";
import HomeView from "../views/HomeView.vue";
import LoginForm from "../components/LoginForm.vue";
import RegisterForm from "../components/RegisterForm.vue";
import AdminExposView from "../views/AdminExposView.vue";
import GestorExposView from "../views/GestorExposView.vue";
import ExpoDetailView from "../views/ExpoDetailView.vue";
import PublicCatalogView from "../views/PublicCatalogView.vue";

=======
>>>>>>> 10c46cf789ffa41b36bff5a0d674dc3b09cd6f2b
import auth from "@/common/auth";
import { getStore } from "@/common/store";
import { ROLES } from "@/constants";


// VISTAS PUBLICAS

// AUTH

// VISITANTE

// GESTOR

// ADMIN

// ERROR
import ErrorNotFound  from "@/views/ErrorNotFound.vue";

<<<<<<< HEAD
=======

>>>>>>> 10c46cf789ffa41b36bff5a0d674dc3b09cd6f2b
const routes = [


  // RUTAS PUBLICAS

  {
<<<<<<< HEAD
    path: "/",
    name: "HomeView",
    component: HomeView,
    meta: { public: true, guestOnly: true }
  },
  {
    path: "/catalogo",
    name: "PublicCatalog",
    component: PublicCatalogView,
=======
    path: '/',
    name: 'CatalogoPublico',
    component: CatalogoPublico,
>>>>>>> 10c46cf789ffa41b36bff5a0d674dc3b09cd6f2b
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

  // AUTH 
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

  // VISITANTE
  {
<<<<<<< HEAD
    path: "/expos/admin",
    name: "AdminExpos",
    component: AdminExposView,
    meta: { authority: ["ADMIN"] }
  },
  {
    path: "/expos/gestor",
    name: "GestorExpos",
    component: GestorExposView,
    meta: { authority: ["ADMIN", "GESTOR"] }
  },
  {
    path: "/expos/:id",
    name: "ExpoDetail",
    component: ExpoDetailView,
    meta: { authority: ["ADMIN", "GESTOR"] }
=======
    path: '/mis-reservas',
    name: 'MisReservas',
    component:  MisReservas,
    meta: { authority: ROLES.VISITANTE }
>>>>>>> 10c46cf789ffa41b36bff5a0d674dc3b09cd6f2b
  },
  {
    path: '/reservas/: idReserva',
    name: 'ReservaDetalle',
    component: ReservaDetalle,
    meta: { authority: ROLES.VISITANTE }
  },
  {
    path: '/sesiones/: idSesion/reservar',
    name: 'ReservarEntradas',
    component: ReservarEntradas,
    meta: { authority: ROLES.VISITANTE }
  },

  // GESTOR
  {
    path: '/gestor',
    name: 'PanelGestor',
    component: PanelGestor,
    meta: { authority: ROLES.GESTOR }
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
    meta: { authority: ROLES.GESTOR }
  },
  {
    path: '/gestor/ediciones/: idEdicion/sesiones',
    name: 'GestionSesiones',
    component:  GestionSesiones,
    meta: { authority: ROLES.GESTOR }
  },
  {
    path: '/gestor/ediciones/:idEdicion/salas',
    name: 'GestionSalas',
    component: GestionSalas,
    meta: { authority: ROLES.GESTOR }
  },
  {
    path:  '/gestor/exposiciones/:idExposicion/permisos',
    name: 'GestionPermisos',
    component: GestionPermisos,
    meta: { authority: ROLES.GESTOR }
  },
  {
    path: '/gestor/obras',
    name: 'CatalogoObras',
    component: CatalogoObras,
    meta: { authority: ROLES.GESTOR }
  },

  // ADMIN (tiene acceso a todo de GESTOR + gestión de usuarios)
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

  //  ERROR 404 
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


router.beforeEach((to, from, next) => {
  // Lo primero que hacemos antes de cargar ninguna ruta es comprobar si
  // el usuario está autenticado (revisando el token)
  auth.isAuthenticationChecked.finally(() => {
    // por defecto, el usuario debe estar autenticado para acceder a las rutas
    const requiresAuth = !to.meta.public;
    const requiredAuthority = to.meta.authority;
    const userIsLogged = getStore().state.user.logged;
    const loggedUserAuthority = getStore().state.user.authority;
    const guestOnly = to.meta.guestOnly; // Definido para trabajar con la vista pública

    if (requiresAuth) {
      // Página privada (requiere autenticación)
      if (userIsLogged) {
<<<<<<< HEAD
        const hasAuthority = Array.isArray(requiredAuthority)
          ? requiredAuthority.includes(loggedUserAuthority)
          : !requiredAuthority || requiredAuthority === loggedUserAuthority;

        if (requiredAuthority && !hasAuthority) {
          // usuario logueado pero sin permisos suficientes, le redirigimos a la página de login
          alert("Acceso prohibido para el usuario actual; intenta autenticarte de nuevo");
          auth.logout();
          next("/");
=======
        //Usuario autenticado
        if (requiredAuthority) {
          //La ruta requiere un rol específico
          if (loggedUserAuthority === ROLES.ADMIN) {
            // Admin tiene acceso total (puede acceder a rutas de gestor también)
            next();
          } else if (requiredAuthority === loggedUserAuthority) {
            // El rol coincide: suficiente
            next();
          } else {
            // Rol insuficiente
            alert('No tienes permisos para acceder a esta sección');
            // Redirigir según rol
            if (loggedUserAuthority === ROLES.VISITANTE) {
              next('/');
            } else if (loggedUserAuthority === ROLES.GESTOR) {
              next('/gestor');
            } else {
              next('/');
            }
          }
>>>>>>> 10c46cf789ffa41b36bff5a0d674dc3b09cd6f2b
        } else {
          // Ruta privada sin rol específico
          next();
        }
      } else {
        // Usuario NO autenticado
        alert('Esta página requiere autenticación');
        next({ name: 'Login', query: { redirect: to.fullPath } });
      }
    } else {
      // Página pública
      if (userIsLogged && guestOnly) {
<<<<<<< HEAD
        // si estamos logueados no hace falta volver a mostrar el login
        next({ name: "PublicCatalog", replace: true });
=======
        // Si está loggeado, redirigir al panel correspondiente
        if (loggedUserAuthority === ROLES.ADMIN) {
          next({ name: 'PanelAdmin', replace: true });
        } else if (loggedUserAuthority === ROLES.GESTOR) {
          next({ name: 'PanelGestor', replace: true });
        } else {
          next({ name: 'CatalogoPublico', replace: true });
        }
>>>>>>> 10c46cf789ffa41b36bff5a0d674dc3b09cd6f2b
      } else {
        next();
      }
    }
  });
});


export default router;
