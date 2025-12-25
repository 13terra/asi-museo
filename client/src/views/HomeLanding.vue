<template>
  <div class="home-shell">
    <section class="hero">
      <div class="hero-copy">
        <p class="eyebrow">Bienvenido al Sistema de Museos</p>
        <h1>Descubre, reserva y gestiona exposiciones en un solo lugar.</h1>
        <p class="lead">Explora el catálogo público, reserva tus visitas o administra exposiciones si formas parte del equipo.</p>
        <div class="cta">
          <router-link class="btn primary" to="/catalogo">Ver catálogo</router-link>
          <!-- Usar computed para reactividad, si no no desaparece el login-->
          <router-link v-if="!isUserLogged" class="btn ghost" to="/login">Iniciar sesión</router-link>
          <router-link> v-else class="btn ghost" :to="getDashboardRoute"
            <i class="bi bi-speedometer2"></i> Mi Panel
          </router-link>
        </div>
      </div>
      <div class="hero-panel">
        <div class="card">
          <h3>Próximas aperturas</h3>
          <ul>
            <li>
              <strong>Artes del Renacimiento</strong>
              <span>Edición 2025 · Sala Norte</span>
            </li>
            <li>
              <strong>Escultura Contemporánea</strong>
              <span>Edición 2025 · Sala Central</span>
            </li>
            <li>
              <strong>El museo y la tecnología</strong>
              <span>Edición 2025 · Sala Este</span>
            </li>
          </ul>
          <router-link class="link" to="/catalogo">Ir al catálogo</router-link>
        </div>
      </div>
    </section>
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
        case ROLES. VISITANTE:
          return '/mis-reservas';
        default:
          return '/catalogo';
      }
    }
  }
};

</script>

<style scoped>
.home-shell { max-width: 1200px; margin: 0 auto; padding: 48px 20px 64px; }
.hero { display: grid; grid-template-columns: repeat(auto-fit,minmax(320px,1fr)); gap: 24px; align-items: center; }
.hero-copy h1 { margin: 8px 0 12px; font-size: clamp(28px, 4vw, 40px); line-height: 1.15; }
.lead { color: #4a5460; margin: 0 0 18px; }
.eyebrow { text-transform: uppercase; letter-spacing: 0.08em; font-weight: 700; color: #5b6472; margin: 0; }
.cta { display: flex; gap: 12px; flex-wrap: wrap; }
.btn { padding: 12px 16px; border-radius: 12px; font-weight: 700; text-decoration: none; border: 1px solid #d9deea; color: #1f2a44; }
.btn.primary { background: linear-gradient(135deg,#1f4b99,#153a7a); color: #fff; border: none; }
.btn.ghost { background: #fff; }
.hero-panel .card { background: #0f172a; color: #e2e8f0; border-radius: 16px; padding: 18px; box-shadow: 0 12px 30px rgba(0,0,0,0.12); }
.hero-panel h3 { margin: 0 0 12px; }
.hero-panel ul { list-style: none; padding: 0; margin: 0 0 12px; display: flex; flex-direction: column; gap: 10px; }
.hero-panel li strong { display: block; }
.hero-panel li span { color: #94a3b8; font-size: 14px; }
.link { color: #93c5fd; text-decoration: none; font-weight: 700; }
</style>
