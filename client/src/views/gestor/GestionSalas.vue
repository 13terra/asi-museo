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
        <label>Planta
          <select v-model="form.planta" @change="onPlantaChange">
            <option value="">Selecciona planta</option>
            <option v-for="p in plantas" :key="p" :value="p">Planta {{ p }}</option>
            <option value="__custom">Otra (introducir)</option>
          </select>
        </label>
        <label v-if="form.planta === '__custom'">Planta personalizada
          <input type="number" v-model.number="form.plantaCustom" min="-10" max="50" placeholder="Ej: 0" />
        </label>
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
        <article v-for="sala in salasOrdenadas" :key="sala.idSala" class="item">
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
    return { salas: [], loading: true, saving: false, error: '', editId: null, form: { nombre: '', planta: '', plantaCustom: '' } };
  },
  computed: {
    plantas() {
      const uniques = new Set(this.salas.filter(s => s.planta !== null && s.planta !== undefined && s.planta !== '').map(s => s.planta));
      return Array.from(uniques).sort((a, b) => Number(a) - Number(b));
    },
    salasOrdenadas() {
      return [...this.salas].sort((a, b) => {
        const pa = Number(a.planta ?? 0);
        const pb = Number(b.planta ?? 0);
        if (pa !== pb) return pa - pb;
        return (a.nombre || '').localeCompare(b.nombre || '');
      });
    }
  },
  async created() { await this.load(); },
  methods: {
    reset() { this.editId = null; this.form = { nombre: '', planta: '', plantaCustom: '' }; this.error = ''; },
    onPlantaChange() { if (this.form.planta !== '__custom') this.form.plantaCustom = ''; },
    async load() {
      this.loading = true; this.error = '';
      try { this.salas = await SalaRepository.getAll(); }
      catch (e) { this.error = 'No se pudieron cargar las salas.'; }
      finally { this.loading = false; }
    },
    async save() {
      if (!this.form.nombre) return;
      const plantaSeleccionada = this.form.planta === '__custom' ? this.form.plantaCustom : this.form.planta;
      if (plantaSeleccionada === '' || plantaSeleccionada === null || plantaSeleccionada === undefined) {
        this.error = 'Hay que añadir planta';
        return;
      }
      if (Number(plantaSeleccionada) < -10 || Number(plantaSeleccionada) > 50) {
        this.error = 'La planta debe estar entre -10 y 50';
        return;
      }
      this.error = '';
      this.saving = true; this.error = '';
      try {
        const payload = { nombre: this.form.nombre, planta: plantaSeleccionada };
        if (this.editId) await SalaRepository.update(this.editId, payload);
        else await SalaRepository.create(payload);
        this.reset();
        await this.load();
      } catch (e) { this.error = e.response?.data?.message || 'No se pudo guardar la sala.'; }
      finally { this.saving = false; }
    },
    startEdit(sala) {
      this.editId = sala.idSala;
      this.form.nombre = sala.nombre;
      this.form.planta = sala.planta ?? '';
      this.form.plantaCustom = '';
    },
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
