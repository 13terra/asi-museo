<template>
  <div class="detail-shell" v-if="!loading && expo">
    <div class="detail-header">
      <div class="breadcrumbs">
        <router-link :to="listRoute">Exposiciones</router-link>
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
        <p class="subtitle">{{ expo.descripcion || "Sin descripción" }}</p>
        <div class="meta">
          <div class="pill">Creador: {{ expo.creador?.login || "ADMIN" }}</div>
          <div class="pill">Gestores: {{ expo.gestores?.length || 0 }}</div>
          <div class="pill">Ediciones: {{ ediciones.length }}</div>
        </div>
      </div>
      <div class="hero-actions">
        <router-link class="btn-primary" to="/expos/admin">Volver</router-link>
      </div>
    </div>

    <div class="grid">
      <section class="card-box">
        <header>
          <div>
            <p class="eyebrow">Gestión y permisos</p>
            <h3>Equipo y estados</h3>
          </div>
        </header>
        <div class="stack">
          <div>
            <div class="label">Descripción</div>
            <p class="body">{{ expo.descripcion || "Sin descripción." }}</p>
          </div>
          <div>
            <div class="label">Gestor creador</div>
            <p class="body">{{ expo.creador?.login || "ADMIN" }}</p>
          </div>
          <div>
            <div class="label">Gestores con permisos</div>
            <ul class="list">
              <li v-for="g in expo.gestores || []" :key="g.idUser">{{ g.login }} — {{ g.permiso }}</li>
              <li v-if="!expo.gestores || expo.gestores.length === 0" class="muted">Sin gestores asignados.</li>
            </ul>
          </div>
        </div>
      </section>

      <section class="card-box form-box">
        <header>
          <p class="eyebrow">Edición</p>
          <h3>Editar exposición</h3>
        </header>
        <div class="form-grid">
          <label>
            <span>Título</span>
            <input v-model="form.titulo" type="text" />
          </label>
          <label class="full">
            <span>Descripción</span>
            <textarea v-model="form.descripcion" rows="3"></textarea>
          </label>
          <label class="full">
            <span>Estado</span>
            <select v-model="form.estado">
              <option value="">(sin cambio)</option>
              <option value="BORRADOR">BORRADOR</option>
              <option value="ACTIVA">ACTIVA</option>
              <option value="ARCHIVADA">ARCHIVADA</option>
            </select>
            <small>Sólo CREADOR/ADMIN pueden cambiar estado.</small>
          </label>
        </div>
        <div class="actions">
          <button class="btn-primary" @click="update" :disabled="saving">Guardar</button>
          <router-link class="btn-ghost" to="/expos/admin">Volver</router-link>
        </div>
        <p v-if="saveError" class="error">{{ saveError }}</p>
      </section>
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
            <div class="row-edit">
              <input type="date" v-model="editModel(ed).fechaInicio" />
              <input type="date" v-model="editModel(ed).fechaFin" />
              <select v-model="editModel(ed).estado">
                <option v-for="opt in estadosEdicion" :key="opt" :value="opt">{{ opt }}</option>
              </select>
              <button class="btn-ghost" @click="updateEdicion(ed)" :disabled="edSavingId === ed.idEdicion">Guardar</button>
            </div>
          </div>
          <span class="chip" :class="edBadge(ed.estadoEdicion)">{{ ed.estadoEdicion }}</span>
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
import ExpoRepository from "@/repositories/ExpoRepository";
import EdicionRepository from "@/repositories/EdicionRepository";
import auth from "@/common/auth";

