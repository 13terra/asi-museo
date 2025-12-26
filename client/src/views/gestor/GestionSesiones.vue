<template>
  <div class="page">
    <header class="head">
      <div>
        <p class="eyebrow">Sesiones</p>
        <h1>Edición #{{ idEdicion }}</h1>
      </div>
      <div class="actions">
        <button class="btn" @click="$router.back()">Volver</button>
      </div>
    </header>

    <section class="card">
      <h3>Nueva sesión</h3>
      <div class="form-grid">
        <label>Fecha<input type="date" v-model="form.fecha" /></label>
        <label>Hora inicio<input type="time" v-model="form.horaInicio" /></label>
        <label>Hora fin<input type="time" v-model="form.horaFin" /></label>
        <label>Aforo<input type="number" min="1" max="50" v-model.number="form.aforo" /></label>
        <div class="full sala-picker">
          <span class="label">Salas (selección múltiple)</span>
          <div class="sala-list">
            <label v-for="s in salas" :key="s.idSala" class="sala-item">
              <input type="checkbox" :value="s.idSala" v-model="form.idSalas" />
              {{ s.nombre }} <span class="muted">(Planta {{ s.planta ?? '-' }})</span>
            </label>
          </div>
          <small class="muted">{{ form.idSalas.length }} salas seleccionadas</small>
        </div>
      </div>
      <div class="actions">
        <button class="btn primary" :disabled="saving || !canCreate" @click="create">{{ saving ? 'Creando...' : 'Crear sesión' }}</button>
      </div>
      <p v-if="error" class="error">{{ error }}</p>
    </section>

    <section class="card">
      <h3>Listado</h3>
      <div v-if="loading" class="center"><div class="spinner-border" role="status"></div></div>
      <div v-else-if="sesiones.length === 0" class="empty">No hay sesiones registradas.</div>
      <div class="grid">
        <article v-for="sesion in sesiones" :key="sesion.idSesion" class="item">
          <div v-if="editId !== sesion.idSesion">
            <p class="eyebrow">#{{ sesion.idSesion }} · {{ formatFecha(sesion.fecha) }}</p>
            <h4>{{ formatHora(sesion.horaInicio) }} - {{ formatHora(sesion.horaFin) }}</h4>
            <p class="muted">Salas: {{ salasTexto(sesion.salas) }}</p>
            <p class="pill">Aforo: {{ sesion.aforoOcupado ?? 0 }} / {{ sesion.aforo }}</p>
            <span class="chip" :class="stateClass(sesion.estado)">{{ sesion.estado }}</span>
          </div>
          <div v-else class="edit-grid">
            <label>Fecha<input type="date" v-model="editDraft.fecha" /></label>
            <label>Hora inicio<input type="time" v-model="editDraft.horaInicio" /></label>
            <label>Hora fin<input type="time" v-model="editDraft.horaFin" /></label>
            <label>Aforo<input type="number" min="1" max="50" v-model.number="editDraft.aforo" /></label>
            <div class="full sala-picker">
              <span class="label">Salas</span>
              <div class="sala-list">
                <label v-for="s in salasOrdenadas" :key="s.idSala" class="sala-item">
                  <input type="checkbox" :value="s.idSala" v-model="editDraft.idSalas" />
                  {{ s.nombre }} <span class="muted">(Planta {{ s.planta ?? '-' }})</span>
                </label>
              </div>
            </div>
          </div>
          <div class="item-actions">
            <template v-if="editId === sesion.idSesion">
              <button class="btn" @click="saveEdit(sesion.idSesion)" :disabled="saving">Guardar</button>
              <button class="btn ghost" @click="cancelEdit">Cancelar edición</button>
            </template>
            <template v-else>
              <button class="btn" @click="startEdit(sesion)">Editar</button>
              <button class="btn" @click="cancelar(sesion.idSesion)">Cancelar</button>
              <button class="btn danger" @click="remove(sesion.idSesion)">Eliminar</button>
            </template>
          </div>
        </article>
      </div>
    </section>
  </div>
</template>

<script>
import SesionRepository from '@/repositories/SesionRepository';
import SalaRepository from '@/repositories/SalaRepository';
import { ESTADOS_SESION } from '@/constants';

