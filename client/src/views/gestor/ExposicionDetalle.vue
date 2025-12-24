<template>
  <div class="detail-shell" v-if="!loading && expo">
    <div class="detail-header">
      <div class="breadcrumbs">
        <router-link to="/gestor">Exposiciones</router-link>
        <span>/</span>
        <span>{{ expo.titulo }}</span>
      </div>
      <div class="status">
        <span class="badge" :class="badgeClass(expo.estadoExpo)">{{ expo.estadoExpo }}</span>
      </div>
    </div>

    <div class="hero">
      <div>
        <p class="eyebrow">Detalle de exposición</p>
        <h1>{{ expo.titulo }}</h1>
        <p class="subtitle">{{ expo.descripcion || 'Sin descripción' }}</p>
        <div class="meta">
          <div class="pill">Creador: {{ expo.creador?.login || 'ADMIN' }}</div>
          <div class="pill">Gestores: {{ expo.gestores?.length || 0 }}</div>
          <div class="pill">Ediciones: {{ ediciones.length }}</div>
        </div>
      </div>
      <div class="hero-actions">
        <button class="btn-ghost" @click="load" :disabled="loading">Recargar</button>
        <router-link class="btn-primary" :to="`/gestor/exposiciones/${expo.idExposicion}/permisos`">Permisos</router-link>
      </div>
    </div>

    <section class="card-box">
      <header class="ed-header">
        <div>
          <p class="eyebrow">Ediciones</p>
          <h3>Ediciones asociadas</h3>
        </div>
        <div class="ed-actions">
          <input type="date" v-model="edicionForm.fechaInicio" />
          <input type="date" v-model="edicionForm.fechaFin" />
          <button class="btn-primary" @click="createEdicion" :disabled="edSaving">Crear edición</button>
        </div>
      </header>
      <p v-if="edError" class="error">{{ edError }}</p>
      <div class="editions">
        <div v-for="ed in ediciones" :key="ed.idEdicion" class="edition-row">
          <div>
            <h4>{{ ed.nombre || ed.fechaInicio }}</h4>
            <p class="muted">{{ ed.fechaInicio }} → {{ ed.fechaFin }}</p>
          </div>
          <div class="row-actions">
            <router-link :to="`/gestor/exposiciones/${expo.idExposicion}/ediciones/${ed.idEdicion}`" class="btn-ghost">Gestionar</router-link>
            <span class="chip" :class="edBadge(ed.estadoEdicion)">{{ ed.estadoEdicion }}</span>
          </div>
        </div>
        <p v-if="ediciones.length === 0" class="muted">Aún no hay ediciones creadas.</p>
      </div>
    </section>
  </div>
  <div v-else class="loading">
    <div class="spinner-border" role="status"></div>
  </div>
</template>

<script>
import ExpoRepository from '@/repositories/ExpoRepository';
import EdicionRepository from '@/repositories/EdicionRepository';

export default {
  name: 'ExposicionDetalle',
  data() {
    return {
      expo: null,
      loading: true,
      ediciones: [],
      edSaving: false,
      edError: '',
      edicionForm: { fechaInicio: '', fechaFin: '' }
    };
  },
  created() { this.load(); },
  methods: {
    badgeClass(estado) {
      const map = { ACTIVA: 'badge-success', EN_PREPARACION: 'badge-secondary', BORRADOR: 'badge-secondary', ARCHIVADA: 'badge-dark' };
      return map[estado] || 'badge-secondary';
    },
    edBadge(estado) {
      return { PUBLICADA: 'chip chip-green', BORRADOR: 'chip chip-gray', FINALIZADA: 'chip chip-dark', CANCELADA: 'chip chip-red' }[estado] || 'chip';
    },
    async load() {
      this.loading = true; this.edError = '';
      try {
        const id = this.$route.params.idExposicion || this.$route.params.id;
        this.expo = await ExpoRepository.detailAdmin(id);
        await this.loadEdiciones();
      } catch (e) { this.edError = 'No se pudo cargar el detalle'; }
      finally { this.loading = false; }
    },
    async loadEdiciones() {
      const id = this.$route.params.idExposicion || this.$route.params.id;
      this.ediciones = await EdicionRepository.getByExposicion(id);
    },
    async createEdicion() {
      if (!this.edicionForm.fechaInicio || !this.edicionForm.fechaFin) return;
      this.edSaving = true; this.edError = '';
      try {
        const id = this.$route.params.idExposicion || this.$route.params.id;
        await EdicionRepository.create(id, { ...this.edicionForm });
        this.edicionForm = { fechaInicio: '', fechaFin: '' };
        await this.loadEdiciones();
      } catch (e) { this.edError = 'No se pudo crear la edición'; }
      finally { this.edSaving = false; }
    }
  }
};
</script>

