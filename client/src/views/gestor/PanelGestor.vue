<template>
  <div class="panel-shell">
    <div class="panel-header">
      <div>
        <h2>Mis exposiciones (GESTOR)</h2>
        <p>Listado de exposiciones que gestionas y alta de nuevas.</p>
      </div>
      <div class="header-actions">
        <label class="switch">
          <input type="checkbox" v-model="incluirArchivadas" @change="load" />
          <span>Mostrar archivadas</span>
        </label>
        <button v-if="puedeCrear" class="btn-primary" @click="toggleCreate" :disabled="creating">+ Nueva Exposición</button>
      </div>
    </div>

    <div v-if="creating" class="card-create">
      <h5>Crear exposición</h5>
      <div class="form-grid">
        <div>
          <label>Título</label>
          <input v-model="form.titulo" type="text" required />
        </div>
        <div class="full">
          <label>Descripción</label>
          <textarea v-model="form.descripcion" rows="3"></textarea>
        </div>
      </div>
      <div class="actions">
        <button class="btn-primary" @click="create" :disabled="loading || !form.titulo">Crear</button>
        <button class="btn-ghost" @click="toggleCreate">Cancelar</button>
      </div>
    </div>

    <div v-if="loading" class="center"><div class="spinner-border" role="status"></div></div>
    <div v-else-if="error" class="alert alert-danger">{{ error }}</div>

    <div v-else class="cards-grid">
      <div v-if="expos.length === 0" class="empty">No hay exposiciones asignadas.</div>
      <article class="expo-card" v-for="expo in expos" :key="expo.idExposicion">
        <div class="expo-top">
          <div>
            <h4>{{ expo.titulo }}</h4>
            <p class="muted">Ediciones: {{ expo.numEdiciones }}</p>
          </div>
          <span class="badge" :class="badgeClass(expo.estadoExpo)">{{ expo.estadoExpo }}</span>
        </div>
        <p class="description">{{ expo.descripcion || 'Sin descripción' }}</p>
        <div class="pill">Permiso: {{ expo.miPermiso || '-' }}</div>
        <div class="actions">
          <router-link :to="`/gestor/exposiciones/${expo.idExposicion}`" class="btn-link">Detalle</router-link>
          <button v-if="esCreador(expo)" class="btn-ghost" @click="toggleArchive(expo)" :disabled="actionLoading">{{ expo.estadoExpo === 'ARCHIVADA' ? 'Desarchivar' : 'Archivar' }}</button>
          <button v-if="esCreador(expo)" class="btn-danger" @click="eliminar(expo)" :disabled="actionLoading">Eliminar</button>
        </div>
      </article>
    </div>
  </div>
</template>

<script>
import ExpoRepository from '@/repositories/ExpoRepository';

export default {
  name: 'PanelGestor',
  data() {
    return { expos: [], loading: false, actionLoading: false, error: '', incluirArchivadas: false, creating: false, form: { titulo: '', descripcion: '' }, puedeCrear: false };
  },
  created() { this.load(); },
  methods: {
    badgeClass(estado) {
      const map = { ACTIVA: 'badge-success', EN_PREPARACION: 'badge-secondary', BORRADOR: 'badge-secondary', ARCHIVADA: 'badge-dark' };
      return map[estado] || 'badge-secondary';
    },
    toggleCreate() {
      this.creating = !this.creating;
      if (!this.creating) { this.form.titulo = ''; this.form.descripcion = ''; }
    },
    async load() {
      this.loading = true; this.error = '';
      try {
        const raw = await ExpoRepository.listGestor({ incluirArchivadas: this.incluirArchivadas });
        this.expos = (raw || []).map(expo => {
          const permiso = (expo.miPermiso || expo.permiso || expo.tipoPermiso || '').toString().toUpperCase();
          return { ...expo, miPermiso: permiso };
        });
        this.puedeCrear = this.expos.some(this.esCreador);
      } catch (e) { this.error = 'No se pudieron cargar las exposiciones'; }
      finally { this.loading = false; }
    },
    async create() {
      this.loading = true; this.error = '';
      try {
        await ExpoRepository.create({ ...this.form });
        this.toggleCreate();
        await this.load();
      } catch (e) { this.error = 'Error al crear la exposición'; }
      finally { this.loading = false; }
    },
    goEdit(expo) { this.$router.push({ name: 'ExposicionDetalle', params: { idExposicion: expo.idExposicion } }); },
    esCreador(expo) { return (expo.miPermiso || '').toUpperCase() === 'CREADOR'; },
    async toggleArchive(expo) {
      if (this.actionLoading) return;
      this.actionLoading = true; this.error = '';
      try {
        if (expo.estadoExpo === 'ARCHIVADA') await ExpoRepository.desarchivar(expo.idExposicion);
        else await ExpoRepository.archivar(expo.idExposicion);
        await this.load();
      } catch (e) {
        this.error = e.response?.data?.message || 'No se pudo cambiar el estado';
      } finally {
        this.actionLoading = false;
      }
    },
    async eliminar(expo) {
      if (!confirm('¿Eliminar esta exposición?')) return;
      this.actionLoading = true; this.error = '';
      try {
        await ExpoRepository.delete(expo.idExposicion);
        await this.load();
      } catch (e) {
        this.error = e.response?.data?.message || 'No se pudo eliminar';
      } finally {
        this.actionLoading = false;
      }
    }
  }
};
</script>

<style scoped>
.panel-shell { max-width: 1200px; margin: 0 auto; padding: 32px 20px 48px; display: flex; flex-direction: column; gap: 16px; }
.panel-header { display: flex; justify-content: space-between; gap: 12px; flex-wrap: wrap; }
.header-actions { display: flex; align-items: center; gap: 12px; }
.switch { display: flex; align-items: center; gap: 6px; font-weight: 600; }
.btn-primary { background: #1f4b99; color: #fff; border: none; border-radius: 10px; padding: 10px 14px; font-weight: 700; cursor: pointer; }
.btn-ghost { border: 1px solid #d9deea; background: transparent; border-radius: 10px; padding: 8px 12px; cursor: pointer; }
.cards-grid { display: grid; grid-template-columns: repeat(auto-fit,minmax(280px,1fr)); gap: 12px; }
.expo-card { background: #fff; border-radius: 14px; box-shadow: 0 8px 22px rgba(0,0,0,0.06); padding: 14px; display: flex; flex-direction: column; gap: 10px; }
.expo-top { display: flex; justify-content: space-between; gap: 10px; }
.muted { color: #5b6472; margin: 0; }
.pill { background: #eef1f6; padding: 6px 10px; border-radius: 10px; font-weight: 700; width: fit-content; }
.badge { padding: 6px 10px; border-radius: 999px; font-weight: 700; font-size: 12px; }
.badge-success { background: #e3f7e9; color: #1f7a3d; }
.badge-secondary { background: #eef1f6; color: #5b6472; }
.badge-dark { background: #dfe2e7; color: #2a2f36; }
.empty { padding: 14px; background: #f6f8ff; border-radius: 10px; color: #4a5460; }
.card-create { background: #fff; border-radius: 14px; padding: 16px; box-shadow: 0 8px 22px rgba(0,0,0,0.06); display: flex; flex-direction: column; gap: 12px; }
.form-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(260px, 1fr)); gap: 12px; }
.form-grid .full { grid-column: 1 / -1; }
.form-grid input, .form-grid textarea { width: 100%; border-radius: 10px; border: 1px solid #d9deea; padding: 10px; }
.center { display: flex; justify-content: center; padding: 30px 0; }
</style>
