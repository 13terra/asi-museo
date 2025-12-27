<template>
  <div class="container animate-slide-up">
    <section class="hero-section text-center mb-5">
      <p class="text-uppercase text-accent fw-bold mb-2">Bienvenido al Sistema de Museos</p>
      <h1 class="hero-title display-4 mb-4">Descubre, reserva y gestiona exposiciones</h1>
      <p class="lead mb-4 text-white-50">Explora el catálogo público, reserva tus visitas o administra exposiciones.</p>
      <div class="d-flex justify-content-center gap-3">
        <router-link class="btn btn-primary btn-lg" to="/catalogo">Ver catálogo</router-link>
        <router-link v-if="!isUserLogged" class="btn btn-outline-light btn-lg" to="/login">Iniciar sesión</router-link>
        <router-link v-else class="btn btn-outline-light btn-lg" :to="getDashboardRoute">
          <i class="bi bi-speedometer2"></i> Mi Panel
        </router-link>
      </div>
    </section>

    <div class="row g-4">
      <div class="col-md-4">
        <div class="card h-100 shadow-hover">
          <div class="card-body text-center p-4">
            <i class="bi bi-palette display-4 text-accent mb-3"></i>
            <h3 class="h4">Exposiciones Únicas</h3>
            <p class="text-muted">Descubre colecciones exclusivas y ediciones limitadas de artistas renombrados.</p>
          </div>
        </div>
      </div>
      <div class="col-md-4">
        <div class="card h-100 shadow-hover">
          <div class="card-body text-center p-4">
            <i class="bi bi-ticket-perforated display-4 text-accent mb-3"></i>
            <h3 class="h4">Reserva Fácil</h3>
            <p class="text-muted">Gestiona tus entradas y visitas de forma rápida y sencilla desde cualquier dispositivo.</p>
          </div>
        </div>
      </div>
      <div class="col-md-4">
        <div class="card h-100 shadow-hover">
          <div class="card-body text-center p-4">
            <i class="bi bi-people display-4 text-accent mb-3"></i>
            <h3 class="h4">Gestión Integral</h3>
            <p class="text-muted">Herramientas potentes para gestores y administradores de museos.</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getStore } from '@/common/store';
import { ROLES } from '@/constants';

export default {
  name: 'HomeLanding',
  data() {
    return { user: getStore().state.user };
  },
  computed: {

    isUserLogged() {
      return getStore().state.user.logged;
    },

    userAuthority() {
      return getStore().state.user.authority;
    },
    getDashboardRoute() {
      switch (this.userAuthority) {
        case ROLES.ADMIN:
          return '/admin';
        case ROLES.GESTOR:
          return '/gestor';
        case ROLES.VISITANTE:
          return '/mis-reservas';
        default:
          return '/catalogo';
      }
    }
  }
};

</script>

<style scoped>
/* Styles are now handled by global main.css */
</style>