export default {
  name: "ExpoDetailView",
  data() {
    return {
      expo: null,
      loading: true,
      saving: false,
      saveError: "",
      ediciones: [],
      edSaving: false,
      edSavingId: null,
      edError: "",
      edicionEdits: {},
      edicionForm: {
        fechaInicio: "",
        fechaFin: ""
      },
      estadosEdicion: ["BORRADOR", "PUBLICADA", "FINALIZADA", "CANCELADA"],
      form: {
        titulo: "",
        descripcion: "",
        estado: ""
      }
    };
  },
  created() {
    this.load();
  },
  computed: {
    listRoute() {
      return auth.isAdmin() ? "/expos/admin" : "/expos/gestor";
    }
  },
  methods: {
    badgeClass(estado) {
      const map = {
        ACTIVA: "badge-success",
        EN_PREPARACION: "badge-secondary",
        BORRADOR: "badge-secondary",
        ARCHIVADA: "badge-dark"
      };
      return map[estado] || "badge-secondary";
    },
    async load() {
      this.loading = true;
      this.saveError = "";
      try {
        const id = this.$route.params.id;
        this.expo = await ExpoRepository.detailAdmin(id);
        this.form.titulo = this.expo.titulo;
        this.form.descripcion = this.expo.descripcion;
        this.form.estado = "";
        await this.loadEdiciones();
      } catch (e) {
        this.saveError = "No se pudo cargar el detalle";
      } finally {
        this.loading = false;
      }
    },
    async loadEdiciones() {
      try {
        const id = this.$route.params.id;
        this.ediciones = await EdicionRepository.listByExpo(id);
      } catch (e) {
        this.edError = "No se pudieron cargar las ediciones";
      }
    },
    async update() {
      this.saving = true;
      this.saveError = "";
      try {
        const id = this.$route.params.id;
        const payload = {
          titulo: this.form.titulo,
          descripcion: this.form.descripcion,
          estado: this.form.estado || undefined
        };
        await ExpoRepository.update(id, payload);
        await this.load();
      } catch (e) {
        this.saveError = "Error al guardar la exposición";
      } finally {
        this.saving = false;
      }
    },
    async createEdicion() {
      this.edSaving = true;
      this.edError = "";
      if (!this.edicionForm.fechaInicio || !this.edicionForm.fechaFin) {
        this.edError = "Indica fecha de inicio y fin";
        this.edSaving = false;
        return;
      }
      if (this.edicionForm.fechaFin < this.edicionForm.fechaInicio) {
        this.edError = "La fecha fin debe ser posterior a inicio";
        this.edSaving = false;
        return;
      }
      try {
        const id = this.$route.params.id;
        await EdicionRepository.create(id, {
          fechaInicio: this.edicionForm.fechaInicio,
          fechaFin: this.edicionForm.fechaFin
        });
        this.edicionForm.fechaInicio = "";
        this.edicionForm.fechaFin = "";
        await this.loadEdiciones();
      } catch (e) {
        this.edError = "No se pudo crear la edición";
      } finally {
        this.edSaving = false;
      }
    },
    editModel(ed) {
      if (!this.edicionEdits) this.edicionEdits = {};
      if (!this.edicionEdits[ed.idEdicion]) {
        this.edicionEdits[ed.idEdicion] = {
          fechaInicio: ed.fechaInicio,
          fechaFin: ed.fechaFin,
          estado: ed.estadoEdicion
        };
      }
      return this.edicionEdits[ed.idEdicion];
    },
    async updateEdicion(ed) {
      this.edError = "";
      const draft = this.editModel(ed);
      if (!draft.fechaInicio || !draft.fechaFin) {
        this.edError = "Indica fecha de inicio y fin";
        return;
      }
      if (draft.fechaFin < draft.fechaInicio) {
        this.edError = "La fecha fin debe ser posterior a inicio";
        return;
      }
      this.edSavingId = ed.idEdicion;
      try {
        await EdicionRepository.update(ed.idEdicion, {
          fechaInicio: draft.fechaInicio,
          fechaFin: draft.fechaFin,
          estado: draft.estado
        });
        await this.loadEdiciones();
        this.edicionEdits = {};
      } catch (e) {
        this.edError = "No se pudo actualizar la edición";
      } finally {
        this.edSavingId = null;
      }
    },
    edBadge(estado) {
      const map = {
        PUBLICADA: "chip chip-green",
        BORRADOR: "chip chip-gray",
        FINALIZADA: "chip chip-dark",
        CANCELADA: "chip chip-red"
      };
      return map[estado] || "chip";
    }
  }
};
</script>

