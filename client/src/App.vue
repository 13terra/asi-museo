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
              <router-link class="nav-link" to="/catalogo" active-class="active">
                <i class="bi bi-house-door"></i> Catálogo
              </router-link>
            </li>
          </ul>

          <!-- Menú visitante -->
          <ul class="navbar-nav me-auto mb-2 mb-lg-0" v-else-if="isVisitante">
            <li class="nav-item">
              <router-link class="nav-link" to="/catalogo" active-class="active">
                <i class="bi bi-house-door"></i> Catálogo
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/mis-reservas" active-class="active">
                <i class="bi bi-ticket-perforated"></i> Mis reservas
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
              <router-link class="nav-link" to="/catalogo" active-class="active">
                <i class="bi bi-eye"></i> Vista Pública
              </router-link>
            </li>
            <li>
              <router-link to="/gestor/obras" class="nav-link">
                <i class="bi bi-palette"></i> Obras
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
              <router-link class="nav-link" to="/admin/exposiciones" active-class="active">
                <i class="bi bi-kanban"></i> Exposiciones
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/catalogo" active-class="active">
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
      v-if="notification.show"
      class="toast-wrap"
      :class="`toast-${notification.type}`"
      role="alert"
    >
      <div class="toast-message">{{ notification.message }}</div>
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
    notification() {
      return this.store.state.notification;
    },
    isAdmin() {
      return auth.isAdmin();
    },
    isGestor() {
      return auth. isGestor();
    },
    isVisitante() {
      return auth. isVisitante();
    }
  },
  methods: {
    // ✅ CORRECCIÓN: Hacer logout asíncrono y esperar antes de redirigir
    async handleLogout() {
      if (confirm("¿Estás seguro de que quieres cerrar sesión?")) {
        try {
          // ✅ Esperar a que logout termine
          await auth.logout();
          
          // ✅ Pequeño delay para asegurar limpieza de estado
          await new Promise(resolve => setTimeout(resolve, 100));
          
          // ✅ Usar replace para evitar volver con botón atrás
          await this.$router.replace("/login");
        } catch (e) {
          console.error("Error al cerrar sesión:", e);
          // Aunque falle, limpiar y redirigir
          await this.$router.replace("/login");
        }
      }
    },
    getHomeRoute() {
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

.toast-wrap {
  position: fixed;
  right: 20px;
  bottom: 24px;
  min-width: 260px;
  max-width: 360px;
  background: #111827;
  color: #f9fafb;
  border-radius: 12px;
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.2);
  padding: 14px 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  z-index: 1050;
}

.toast-wrap .btn-close {
  filter: invert(1);
}

.toast-message {
  font-weight: 600;
}

.toast-info {
  border-left: 4px solid #3b82f6;
}

.toast-success {
  border-left: 4px solid #10b981;
}

.toast-warning {
  border-left: 4px solid #f59e0b;
}

.toast-error {
  border-left: 4px solid #ef4444;
}
</style>
