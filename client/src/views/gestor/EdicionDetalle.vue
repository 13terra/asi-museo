<template>
  <div class="page" v-if="edicion">
    <header class="head">
      <div>
        <p class="eyebrow">Edición #{{ edicion.idEdicion }}</p>
        <h1>{{ edicion.nombre || edicion.idEdicion }}</h1>
        <p class="muted">{{ edicion.fechaInicio }} → {{ edicion.fechaFin }}</p>
      </div>
      <div class="chips">
        <span class="chip" :class="stateClass(edicion.estado)">{{ edicion.estado }}</span>
        <button class="btn" @click="load" :disabled="loading">Recargar</button>
      </div>
    </header>

    <section class="card">
      <h3>Actualizar edición</h3>
      <div class="form-grid">
        <label>Fecha inicio<input type="date" v-model="form.fechaInicio" /></label>
        <label>Fecha fin<input type="date" v-model="form.fechaFin" /></label>
        <label>Estado
          <select v-model="form.estado">
            <option value="">(sin cambio)</option>
            <option value="BORRADOR">BORRADOR</option>
            <option value="PUBLICADA">PUBLICADA</option>
            <option value="FINALIZADA">FINALIZADA</option>
            <option value="CANCELADA">CANCELADA</option>
          </select>
        </label>
      </div>
      <div class="actions">
        <button class="btn primary" :disabled="saving" @click="update">Guardar</button>
        <button class="btn" :disabled="saving" @click="publicar">Publicar</button>
        <button class="btn" :disabled="saving" @click="cancelar">Cancelar</button>
        <button class="btn danger" :disabled="saving" @click="eliminar">Eliminar</button>
      </div>
      <p v-if="error" class="error">{{ error }}</p>
    </section>

    <section class="card links">
      <h3>Gestiones de la edición</h3>
      <div class="link-grid">
        <router-link :to="`/gestor/ediciones/${edicion.idEdicion}/piezas`" class="btn-ghost">Piezas expuestas</router-link>
        <router-link :to="`/gestor/ediciones/${edicion.idEdicion}/sesiones`" class="btn-ghost">Sesiones</router-link>
        <router-link :to="`/gestor/ediciones/${edicion.idEdicion}/salas`" class="btn-ghost">Salas</router-link>
      </div>
    </section>
  </div>
  <div v-else class="center"><div class="spinner-border" role="status"></div></div>
</template>

<script>
import EdicionRepository from '@/repositories/EdicionRepository';
import { ESTADOS_EDICION } from '@/constants';

export default {
  name: 'EdicionDetalle',
  data() {
    return { edicion: null, form: { fechaInicio: '', fechaFin: '', estado: '' }, loading: true, saving: false, error: '' };
  },
  async created() { await this.load(); },
  methods: {
    stateClass(estado) {
      return { [ESTADOS_EDICION.BORRADOR]: 'chip-gray', [ESTADOS_EDICION.PUBLICADA]: 'chip-green', [ESTADOS_EDICION.FINALIZADA]: 'chip-dark', [ESTADOS_EDICION.CANCELADA]: 'chip-red' }[estado] || 'chip-gray';
    },
    async load() {
      this.loading = true; this.error = '';
      try {
        const id = this.$route.params.idEdicion || this.$route.params.id;
        this.edicion = await EdicionRepository.getDetalleForAdmin(id);
        this.form.fechaInicio = this.edicion.fechaInicio;
        this.form.fechaFin = this.edicion.fechaFin;
        this.form.estado = '';
      } catch (e) { this.error = 'No se pudo cargar la edición.'; }
      finally { this.loading = false; }
    },
    async update() {
      this.saving = true; this.error = '';
      try {
        const id = this.$route.params.idEdicion || this.$route.params.id;
        await EdicionRepository.update(id, { ...this.form });
        await this.load();
      } catch (e) { this.error = 'No se pudo guardar.'; }
      finally { this.saving = false; }
    },
    async publicar() {
      this.saving = true; this.error = '';
      try { await EdicionRepository.publicar(this.edicion.idEdicion); await this.load(); }
      catch (e) { this.error = 'No se pudo publicar.'; }
      finally { this.saving = false; }
    },
    async cancelar() {
      this.saving = true; this.error = '';
      try { await EdicionRepository.cancelar(this.edicion.idEdicion); await this.load(); }
      catch (e) { this.error = 'No se pudo cancelar.'; }
      finally { this.saving = false; }
    },
    async eliminar() {
      if (!confirm('¿Eliminar esta edición (solo BORRADOR)?')) return;
      this.saving = true; this.error = '';
      try { await EdicionRepository.delete(this.edicion.idEdicion); this.$router.push('/gestor'); }
      catch (e) { this.error = 'No se pudo eliminar.'; }
      finally { this.saving = false; }
    }
  }
};
</script>

<style scoped>
.page { max-width: 1000px; margin: 0 auto; padding: 28px 18px 48px; display: flex; flex-direction: column; gap: 16px; }
.head { display: flex; justify-content: space-between; align-items: center; gap: 12px; }
.eyebrow { text-transform: uppercase; letter-spacing: .08em; font-size: 12px; color: #5b6472; margin: 0; }
.muted { color: #5b6472; margin: 0; }
.chips { display: flex; gap: 8px; align-items: center; }
.chip { padding: 6px 10px; border-radius: 999px; font-weight: 700; font-size: 12px; background: #eef1f6; color: #2a2f36; }
.chip-green { background: #e3f7e9; color: #1f7a3d; }
.chip-gray { background: #eef1f6; color: #4a5460; }
.chip-dark { background: #dfe2e7; color: #2a2f36; }
.chip-red { background: #fff0f0; color: #d23b3b; }
.card { background: #fff; border: 1px solid #e9ecf5; border-radius: 14px; padding: 16px; box-shadow: 0 8px 18px rgba(0,0,0,0.05); display: flex; flex-direction: column; gap: 12px; }
.form-grid { display: grid; grid-template-columns: repeat(auto-fit,minmax(240px,1fr)); gap: 10px; }
.actions { display: flex; gap: 10px; flex-wrap: wrap; }
.btn { padding: 10px 14px; border-radius: 10px; border: 1px solid #d9deea; background: #fff; cursor: pointer; font-weight: 700; }
.btn.primary { background: linear-gradient(135deg,#1f4b99,#153a7a); color: #fff; border: none; }
.btn.danger { border-color: #e84a4a; color: #e84a4a; background: #fff3f3; }
.btn-ghost { border: 1px solid #d9deea; background: #fff; color: #1f4b99; padding: 10px 14px; border-radius: 10px; font-weight: 700; cursor: pointer; text-decoration: none; }
.link-grid { display: grid; grid-template-columns: repeat(auto-fit,minmax(200px,1fr)); gap: 10px; }
.center { display: flex; justify-content: center; padding: 30px 0; }
.error { color: #d23b3b; margin: 0; }
</style>
