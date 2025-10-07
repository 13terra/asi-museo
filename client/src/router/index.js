import { createRouter, createWebHistory } from "vue-router";
import AboutView from "../views/AboutView.vue";
import ErrorNotFoundView from "../views/ErrorNotFoundView.vue";
import HomeView from "../views/HomeView.vue";
import LoginForm from "../components/LoginForm.vue";

import auth from "@/common/auth";
import { getStore } from "@/common/store";

import notesRoutes from "@/components/notes/routes.js";
import RegisterForm from "@/components/RegisterForm.vue";
import UserList from "../components/UserList.vue";

const routes = [
  {
    path: "/",
    name: "HomeView",
    component: HomeView,
    meta: { public: true, guestOnly: true } // repercute en las últimas líneas del archivo para saber si el usuario ya está logueado
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
    path: "/users",
    name: "Users",
    component: UserList
  },
  {
    path: "/:catchAll(.*)*",
    component: ErrorNotFoundView,
    meta: { public: true }
  }
].concat(notesRoutes); // concatena con las rutas de las notas

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
        if (requiredAuthority && requiredAuthority != loggedUserAuthority) {
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
        next({ name: "NoteList", replace: true });
      } else {
        next();
      }
    }
  });
});

export default router;