<style scoped>
.detail-shell { max-width: 1100px; margin: 0 auto; padding: 32px 20px 48px; display: flex; flex-direction: column; gap: 16px; }
.detail-header { display: flex; justify-content: space-between; align-items: center; gap: 12px; }
.breadcrumbs { display: flex; gap: 6px; align-items: center; color: #4a5460; }
.breadcrumbs a { color: #1f4b99; text-decoration: none; font-weight: 700; }
.status .badge { padding: 6px 10px; border-radius: 999px; font-weight: 700; font-size: 12px; }
.badge-success { background: #e3f7e9; color: #1f7a3d; }
.badge-secondary { background: #eef1f6; color: #5b6472; }
.badge-dark { background: #dfe2e7; color: #2a2f36; }
.hero { display: flex; justify-content: space-between; gap: 16px; align-items: center; border: 1px solid #e5e9f4; border-radius: 14px; padding: 16px; background: #fff; box-shadow: 0 6px 16px rgba(0,0,0,0.05); }
.eyebrow { text-transform: uppercase; letter-spacing: .08em; font-size: 12px; color: #5b6472; margin: 0; }
.subtitle { color: #4a5460; }
.meta { display: flex; gap: 10px; flex-wrap: wrap; }
.pill { background: #eef1f6; padding: 6px 10px; border-radius: 10px; font-weight: 700; }
.btn-primary { border: none; background: linear-gradient(135deg,#1f4b99,#153a7a); color: #fff; padding: 10px 14px; border-radius: 10px; font-weight: 700; cursor: pointer; text-decoration: none; }
.btn-ghost { border: 1px solid #d9deea; background: #fff; color: #1f4b99; padding: 10px 14px; border-radius: 10px; font-weight: 700; cursor: pointer; }
.card-box { background: #fff; border: 1px solid #e9ecf5; border-radius: 14px; padding: 16px; box-shadow: 0 8px 18px rgba(0,0,0,0.05); display: flex; flex-direction: column; gap: 12px; }
.ed-header { display: flex; justify-content: space-between; align-items: center; gap: 10px; flex-wrap: wrap; }
.ed-actions { display: flex; gap: 8px; align-items: center; }
.editions { display: flex; flex-direction: column; gap: 10px; }
.edition-row { display: flex; justify-content: space-between; align-items: center; border: 1px solid #eef1f6; border-radius: 12px; padding: 10px; gap: 10px; }
.row-actions { display: flex; gap: 10px; align-items: center; }
.chip { padding: 6px 10px; border-radius: 999px; font-weight: 700; font-size: 12px; }
.chip-green { background: #e3f7e9; color: #1f7a3d; }
.chip-gray { background: #eef1f6; color: #4a5460; }
.chip-dark { background: #dfe2e7; color: #2a2f36; }
.chip-red { background: #fff0f0; color: #d23b3b; }
.error { color: #d23b3b; margin: 0; }
.loading { display: flex; justify-content: center; padding: 48px 0; }
</style>
