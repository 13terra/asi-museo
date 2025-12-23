import { createRouter, createWebHistory } from "vue-router";
import AboutView from "../views/AboutView.vue";
import ErrorNotFoundView from "../views/ErrorNotFoundView.vue";
import HomeView from "../views/HomeView.vue";
import LoginForm from "../components/LoginForm.vue";
import RegisterForm from "../components/RegisterForm.vue";
import AdminExposView from "../views/AdminExposView.vue";
import GestorExposView from "../views/GestorExposView.vue";
import ExpoDetailView from "../views/ExpoDetailView.vue";
import PublicCatalogView from "../views/PublicCatalogView.vue";

import auth from "@/common/auth";
import { getStore } from "@/common/store";

const routes = [
  {
    path: "/",
    name: "HomeView",
    component: HomeView,
    meta: { public: true, guestOnly: true }
  },
  {
    path: "/catalogo",
    name: "PublicCatalog",
    component: PublicCatalogView,
    meta: { public: true }
  },
  {
    path: "/login",
    name: "Login",
    component: LoginForm,
    meta: { public: true, guestOnly: true }
  },
  {
    path: "/about",
    name: "about",
    component: AboutView
  },
  {
    path: "/register",
    name: "Register",
    component: RegisterForm,
    meta: { public: true, guestOnly: true }
  },
  {
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
  },
  {
    path: "/:catchAll(.*)*",
    component: ErrorNotFoundView,
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
      // página privada
      if (userIsLogged) {
        const hasAuthority = Array.isArray(requiredAuthority)
          ? requiredAuthority.includes(loggedUserAuthority)
          : !requiredAuthority || requiredAuthority === loggedUserAuthority;

        if (requiredAuthority && !hasAuthority) {
          // usuario logueado pero sin permisos suficientes, le redirigimos a la página de login
          alert("Acceso prohibido para el usuario actual; intenta autenticarte de nuevo");
          auth.logout();
          next("/");
        } else {
          // usuario logueado y con permisos adecuados
          next();
        }
      } else {
        // usuario no está logueado, no puede acceder a la página
        alert("Esta página requiere autenticación");
        next("/");
      }
    } else {
      // página pública
      if (userIsLogged && guestOnly) {
        // si estamos logueados no hace falta volver a mostrar el login
        next({ name: "PublicCatalog", replace: true });
      } else {
        next();
      }
    }
  });
});

export default router;
