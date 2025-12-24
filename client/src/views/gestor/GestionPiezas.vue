<template>
  <div class="page">
    <header class="head">
      <div>
        <p class="eyebrow">Piezas expuestas</p>
        <h1>Edición #{{ idEdicion }}</h1>
      </div>
      <button class="btn" @click="load" :disabled="loading">Recargar</button>
    </header>

    <section class="card">
      <h3>Añadir pieza</h3>
      <div class="form-grid">
        <label>Obra
          <select v-model.number="form.idObra">
            <option value="">Selecciona obra</option>
            <option v-for="obra in obras" :key="obra.idObra" :value="obra.idObra">{{ obra.titulo }} ({{ obra.autor }})</option>
          </select>
        </label>
        <label>Sala
          <select v-model.number="form.idSala">
            <option value="">Selecciona sala</option>
            <option v-for="s in salas" :key="s.idSala" :value="s.idSala">{{ s.nombre }}</option>
          </select>
        </label>
        <label>Orden<input v-model.number="form.orden" type="number" min="1" /></label>
        <label class="full">Texto curatorial<textarea v-model="form.textoCuratorial" rows="2"></textarea></label>
        <label><input type="checkbox" v-model="form.portada" /> Marcar como portada</label>
        <button class="btn primary" :disabled="saving || !form.idObra || !form.idSala" @click="create">{{ saving ? 'Guardando...' : 'Añadir' }}</button>
      </div>
      <p v-if="error" class="error">{{ error }}</p>
    </section>

    <section class="card">
      <h3>Listado</h3>
      <div v-if="loading" class="center"><div class="spinner-border" role="status"></div></div>
      <div v-else-if="piezas.length === 0" class="empty">No hay piezas expuestas.</div>
      <div v-else class="grid">
        <article v-for="pieza in piezas" :key="pieza.idPiezaExpuesta" class="item">
          <div>
            <p class="eyebrow">#{{ pieza.idPiezaExpuesta }} · Orden {{ pieza.orden }}</p>
            <h4>{{ pieza.obra?.titulo || 'Obra' }}</h4>
            <p class="muted">{{ pieza.obra?.autor }}</p>
            <p class="muted">Sala: {{ pieza.sala?.nombre || pieza.idSala }}</p>
            <p class="body">{{ pieza.textoCuratorial || 'Sin texto curatorial' }}</p>
          </div>
          <div class="item-actions">
            <label><input type="checkbox" v-model="pieza.portada" @change="togglePortada(pieza)" /> Portada</label>
            <button class="btn" @click="startEdit(pieza)">Editar</button>
            <button class="btn danger" @click="remove(pieza.idPiezaExpuesta)">Eliminar</button>
          </div>
        </article>
      </div>
    </section>
  </div>
</template>

<script>
import PiezaExpuestaRepository from '@/repositories/PiezaExpuestaRepository';
import SalaRepository from '@/repositories/SalaRepository';
import ObraRepository from '@/repositories/ObraRepository';

export default {
  name: 'GestionPiezas',
  data() {
    return {
      idEdicion: this.$route.params.idEdicion || this.$route.params.id,
      piezas: [],
      salas: [],
      obras: [],
      form: { idObra: '', idSala: '', orden: 1, textoCuratorial: '', portada: false },
      loading: true,
      saving: false,
      error: ''
    };
  },
  async created() {
    await Promise.all([this.load(), this.loadAux()]);
  },
  methods: {
    async load() {
      this.loading = true; this.error = '';
      try { this.piezas = await PiezaExpuestaRepository.getByEdicion(this.idEdicion); }
      catch (e) { this.error = 'No se pudieron cargar las piezas.'; }
      finally { this.loading = false; }
    },
    async loadAux() {
      try {
        [this.salas, this.obras] = await Promise.all([
          SalaRepository.getAll(),
          ObraRepository.getAll()
        ]);
      } catch (e) { /* ignore */ }
    },
    resetForm() { this.form = { idObra: '', idSala: '', orden: 1, textoCuratorial: '', portada: false }; },
    async create() {
      this.saving = true; this.error = '';
      try {
        await PiezaExpuestaRepository.create(this.idEdicion, { ...this.form });
        this.resetForm();
        await this.load();
      } catch (e) { this.error = 'No se pudo añadir la pieza.'; }
      finally { this.saving = false; }
    },
    startEdit(pieza) {
      this.form = { idObra: pieza.obra?.idObra || pieza.idObra, idSala: pieza.sala?.idSala || pieza.idSala, orden: pieza.orden, textoCuratorial: pieza.textoCuratorial, portada: pieza.portada };
      this.form.idPiezaExpuesta = pieza.idPiezaExpuesta;
    },
    async togglePortada(pieza) {
      try { await PiezaExpuestaRepository.update(pieza.idPiezaExpuesta, { portada: pieza.portada }); }
      catch (e) { alert('No se pudo actualizar portada'); }
    },
    async remove(id) {
      if (!confirm('¿Eliminar esta pieza expuesta?')) return;
      try { await PiezaExpuestaRepository.delete(id); await this.load(); }
      catch (e) { alert('No se pudo eliminar'); }
    }
  }
};
</script>

<style scoped>
.page { max-width: 1200px; margin: 0 auto; padding: 28px 18px 48px; display: flex; flex-direction: column; gap: 16px; }
.head { display: flex; justify-content: space-between; align-items: center; gap: 12px; }
.eyebrow { text-transform: uppercase; letter-spacing: .08em; font-size: 12px; color: #5b6472; margin: 0; }
.muted { color: #5b6472; margin: 0; }
.body { color: #2f3540; margin: 0; }
.btn { padding: 10px 14px; border-radius: 10px; border: 1px solid #d9deea; background: #fff; cursor: pointer; font-weight: 700; }
.btn.primary { background: linear-gradient(135deg,#1f4b99,#153a7a); color: #fff; border: none; }
.btn.danger { border-color: #e84a4a; color: #e84a4a; background: #fff3f3; }
.card { background: #fff; border: 1px solid #e9ecf5; border-radius: 14px; padding: 16px; box-shadow: 0 8px 18px rgba(0,0,0,0.05); display: flex; flex-direction: column; gap: 12px; }
.form-grid { display: grid; grid-template-columns: repeat(auto-fit,minmax(220px,1fr)); gap: 10px; align-items: end; }
select, input, textarea { width: 100%; padding: 10px; border-radius: 10px; border: 1px solid #d9deea; }
.grid { display: grid; grid-template-columns: repeat(auto-fit,minmax(260px,1fr)); gap: 12px; }
.item { border: 1px solid #eef1f6; border-radius: 12px; padding: 12px; display: flex; justify-content: space-between; gap: 10px; }
.item-actions { display: flex; gap: 8px; align-items: center; }
.empty { padding: 12px; background: #f6f8ff; border-radius: 10px; color: #4a5460; }
.center { display: flex; justify-content: center; padding: 20px 0; }
.error { color: #d23b3b; margin: 0; }
</style>
