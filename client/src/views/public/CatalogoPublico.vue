<template>
  <div class="catalog-shell">
    <header class="catalog-header">
      <div class="brand">Catálogo público</div>
      <div class="auth-links">
        <router-link v-if="!user.logged" to="/login">Iniciar sesión</router-link>
        <router-link v-if="!user.logged" to="/register">Registrarse</router-link>
        <button v-if="user.logged" class="link-btn" @click="logout">Salir</button>
      </div>
    </header>

    <section class="search-bar">
      <input v-model="searchTerm" type="search" placeholder="Buscar exposiciones" @keyup.enter="search" />
      <button @click="search" :disabled="loading">Buscar</button>
    </section>

    <p class="subtitle">Exposiciones activas con ediciones publicadas vigentes.</p>

    <div v-if="loading" class="center">
      <div class="spinner-border" role="status"></div>
    </div>

    <div v-else-if="error" class="alert alert-danger" role="alert">
      {{ error }}
    </div>

    <div v-else class="cards-grid">
      <div v-if="expos.length === 0" class="empty">No hay exposiciones disponibles.</div>
      <article class="expo-card" v-for="expo in expos" :key="expo.idExposicion">
        <div class="expo-image">Imagen obra</div>
        <div class="expo-body">
          <div class="expo-meta">
            <span class="badge" :class="badgeClass(expo.estadoExpo)">{{ expo.estadoExpo }}</span>
          </div>
          <h3>{{ expo.titulo }}</h3>
          <p class="description">{{ expo.descripcion || 'Sin descripción' }}</p>
          <router-link :to="`/exposiciones/${expo.idExposicion}`" class="link-btn">Ver detalle</router-link>
        </div>
      </article>
    </div>
  </div>
</template>

<script>
import ExpoRepository from '@/repositories/ExpoRepository';
import { getStore } from '@/common/store';
import auth from '@/common/auth';

export default {
  name: 'CatalogoPublico',
  data() {
    return {
      expos: [],
      loading: false,
      error: '',
      searchTerm: '',
      user: getStore().state.user
    };
  },
  created() {
    this.loadPublic();
  },
  methods: {
    badgeClass(estado) {
      const map = { BORRADOR: 'badge-gray', ACTIVA: 'badge-green', ARCHIVADA: 'badge-dark' };
      return map[estado] || 'badge-gray';
    },
    async loadPublic() {
      this.loading = true;
      this.error = '';
      try {
        this.expos = await ExpoRepository.listPublic();
      } catch (e) {
        this.error = 'No se pudo cargar el catálogo público';
      } finally {
        this.loading = false;
      }
    },
    async search() {
      if (!this.searchTerm) return this.loadPublic();
      this.loading = true;
      this.error = '';
      try {
        this.expos = await ExpoRepository.searchPublic(this.searchTerm);
      } catch (e) {
        this.error = 'Error al buscar exposiciones';
      } finally {
        this.loading = false;
      }
    },
    logout() {
      auth.logout();
      this.$router.push('/login');
    }
  }
};
</script>

<style scoped>
.catalog-shell { max-width: 1200px; margin: 0 auto; padding: 32px 20px 48px; }
.catalog-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.brand { font-weight: 800; font-size: 24px; }
.auth-links { display: flex; gap: 12px; align-items: center; font-weight: 700; }
.auth-links a, .link-btn { color: #1f4b99; text-decoration: none; font-weight: 700; }
.link-btn { background: none; border: none; cursor: pointer; padding: 0; }
.search-bar { display: flex; gap: 12px; margin: 12px 0 8px; }
.search-bar input { flex: 1; padding: 12px 14px; border-radius: 12px; border: 1px solid #d9deea; }
.search-bar button { padding: 12px 18px; border-radius: 12px; border: none; background: #1f4b99; color: #fff; font-weight: 700; cursor: pointer; }
.subtitle { color: #5b6472; margin: 0 0 16px; }
.center { display: flex; justify-content: center; padding: 40px 0; }
.cards-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(260px, 1fr)); gap: 16px; }
.expo-card { background: #fff; border-radius: 14px; box-shadow: 0 8px 22px rgba(0,0,0,0.06); overflow: hidden; display: flex; flex-direction: column; }
.expo-image { background: linear-gradient(135deg, #dbe6ff, #f0f4ff); height: 160px; display: flex; align-items: center; justify-content: center; font-weight: 700; }
.expo-body { padding: 14px; display: flex; flex-direction: column; gap: 10px; }
.expo-meta { display: flex; gap: 10px; align-items: center; }
.badge { padding: 6px 10px; border-radius: 999px; font-weight: 700; font-size: 12px; }
.badge-gray { background: #eef1f6; color: #5b6472; }
.badge-green { background: #e3f7e9; color: #1f7a3d; }
.badge-dark { background: #dfe2e7; color: #2a2f36; }
.description { color: #4a5460; margin: 0; }
.empty { padding: 14px; background: #f6f8ff; border-radius: 10px; color: #4a5460; text-align: center; }
</style>
