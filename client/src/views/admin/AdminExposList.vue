<template>
  <div class="page">
    <header class="head">
      <div>
        <p class="eyebrow">Panel de administración</p>
        <h1>Exposiciones</h1>
        <p class="muted">Listado completo y creación rápida.</p>
      </div>
    </header>

    <section class="card create">
      <h3>Crear nueva exposición</h3>
      <div class="form-grid">
        <div>
          <label>Título</label>
          <input v-model="form.titulo" type="text" placeholder="Título" />
        </div>
        <div class="full">
          <label>Descripción</label>
          <textarea v-model="form.descripcion" rows="2" placeholder="Descripción"></textarea>
        </div>
      </div>
      <div class="actions">
        <button class="btn primary" @click="create" :disabled="loading || !form.titulo">Crear</button>
        <span v-if="error" class="error">{{ error }}</span>
      </div>
    </section>

    <section class="card">
      <div class="section-head">
        <div>
          <p class="eyebrow">Listado</p>
          <h3>Exposiciones registradas</h3>
        </div>
        <label class="switch">
          <input type="checkbox" v-model="incluirArchivadas" @change="load" />
          <span>Mostrar archivadas</span>
        </label>
      </div>
      <div v-if="loading" class="center"><div class="spinner-border" role="status"></div></div>
      <p v-else-if="error" class="error">{{ error }}</p>
      <div v-else class="table">
        <div class="row header">
          <span>Título</span><span>Estado</span><span>Ediciones</span><span>Acciones</span>
        </div>
        <div v-if="expos.length === 0" class="empty">No hay exposiciones registradas.</div>
        <div v-for="expo in expos" :key="expo.idExposicion" class="row">
          <span>{{ expo.titulo }}</span>
          <span class="pill">{{ expo.estadoExpo }}</span>
          <span>{{ expo.numEdiciones || 0 }}</span>
          <span class="actions">
            <router-link class="link" :to="{ name: 'ExposicionDetalle', params: { idExposicion: expo.idExposicion } }">Detalle</router-link>
            <button class="btn ghost" @click="archivar(expo)" :disabled="actionLoading">{{ expo.estadoExpo === 'ARCHIVADA' ? 'Desarchivar' : 'Archivar' }}</button>
            <button class="btn danger" @click="eliminar(expo)" :disabled="actionLoading">Eliminar</button>
          </span>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
import ExpoRepository from '@/repositories/ExpoRepository';

export default {
  name: 'AdminExposList',
  data() {
    return { expos: [], loading: true, actionLoading: false, error: '', form: { titulo: '', descripcion: '' }, incluirArchivadas: true };
  },
  created() { this.load(); },
  methods: {
    async load() {
      this.loading = true; this.error = '';
      try {
        this.expos = await ExpoRepository.getAllForAdmin(this.incluirArchivadas);
      } catch (e) {
        this.error = 'No se pudo cargar el listado';
      } finally { this.loading = false; }
    },
    async create() {
      this.loading = true; this.error = '';
      try {
        await ExpoRepository.create({ ...this.form });
        this.form.titulo = ''; this.form.descripcion = '';
        await this.load();
      } catch (e) {
        this.error = 'Error al crear la exposición';
      } finally { this.loading = false; }
    },
    async archivar(expo) {
      if (this.actionLoading) return;
      this.actionLoading = true; this.error = '';
      try {
        if (expo.estadoExpo === 'ARCHIVADA') {
          await ExpoRepository.desarchivar(expo.idExposicion);
          await ExpoRepository.update(expo.idExposicion, { estado: 'BORRADOR' });
        } else {
          await ExpoRepository.archivar(expo.idExposicion);
        }
        await this.load();
      } catch (e) {
        this.error = e.response?.data?.message || 'No se pudo actualizar el estado';
      } finally {
        this.actionLoading = false;
      }
    },
    async eliminar(expo) {
      if (!confirm('¿Eliminar esta exposición? No se puede deshacer.')) return;
      this.actionLoading = true; this.error = '';
      try {
        await ExpoRepository.delete(expo.idExposicion);
        await this.load();
      } catch (e) {
        this.error = e.response?.data?.message || 'No se pudo eliminar (verifica reservas o ediciones publicadas)';
      } finally {
        this.actionLoading = false;
      }
    }
  }
};
</script>

<style scoped>
.page { max-width: 1100px; margin: 0 auto; padding: 28px 18px 48px; display: flex; flex-direction: column; gap: 16px; }
.head { display: flex; justify-content: space-between; align-items: center; gap: 12px; }
.eyebrow { text-transform: uppercase; letter-spacing: .08em; font-size: 12px; color: #5b6472; margin: 0; }
.muted { color: #5b6472; margin: 0; }
.btn { padding: 10px 14px; border-radius: 10px; border: 1px solid #d9deea; background: #fff; cursor: pointer; font-weight: 700; }
.btn.primary { background: linear-gradient(135deg,#1f4b99,#153a7a); color: #fff; border: none; }
.card { background: #fff; border: 1px solid #e9ecf5; border-radius: 14px; padding: 16px; box-shadow: 0 8px 18px rgba(0,0,0,0.05); display: flex; flex-direction: column; gap: 12px; }
.card.create { gap: 10px; }
.form-grid { display: grid; grid-template-columns: repeat(auto-fit,minmax(260px,1fr)); gap: 12px; }
.form-grid .full { grid-column: 1 / -1; }
.form-grid input, .form-grid textarea { width: 100%; border-radius: 10px; border: 1px solid #d9deea; padding: 10px; }
.section-head { display: flex; justify-content: space-between; align-items: center; gap: 10px; }
.table { display: flex; flex-direction: column; gap: 6px; }
.row { display: grid; grid-template-columns: 2fr 1fr 1fr 1fr; gap: 8px; padding: 10px; border: 1px solid #eef1f6; border-radius: 10px; align-items: center; }
.row.header { background: #f6f8ff; font-weight: 700; }
.pill { padding: 6px 10px; border-radius: 999px; background: #eef1f6; font-weight: 700; }
.empty { padding: 12px; background: #f6f8ff; border-radius: 10px; color: #4a5460; }
.actions { display: flex; align-items: center; gap: 8px; }
.link { color: #1f4b99; text-decoration: none; font-weight: 700; }
.error { color: #d23b3b; margin: 0; }
.center { display: flex; justify-content: center; padding: 16px 0; }
</style>
