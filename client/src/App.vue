<template>
  <div id="app">
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
      <div class="container-fluid">
        <router-link class="navbar-brand" :to="getHomeRoute()">
          <i class="bi bi-building"></i> Sistema de Museos
        </router-link>
        <button
          class="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarContent"
          aria-controls="navbarContent"
          aria-expanded="false"
          aria-label="Toggle navigation"
        >
          <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarContent">
          <!-- Menú público -->
          <ul class="navbar-nav me-auto mb-2 mb-lg-0" v-if="!store.state.user.logged">
            <li class="nav-item">
              <router-link class="nav-link" to="/" active-class="active">
                <i class="bi bi-house-door"></i> Catálogo
              </router-link>
            </li>
          </ul>

          <!-- Menú visitante -->
          <ul class="navbar-nav me-auto mb-2 mb-lg-0" v-else-if="isVisitante">
            <li class="nav-item">
              <router-link class="nav-link" to="/" active-class="active">
                <i class="bi bi-house-door"></i> Catálogo
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/mis-reservas" active-class="active">
                <i class="bi bi-ticket-perforated"></i> Mis Reservas
              </router-link>
            </li>
          </ul>

          <!-- Menú gestor -->
          <ul class="navbar-nav me-auto mb-2 mb-lg-0" v-else-if="isGestor">
            <li class="nav-item">
              <router-link class="nav-link" to="/gestor" active-class="active">
                <i class="bi bi-kanban"></i> Panel Gestor
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/gestor/obras" active-class="active">
                <i class="bi bi-palette"></i> Catálogo Obras
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/" active-class="active">
                <i class="bi bi-eye"></i> Vista Pública
              </router-link>
            </li>
          </ul>

          <!-- Menú admin -->
          <ul class="navbar-nav me-auto mb-2 mb-lg-0" v-else-if="isAdmin">
            <li class="nav-item">
              <router-link class="nav-link" to="/admin" active-class="active">
                <i class="bi bi-shield-check"></i> Panel Admin
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/admin/usuarios" active-class="active">
                <i class="bi bi-people"></i> Usuarios
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/admin/salas" active-class="active">
                <i class="bi bi-grid-3x3"></i> Salas
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/gestor/obras" active-class="active">
                <i class="bi bi-palette"></i> Obras
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/" active-class="active">
                <i class="bi bi-eye"></i> Vista Pública
              </router-link>
            </li>
          </ul>

          <!-- Autenticación / usuario -->
          <div class="d-flex align-items-center ms-auto">
            <template v-if="!store.state.user.logged">
              <router-link to="/login" class="btn btn-outline-light btn-sm me-2">
                <i class="bi bi-box-arrow-in-right"></i> Iniciar Sesión
              </router-link>
              <router-link to="/register" class="btn btn-primary btn-sm">
                <i class="bi bi-person-plus"></i> Registrarse
              </router-link>
            </template>
            <template v-else>
              <span class="navbar-text text-light me-3">
                <i class="bi bi-person-circle"></i>
                <strong>{{ store.state.user.login }}</strong>
                <span class="badge bg-secondary ms-2">{{ store.state.user.authority }}</span>
              </span>
              <button class="btn btn-outline-light btn-sm" @click="handleLogout" title="Cerrar sesión">
                <i class="bi bi-box-arrow-right"></i> Salir
              </button>
            </template>
          </div>
        </div>
      </div>
    </nav>

    <div
      v-if="store.state.notification.show"
      class="alert alert-dismissible fade show m-3"
      :class="`alert-${store.state.notification.type}`"
      role="alert"
    >
      {{ store.state.notification.message }}
      <button type="button" class="btn-close" @click="clearNotification"></button>
    </div>

    <main class="container-fluid mt-4">
      <router-view />
    </main>

    <footer class="bg-dark text-light text-center py-3 mt-5">
      <p class="mb-0">&copy; 2025 Sistema de Gestión de Museos - ASI Trabajo Tutelado</p>
    </footer>
  </div>
</template>

<script>
import { getStore, clearNotification } from "./common/store";
import auth from "./common/auth";
import { ROLES } from "./constants";

export default {
  data() {
    return {
      store: getStore()
    };
  },
  computed: {
    isAdmin() {
      return auth.isAdmin();
    },
    isGestor() {
      return auth.isGestor();
    },
    isVisitante() {
      return auth.isVisitante();
    }
  },
  methods: {
    handleLogout() {
      if (confirm("¿Estás seguro de que quieres cerrar sesión?")) {
        auth.logout();
        this.$router.push("/");
      }
    },
    getHomeRoute() {
      if (!this.store.state.user.logged) return "/";
      if (this.store.state.user.authority === ROLES.ADMIN) return "/admin";
      if (this.store.state.user.authority === ROLES.GESTOR) return "/gestor";
      return "/";
    },
    clearNotification() {
      clearNotification();
    }
  }
};
</script>

<style lang="scss">
#app {
  font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

main {
  flex: 1;
}

.navbar-brand {
  font-size: 1.5rem;
  font-weight: bold;
}

.badge {
  font-size: 0.75rem;
}
</style>
