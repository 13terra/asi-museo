<template>
  <div class="page">
    <header class="head">
      <div>
        <p class="eyebrow">Administración</p>
        <h1>Gestión de salas</h1>
        <p class="muted">Inventario global de salas disponibles.</p>
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
  name: 'GestionSalasAdmin',
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
.gestion-container {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.page-title {
  font-size: 1.8rem;
  font-weight: 800;
  color: #2c3e50;
  margin: 0;
}

/* --- SOLUCIÓN DEL SOLAPAMIENTO --- */
.salas-grid {
  display: grid;
  /* Crea columnas automáticas de mínimo 300px de ancho */
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px; /* Espacio entre tarjetas */
  margin-top: 24px;
}

.sala-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  /* Sombra suave */
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  transition: transform 0.2s, box-shadow 0.2s;
  border: 1px solid rgba(0,0,0,0.05);
}

.sala-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.sala-info h3 {
  margin: 0 0 8px;
  font-size: 1.4rem;
  color: #2c3e50;
}

.sala-info p {
  margin: 0;
  color: #6c757d;
  font-weight: 500;
}

.card-actions {
  margin-top: 24px;
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  border-top: 1px solid #eee;
  padding-top: 16px;
}

/* Botones */
.btn-primary {
  background: #1f4b99;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
}

.btn-primary:hover {
  background: #163a7a;
}

.btn-edit {
  background: #eef2f7;
  color: #1f4b99;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
}

.btn-delete {
  background: #feebee;
  color: #b3261e;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
}
</style>