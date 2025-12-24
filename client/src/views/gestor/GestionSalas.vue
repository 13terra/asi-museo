<template>
  <div class="page">
    <header class="head">
      <div>
        <p class="eyebrow">Gestión de salas</p>
        <h1>Salas disponibles</h1>
        <p class="muted">Crea, edita o elimina salas para asignarlas a sesiones.</p>
      </div>
      <button class="btn" @click="load" :disabled="loading">Recargar</button>
    </header>

    <section class="card">
      <h3>{{ editId ? 'Editar sala' : 'Nueva sala' }}</h3>
      <div class="form-grid">
        <label>Nombre<input v-model="form.nombre" /></label>
        <label>Planta<input v-model="form.planta" /></label>
        <div class="actions">
          <button class="btn primary" :disabled="!form.nombre || saving" @click="save">{{ saving ? 'Guardando...' : editId ? 'Actualizar' : 'Crear' }}</button>
          <button class="btn ghost" v-if="editId" @click="reset">Cancelar</button>
        </div>
      </div>
      <p v-if="error" class="error">{{ error }}</p>
    </section>

    <section class="card">
      <h3>Listado</h3>
      <div v-if="loading" class="center"><div class="spinner-border" role="status"></div></div>
      <div v-else-if="salas.length === 0" class="empty">No hay salas registradas.</div>
      <div v-else class="grid">
        <article v-for="sala in salas" :key="sala.idSala" class="item">
          <div>
            <p class="eyebrow">Sala #{{ sala.idSala }}</p>
            <h4>{{ sala.nombre }}</h4>
            <p class="muted">Planta {{ sala.planta || '-' }}</p>
          </div>
          <div class="item-actions">
            <button class="btn" @click="startEdit(sala)">Editar</button>
            <button class="btn danger" @click="remove(sala.idSala)">Eliminar</button>
          </div>
        </article>
      </div>
    </section>
  </div>
</template>

<script>
import SalaRepository from '@/repositories/SalaRepository';

export default {
  name: 'GestionSalas',
  data() {
    return { salas: [], loading: true, saving: false, error: '', editId: null, form: { nombre: '', planta: '' } };
  },
  async created() { await this.load(); },
  methods: {
    reset() { this.editId = null; this.form = { nombre: '', planta: '' }; this.error = ''; },
    async load() {
      this.loading = true; this.error = '';
      try { this.salas = await SalaRepository.getAll(); }
      catch (e) { this.error = 'No se pudieron cargar las salas.'; }
      finally { this.loading = false; }
    },
    async save() {
      if (!this.form.nombre) return;
      this.saving = true; this.error = '';
      try {
        if (this.editId) await SalaRepository.update(this.editId, { ...this.form });
        else await SalaRepository.create({ ...this.form });
        this.reset();
        await this.load();
      } catch (e) { this.error = 'No se pudo guardar la sala.'; }
      finally { this.saving = false; }
    },
    startEdit(sala) { this.editId = sala.idSala; this.form.nombre = sala.nombre; this.form.planta = sala.planta; },
    async remove(idSala) {
      if (!confirm('¿Eliminar esta sala?')) return;
      try { await SalaRepository.delete(idSala); await this.load(); }
      catch (e) { alert('No se pudo eliminar la sala (verifica si tiene sesiones asociadas).'); }
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
.btn.ghost { background: #fff; }
.btn.danger { border-color: #e84a4a; color: #e84a4a; background: #fff3f3; }
.card { background: #fff; border: 1px solid #e9ecf5; border-radius: 14px; padding: 16px; box-shadow: 0 8px 18px rgba(0,0,0,0.05); display: flex; flex-direction: column; gap: 12px; }
.form-grid { display: grid; grid-template-columns: repeat(auto-fit,minmax(240px,1fr)); gap: 12px; align-items: end; }
input { width: 100%; padding: 10px; border-radius: 10px; border: 1px solid #d9deea; }
.actions { display: flex; gap: 8px; align-items: center; }
.grid { display: grid; grid-template-columns: repeat(auto-fit,minmax(260px,1fr)); gap: 12px; }
.item { border: 1px solid #eef1f6; border-radius: 12px; padding: 12px; display: flex; justify-content: space-between; align-items: center; gap: 10px; }
.item-actions { display: flex; gap: 8px; }
.error { color: #d23b3b; margin: 0; }
.empty { padding: 12px; background: #f6f8ff; border-radius: 10px; color: #4a5460; }
.center { display: flex; justify-content: center; padding: 20px 0; }
</style>