export default {
  name: 'GestionSesiones',
  data() {
    return {
      idEdicion: this.$route.params.idEdicion || this.$route.params.id,
      sesiones: [],
      salas: [],
      form: { fecha: '', horaInicio: '', horaFin: '', aforo: 0, idSalas: [] },
      loading: true,
      saving: false,
      error: '',
      editId: null,
      editDraft: { fecha: '', horaInicio: '', horaFin: '', aforo: 0, idSalas: [] }
    };
  },
  computed: {
    canCreate() {
      return (
        this.form.fecha &&
        this.form.horaInicio &&
        this.form.horaFin &&
        this.form.aforo > 0 &&
        this.form.aforo <= 50 &&
        this.form.idSalas.length > 0 &&
        this.horasValidas
      );
    },
    horasValidas() {
      if (!this.form.horaInicio || !this.form.horaFin) return false;
      return this.form.horaInicio < this.form.horaFin;
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
  async created() {
    await Promise.all([this.load(), this.loadSalas()]);
  },
  methods: {
    salasTexto(salas = []) {
      return salas.length ? salas.map((s) => s.nombre || s.idSala).join(', ') : 'Sin salas';
    },
    stateClass(estado) {
      return (
        {
          [ESTADOS_SESION.DISPONIBLE]: 'chip-green',
          [ESTADOS_SESION.COMPLETA]: 'chip-dark',
          [ESTADOS_SESION.CANCELADA]: 'chip-red'
        }[estado] || 'chip-gray'
      );
    },
    formatFecha(v) {
      if (!v) return '';
      const d = new Date(v);
      return Number.isNaN(d.getTime()) ? v : d.toLocaleDateString();
    },
    formatHora(v) {
      if (!v) return '';
      const d = new Date(v.includes('T') ? v : `1970-01-01T${v}`);
      return Number.isNaN(d.getTime()) ? v : d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    },
    async load() {
      this.loading = true;
      this.error = '';
      try {
        const sesiones = await SesionRepository.getByEdicion(this.idEdicion);
        this.sesiones = this.mergeSalas(sesiones || []);
      } catch (e) {
        this.error = 'No se pudieron cargar las sesiones.';
      } finally {
        this.loading = false;
      }
    },
    async loadSalas() {
      try {
        this.salas = await SalaRepository.getAll();
        if (this.sesiones.length) {
          this.sesiones = this.mergeSalas(this.sesiones);
        }
      } catch (e) {
        /* ignore */
      }
    },
    async create() {
      if (!this.horasValidas) {
        this.error = 'La hora fin debe ser mayor que la inicial.';
        return;
      }
      if (this.form.aforo <= 0 || this.form.aforo > 50) {
        this.error = 'El aforo debe ser entre 1 y 50.';
        return;
      }
      this.saving = true;
      this.error = '';
      try {
        await SesionRepository.create(this.idEdicion, { ...this.form });
        this.form = { fecha: '', horaInicio: '', horaFin: '', aforo: 0, idSalas: [] };
        await this.load();
      } catch (e) {
        this.error = e.response?.data?.message || 'No se pudo crear la sesión.';
      } finally {
        this.saving = false;
      }
    },
    async cancelar(idSesion) {
      if (!confirm('¿Cancelar esta sesión?')) return;
      try {
        await SesionRepository.cancelar(idSesion);
        await this.load();
      } catch (e) {
        alert('No se pudo cancelar');
      }
    },
    async remove(idSesion) {
      if (!confirm('¿Eliminar esta sesión?')) return;
      try {
        await SesionRepository.delete(idSesion);
        await this.load();
      } catch (e) {
        alert('No se pudo eliminar');
      }
    },
    startEdit(sesion) {
      this.editId = sesion.idSesion;
      this.editDraft = {
        fecha: sesion.fecha || '',
        horaInicio: sesion.horaInicio || '',
        horaFin: sesion.horaFin || '',
        aforo: sesion.aforo || 0,
        idSalas: (sesion.salas || []).map((s) => s.idSala).filter(Boolean)
      };
    },
    cancelEdit() {
      this.editId = null;
      this.editDraft = { fecha: '', horaInicio: '', horaFin: '', aforo: 0, idSalas: [] };
    },
    async saveEdit(idSesion) {
      if (!this.editDraft.fecha || !this.editDraft.horaInicio || !this.editDraft.horaFin) {
        this.error = 'Fecha y horas son obligatorias.';
        return;
      }
      if (this.editDraft.horaInicio >= this.editDraft.horaFin) {
        this.error = 'La hora fin debe ser mayor que la inicial.';
        return;
      }
      if (this.editDraft.aforo <= 0 || this.editDraft.aforo > 50) {
        this.error = 'El aforo debe ser entre 1 y 50.';
        return;
      }
      this.saving = true;
      this.error = '';
      try {
        await SesionRepository.update(idSesion, { ...this.editDraft });
        this.cancelEdit();
        await this.load();
      } catch (e) {
        this.error = e.response?.data?.message || 'No se pudo actualizar la sesión.';
      } finally {
        this.saving = false;
      }
    },
    mergeSalas(sesiones) {
      return sesiones.map((s) => {
        if (!s.salas || !s.salas.length) {
          let ids = s.idSalas || s.salasIds || s.salasIdList || [];
          if (typeof ids === 'string') {
            ids = ids
              .split(',')
              .map((v) => Number(v.trim()))
              .filter(Boolean);
          }
          if (s.idSala) {
            ids = [...ids, s.idSala];
          }
          const mapped = (ids || [])
            .map((id) => this.salas.find((x) => x.idSala === id))
            .filter(Boolean);
          return { ...s, salas: mapped };
        }
        return s;
      });
    }
  }
};
</script>

<style scoped>
.page { max-width: 1100px; margin: 0 auto; padding: 28px 18px 48px; display: flex; flex-direction: column; gap: 16px; }
.head { display: flex; justify-content: space-between; align-items: center; gap: 12px; }
.eyebrow { text-transform: uppercase; letter-spacing: .08em; font-size: 12px; color: #5b6472; margin: 0; }
.muted { color: #5b6472; margin: 0; }
p { margin: 0; }
.pill { background: #eef1f6; padding: 6px 10px; border-radius: 10px; font-weight: 700; width: fit-content; }
.btn { padding: 10px 14px; border-radius: 10px; border: 1px solid #d9deea; background: #fff; cursor: pointer; font-weight: 700; }
.btn.primary { background: linear-gradient(135deg,#1f4b99,#153a7a); color: #fff; border: none; }
.btn.danger { border-color: #e84a4a; color: #e84a4a; background: #fff3f3; }
.card { background: #fff; border: 1px solid #e9ecf5; border-radius: 14px; padding: 16px; box-shadow: 0 8px 18px rgba(0,0,0,0.05); display: flex; flex-direction: column; gap: 12px; }
.form-grid { display: grid; grid-template-columns: repeat(auto-fit,minmax(200px,1fr)); gap: 10px; align-items: end; }
select, input { width: 100%; padding: 10px; border-radius: 10px; border: 1px solid #d9deea; }
.sala-picker { display: flex; flex-direction: column; gap: 8px; }
.sala-list { display: grid; grid-template-columns: repeat(auto-fit,minmax(200px,1fr)); gap: 6px; padding: 6px; border: 1px solid #e3e6ef; border-radius: 10px; background: #f8f9fc; }
.sala-item { display: flex; gap: 6px; align-items: center; font-weight: 600; }
.grid { display: grid; grid-template-columns: repeat(auto-fit,minmax(260px,1fr)); gap: 12px; }
.item { border: 1px solid #eef1f6; border-radius: 12px; padding: 12px; display: flex; justify-content: space-between; gap: 10px; }
.item-actions { display: flex; gap: 8px; align-items: center; flex-wrap: wrap; }
.edit-grid { display: grid; grid-template-columns: repeat(auto-fit,minmax(200px,1fr)); gap: 8px; }
.chip { padding: 6px 10px; border-radius: 999px; font-weight: 700; font-size: 12px; background: #eef1f6; color: #2a2f36; }
.chip-green { background: #e3f7e9; color: #1f7a3d; }
.chip-red { background: #fff0f0; color: #d23b3b; }
.chip-dark { background: #dfe2e7; color: #2a2f36; }
.chip-gray { background: #eef1f6; color: #4a5460; }
.empty { padding: 12px; background: #f6f8ff; border-radius: 10px; color: #4a5460; }
.center { display: flex; justify-content: center; padding: 20px 0; }
.error { color: #d23b3b; margin: 0; }
</style>
