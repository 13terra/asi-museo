<template>
  <div class="page">
    <header class="head">
      <div>
        <p class="eyebrow">Gestor</p>
        <h1>Catálogo de obras</h1>
        <p class="muted">Consulta, crea o edita obras disponibles para las ediciones.</p>
      </div>
      <button class="btn" @click="load" :disabled="loading">Recargar</button>
    </header>

    <section class="card">
      <h3>Filtrar</h3>
      <div class="form-grid">
        <label>Autor<input v-model="filters.autor" @change="load" /></label>
        <label>Técnica<input v-model="filters.tecnica" @change="load" /></label>
        <label>Estado
          <select v-model="filters.estado" @change="load">
            <option value="">Todos</option>
            <option value="ACTIVA">ACTIVA</option>
            <option value="INACTIVA">INACTIVA</option>
          </select>
        </label>
      </div>
    </section>

    <section class="card">
      <div class="section-head">
        <div>
          <p class="eyebrow">Formulario</p>
          <h3>{{ editId ? 'Editar obra' : 'Nueva obra' }}</h3>
        </div>
        <button v-if="editId" class="btn ghost" @click="reset">Limpiar</button>
      </div>
      <div class="form-grid">
        <label>Título<input v-model="form.titulo" /></label>
        <label>Autor<input v-model="form.autor" /></label>
        <label>Año creación<input v-model="form.añoCreacion" type="number" min="0" /></label>
        <label>Técnica<input v-model="form.tecnica" /></label>
        <label>Dimensiones<input v-model="form.dimensiones" /></label>
        <label>Estado
          <select v-model="form.estado">
            <option value="ACTIVA">ACTIVA</option>
            <option value="INACTIVA">INACTIVA</option>
          </select>
        </label>
        <label>ID externo<input v-model="form.idExterno" /></label>
        <div class="actions">
          <button class="btn primary" :disabled="saving || !form.titulo" @click="save">{{ saving ? 'Guardando...' : editId ? 'Actualizar' : 'Crear' }}</button>
          <button class="btn ghost" v-if="editId" @click="reset">Cancelar</button>
        </div>
      </div>
      <p v-if="error" class="error">{{ error }}</p>
    </section>

    <section class="card">
      <div class="section-head">
        <div>
          <p class="eyebrow">Obras</p>
          <h3>Listado</h3>
        </div>
        <small class="muted">{{ obras.length }} resultados</small>
      </div>
      <div v-if="loading" class="center"><div class="spinner-border" role="status"></div></div>
      <p v-else-if="error" class="error">{{ error }}</p>
      <div v-else-if="obras.length === 0" class="empty">No hay obras con los filtros actuales.</div>
      <div v-else class="grid">
        <article v-for="obra in obras" :key="obra.idObra" class="item">
          <div>
            <p class="eyebrow">Obra #{{ obra.idObra }}</p>
            <h4>{{ obra.titulo }}</h4>
            <p class="muted">{{ obra.autor || 'Autor desconocido' }} · {{ obra.añoCreacion || '-' }}</p>
            <p class="muted">{{ obra.tecnica || 'Sin técnica' }}</p>
            <p class="pill">Estado: {{ obra.estado || 'ACTIVA' }}</p>
          </div>
          <div class="item-actions">
            <button class="btn" @click="startEdit(obra)">Editar</button>
            <button class="btn danger" @click="remove(obra.idObra)">Eliminar</button>
          </div>
        </article>
      </div>
    </section>
  </div>
</template>

<script>
import ObraRepository from '@/repositories/ObraRepository';

export default {
  name: 'CatalogoObras',
  data() {
    return {
      obras: [],
      loading: true,
      saving: false,
      error: '',
      editId: null,
      filters: { autor: '', tecnica: '', estado: '' },
      form: { titulo: '', autor: '', añoCreacion: '', tecnica: '', dimensiones: '', estado: 'ACTIVA', idExterno: '' }
    };
  },
  async created() { await this.load(); },
  methods: {
    reset() { this.editId = null; this.form = { titulo: '', autor: '', añoCreacion: '', tecnica: '', dimensiones: '', estado: 'ACTIVA', idExterno: '' }; this.error = ''; },
    async load() {
      this.loading = true; this.error = '';
      try { this.obras = await ObraRepository.getAll({ ...this.filters }); }
      catch (e) { this.error = 'No se pudo cargar el catálogo de obras.'; }
      finally { this.loading = false; }
    },
    startEdit(obra) {
      this.editId = obra.idObra;
      this.form = {
        titulo: obra.titulo || '',
        autor: obra.autor || '',
        añoCreacion: obra.añoCreacion || '',
        tecnica: obra.tecnica || '',
        dimensiones: obra.dimensiones || '',
        estado: obra.estado || 'ACTIVA',
        idExterno: obra.idExterno || ''
      };
    },
    async save() {
      if (!this.form.titulo) return;
      this.saving = true; this.error = '';
      const payload = { ...this.form };
      try {
        if (this.editId) await ObraRepository.update(this.editId, payload);
        else await ObraRepository.create(payload);
        this.reset();
        await this.load();
      } catch (e) { this.error = 'No se pudo guardar la obra.'; }
      finally { this.saving = false; }
    },
    async remove(idObra) {
      if (!confirm('¿Eliminar esta obra?')) return;
      try { await ObraRepository.delete(idObra); await this.load(); }
      catch (e) { alert('No se pudo eliminar la obra (verifica si está asignada a ediciones).'); }
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
.form-grid { display: grid; grid-template-columns: repeat(auto-fit,minmax(220px,1fr)); gap: 12px; align-items: end; }
input, select { width: 100%; padding: 10px; border-radius: 10px; border: 1px solid #d9deea; }
.actions { display: flex; gap: 8px; align-items: center; }
.section-head { display: flex; justify-content: space-between; align-items: center; gap: 10px; }
.grid { display: grid; grid-template-columns: repeat(auto-fit,minmax(260px,1fr)); gap: 12px; }
.item { border: 1px solid #eef1f6; border-radius: 12px; padding: 12px; display: flex; justify-content: space-between; align-items: flex-start; gap: 10px; }
.item-actions { display: flex; gap: 8px; flex-wrap: wrap; }
pill { display: inline-block; }
.pill { padding: 6px 10px; border-radius: 999px; background: #eef1f6; font-weight: 700; width: fit-content; }
.error { color: #d23b3b; margin: 0; }
.empty { padding: 12px; background: #f6f8ff; border-radius: 10px; color: #4a5460; }
.center { display: flex; justify-content: center; padding: 20px 0; }
</style>