<style scoped>
.detail-shell {
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px 20px 48px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.breadcrumbs {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #4a5460;
}

.breadcrumbs a {
  color: #1f4b99;
  text-decoration: none;
  font-weight: 700;
}

.status .badge {
  padding: 8px 12px;
  border-radius: 12px;
  font-weight: 700;
}

.hero {
  background: #f7f9fd;
  border: 1px solid #e4e8f2;
  border-radius: 16px;
  padding: 18px 20px;
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
  flex-wrap: wrap;
}

.eyebrow {
  text-transform: uppercase;
  letter-spacing: 0.08em;
  font-size: 12px;
  color: #6c7685;
  margin: 0 0 4px;
}

.hero h1 {
  margin: 0 0 4px;
  font-size: 28px;
}

.subtitle {
  margin: 0 0 10px;
  color: #4a5460;
}

.meta {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.pill {
  background: #eef2ff;
  color: #2f3f82;
  padding: 6px 10px;
  border-radius: 999px;
  font-weight: 700;
  font-size: 12px;
}

.hero-actions {
  display: flex;
  gap: 8px;
}

.btn-primary {
  background: #1f4b99;
  color: #fff;
  border: none;
  border-radius: 10px;
  padding: 10px 14px;
  font-weight: 700;
  cursor: pointer;
  text-decoration: none;
}

.btn-ghost {
  border: 1px solid #d9deea;
  background: transparent;
  border-radius: 10px;
  padding: 10px 14px;
  cursor: pointer;
}

.grid {
  display: grid;
  grid-template-columns: 2fr 1.2fr;
  gap: 14px;
}

@media (max-width: 900px) {
  .grid {
    grid-template-columns: 1fr;
  }
}

.card-box {
  background: #fff;
  border-radius: 14px;
  padding: 16px 18px;
  box-shadow: 0 10px 24px rgba(0, 0, 0, 0.06);
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.card-box header h3 {
  margin: 2px 0 0;
}

.stack {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.label {
  font-weight: 700;
  color: #2a2f36;
  margin-bottom: 4px;
}

.body {
  margin: 0;
  color: #4a5460;
}

.list {
  padding-left: 16px;
  margin: 0;
  color: #2a2f36;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.muted {
  color: #6c7685;
  margin: 0;
}

.form-box .form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 10px;
}

.form-box label {
  display: flex;
  flex-direction: column;
  gap: 6px;
  color: #2a2f36;
  font-weight: 700;
}

.form-box input,
.form-box textarea,
.form-box select {
  border: 1px solid #d9deea;
  border-radius: 10px;
  padding: 10px 12px;
}

.form-box small {
  color: #6c7685;
}

.form-box .full {
  grid-column: 1 / -1;
}

.actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.error {
  color: #e84a4a;
  margin: 0;
}

.editions {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.ed-header {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
}

.ed-actions {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-wrap: wrap;
}

.ed-actions input {
  border: 1px solid #d9deea;
  border-radius: 10px;
  padding: 8px 10px;
}

.row-edit {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-top: 6px;
}

.row-edit input,
.row-edit select {
  border: 1px solid #d9deea;
  border-radius: 10px;
  padding: 8px 10px;
}

.edition-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
  padding: 10px 0;
  border-bottom: 1px solid #eceff5;
}

.chip {
  background: #e8f0ff;
  color: #1f4b99;
  padding: 6px 10px;
  border-radius: 999px;
  font-weight: 700;
}

.chip-green {
  background: #e3f7e9;
  color: #1f7a3d;
}

.chip-gray {
  background: #eef1f6;
  color: #4a5460;
}

.chip-dark {
  background: #dfe2e7;
  color: #2a2f36;
}

.chip-red {
  background: #fff0f0;
  color: #d23b3b;
}

.badge {
  align-self: flex-start;
  padding: 6px 10px;
  border-radius: 10px;
  font-weight: 700;
  font-size: 12px;
}

.badge-success {
  background: #e3f7e9;
  color: #1f7a3d;
}

.badge-secondary {
  background: #eef1f6;
  color: #5b6472;
}

.badge-dark {
  background: #dfe2e7;
  color: #2a2f36;
}

.loading {
  display: flex;
  justify-content: center;
  padding: 48px 0;
}
</style>
