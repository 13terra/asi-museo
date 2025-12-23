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
        <button class="btn-primary" @click="toggleCreate" :disabled="creating">+ Nueva Exposición</button>
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
        <p class="description">{{ expo.descripcion || "Sin descripción" }}</p>
        <div class="pill">Permiso: {{ expo.miPermiso || "-" }}</div>
        <div class="actions">
          <router-link :to="`/expos/${expo.idExposicion}`" class="btn-link">Detalle</router-link>
          <button class="btn-ghost" @click="goEdit(expo)">Editar</button>
        </div>
      </article>
    </div>
  </div>
</template>

<script>
import ExpoRepository from "@/repositories/ExpoRepository";

export default {
  name: "GestorExposView",
  data() {
    return {
      expos: [],
      loading: false,
      error: "",
      incluirArchivadas: false,
      creating: false,
      form: {
        titulo: "",
        descripcion: ""
      }
    };
  },
  created() {
    this.load();
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
    toggleCreate() {
      this.creating = !this.creating;
      if (!this.creating) {
        this.form.titulo = "";
        this.form.descripcion = "";
      }
    },
    async load() {
      this.loading = true;
      this.error = "";
      try {
        this.expos = await ExpoRepository.listGestor({ incluirArchivadas: this.incluirArchivadas });
      } catch (e) {
        this.error = "No se pudieron cargar las exposiciones";
      } finally {
        this.loading = false;
      }
    },
    async create() {
      this.loading = true;
      this.error = "";
      try {
        await ExpoRepository.create({ ...this.form });
        this.toggleCreate();
        await this.load();
      } catch (e) {
        this.error = "Error al crear la exposición";
      } finally {
        this.loading = false;
      }
    },
    goEdit(expo) {
      this.$router.push({ name: "ExpoDetail", params: { id: expo.idExposicion } });
    }
  }
};
</script>

<style scoped>
.panel-shell {
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px 20px 48px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.switch {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
}

.btn-primary {
  background: #1f4b99;
  color: #fff;
  border: none;
  border-radius: 10px;
  padding: 10px 14px;
  font-weight: 700;
  cursor: pointer;
}

.btn-ghost {
  border: 1px solid #d9deea;
  background: transparent;
  border-radius: 10px;
  padding: 8px 12px;
  cursor: pointer;
}

.card-create {
  background: #fff;
  border-radius: 14px;
  padding: 16px;
  box-shadow: 0 8px 22px rgba(0, 0, 0, 0.06);
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 12px;
}

.form-grid .full {
  grid-column: 1 / -1;
}

.form-grid input,
.form-grid textarea {
  width: 100%;
  border-radius: 10px;
  border: 1px solid #d9deea;
  padding: 10px 12px;
}

.actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}

.center {
  display: flex;
  justify-content: center;
  padding: 32px;
}

.cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 14px;
}

.expo-card {
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 8px 22px rgba(0, 0, 0, 0.06);
  padding: 14px 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.expo-top {
  display: flex;
  justify-content: space-between;
  gap: 10px;
}

.description {
  color: #4a5460;
  min-height: 48px;
  margin: 0;
}

.muted {
  color: #6c7685;
  margin: 0;
}

.pill {
  align-self: flex-start;
  background: #eef2ff;
  color: #2f3f82;
  padding: 6px 10px;
  border-radius: 999px;
  font-weight: 700;
  font-size: 12px;
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

.btn-link {
  color: #1f4b99;
  text-decoration: none;
  font-weight: 700;
}

.empty {
  grid-column: 1 / -1;
  text-align: center;
  padding: 20px;
  border: 1px dashed #d9deea;
  border-radius: 12px;
  color: #5b6472;
}
</style>
