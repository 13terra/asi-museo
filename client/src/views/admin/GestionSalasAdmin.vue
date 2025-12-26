<template>
  <div class="page-container">
    <header class="page-header">
      <div class="header-content">
        <h1 class="page-title"><i class="bi bi-grid-3x3-gap-fill"></i> Gestión de Salas</h1>
        <p class="page-subtitle">Administra el inventario de espacios del museo.</p>
      </div>
      <button class="btn-back" @click="$router.back()">
        <i class="bi bi-arrow-left"></i> Volver
      </button>
    </header>

    <div class="content-layout">
      <aside class="form-panel">
        <div class="panel-header">
          <h3>{{ editId ? "Editar Sala" : "Nueva Sala" }}</h3>
          <button v-if="editId" class="btn-text danger" @click="reset">Cancelar</button>
        </div>

        <div class="form-body">
          <div class="form-group">
            <label for="nombreSala">Nombre de la Sala</label>
            <input
              id="nombreSala"
              v-model="form.nombre"
              type="text"
              placeholder="Ej: Sala Goya..."
              class="form-input"
            />
          </div>

          <div class="form-group">
            <label for="plantaSala">Planta</label>
            <select id="plantaSala" v-model="form.planta" class="form-select">
              <option value="" disabled selected>Selecciona una planta</option>
              <option v-for="p in plantasBase" :key="p" :value="p">Planta {{ p }}</option>
            </select>
          </div>

          <div v-if="error" class="alert-error">
            <i class="bi bi-exclamation-triangle-fill"></i> {{ error }}
          </div>

          <button
            class="btn-submit"
            :disabled="!form.nombre || !form.planta || saving"
            @click="save"
          >
            <i v-if="saving" class="spinner-border spinner-border-sm me-2"></i>
            {{ saving ? "Guardando..." : editId ? "Actualizar Sala" : "Crear Sala" }}
          </button>
        </div>
      </aside>

      <main class="list-panel">
        <div class="list-header">
          <h3>Listado de Salas ({{ salas.length }})</h3>
          <div class="sort-controls">
            <label for="ordenarPor">Ordenar por:</label>
            <select id="ordenarPor" v-model="orden" @change="ordenar" class="sort-select">
              <option value="planta">Planta</option>
              <option value="nombre">Nombre</option>
              <option value="id">Recientes</option>
            </select>
          </div>
        </div>

        <div v-if="loading" class="loading-state">
          <div class="spinner-border text-primary" role="status"></div>
          <p>Cargando salas...</p>
        </div>

        <div v-else-if="salas.length === 0" class="empty-state">
          <i class="bi bi-inbox-fill"></i>
          <p>No hay salas registradas aún.</p>
          <small>Usa el formulario para crear la primera.</small>
        </div>

        <div v-else class="salas-grid">
          <article
            v-for="sala in salasOrdenadas"
            :key="sala.idSala"
            class="sala-card"
            :class="{ editing: sala.idSala === editId }"
          >
            <div class="card-header">
              <h4 class="sala-name">{{ sala.nombre }}</h4>
              <span class="sala-id">#{{ sala.idSala }}</span>
            </div>

            <div class="card-body">
              <span class="planta-badge">
                <i class="bi bi-layers-fill me-1"></i> Planta
                {{ sala.planta !== null ? sala.planta : "-" }}
              </span>
            </div>

            <div class="card-actions">
              <button class="btn-icon edit" @click="startEdit(sala)" title="Editar">
                <i class="bi bi-pencil-square"></i> Editar
              </button>
              <button class="btn-icon delete" @click="remove(sala.idSala)" title="Eliminar">
                <i class="bi bi-trash-fill"></i> Eliminar
              </button>
            </div>
          </article>
        </div>
      </main>
    </div>
  </div>
</template>

<script>
// EL SCRIPT SE MANTIENE IDÉNTICO A TU VERSIÓN ORIGINAL
// ... (Copia y pega el bloque <script> completo que me enviaste)
import SalaRepository from "@/repositories/SalaRepository";

export default {
  name: "GestionSalasAdmin",
  data() {
    return {
      salas: [],
      loading: true,
      saving: false,
      error: "",
      editId: null,
      form: { nombre: "", planta: "" },
      orden: "planta",
      plantasBase: ["0", "1", "2", "3"]
    };
  },
  computed: {
    salasOrdenadas() {
      const base = [...this.salas];
      if (this.orden === "nombre")
        return base.sort((a, b) => (a.nombre || "").localeCompare(b.nombre || ""));
      if (this.orden === "id") return base.sort((a, b) => (a.idSala || 0) - (b.idSala || 0));
      return base.sort((a, b) => {
        const pa = Number(a.planta ?? 0);
        const pb = Number(b.planta ?? 0);
        if (pa !== pb) return pa - pb;
        return (a.nombre || "").localeCompare(b.nombre || "");
      });
    }
  },
  async created() {
    await this.load();
  },
  methods: {
    reset() {
      this.editId = null;
      this.form = { nombre: "", planta: "" };
      this.error = "";
    },
    ordenar() {
      /* computed handles */
    },
    async load() {
      this.loading = true;
      this.error = "";
      try {
        this.salas = await SalaRepository.getAll();
      } catch (e) {
        this.error = "No se pudieron cargar las salas.";
      } finally {
        this.loading = false;
      }
    },
    async save() {
      if (!this.form.nombre) return;
      const plantaSeleccionada = this.form.planta;
      if (
        plantaSeleccionada === "" ||
        plantaSeleccionada === null ||
        plantaSeleccionada === undefined
      ) {
        this.error = "Hay que añadir planta";
        return;
      }
      if (!this.plantasBase.includes(String(plantaSeleccionada))) {
        this.error = "Solo plantas 0,1,2 y 3";
        return;
      }
      this.error = "";

      this.saving = true;
      this.error = "";
      try {
        const payload = { nombre: this.form.nombre, planta: plantaSeleccionada };
        if (this.editId) await SalaRepository.update(this.editId, payload);
        else await SalaRepository.create(payload);
        this.reset();
        await this.load();
      } catch (e) {
        this.error = e.response?.data?.message || "No se pudo guardar la sala.";
      } finally {
        this.saving = false;
      }
    },
    startEdit(sala) {
      this.editId = sala.idSala;
      this.form.nombre = sala.nombre;
      this.form.planta = String(sala.planta ?? "");
    },
    async remove(idSala) {
      if (!confirm("¿Eliminar esta sala?")) return;
      try {
        await SalaRepository.delete(idSala);
        await this.load();
      } catch (e) {
        alert("No se pudo eliminar la sala (verifica si tiene sesiones asociadas).");
      }
    }
  }
};
</script>

<style scoped>
/* ESTILOS MODERNOS Y LIMPIOS */
.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px 24px;
  font-family: "Segoe UI", "Roboto", sans-serif;
  color: #333;
}

/* Header */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
  border-bottom: 1px solid #eee;
  padding-bottom: 20px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #1f4b99;
  margin: 0 0 8px 0;
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-subtitle {
  margin: 0;
  color: #666;
}

.btn-back {
  background: none;
  border: 1px solid #ccc;
  padding: 8px 16px;
  border-radius: 8px;
  color: #555;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.2s;
}
.btn-back:hover {
  background: #f5f5f5;
  border-color: #bbb;
}

/* Layout Principal */
.content-layout {
  display: grid;
  grid-template-columns: 350px 1fr; /* Panel izquierdo fijo, derecho flexible */
  gap: 32px;
  align-items: start;
}

/* Panel Formulario */
.form-panel {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border: 1px solid #e0e0e0;
  overflow: hidden;
  position: sticky;
  top: 32px; /* Se mantiene visible al hacer scroll */
}

.panel-header {
  background: #f8f9fa;
  padding: 16px 20px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.panel-header h3 {
  margin: 0;
  font-size: 18px;
  color: #1f4b99;
}

.form-body {
  padding: 24px 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.form-group label {
  font-weight: 600;
  font-size: 14px;
  color: #444;
}
.form-input,
.form-select {
  padding: 10px 12px;
  border: 1px solid #ccc;
  border-radius: 8px;
  font-size: 15px;
  transition: border-color 0.2s;
}
.form-input:focus,
.form-select:focus {
  border-color: #1f4b99;
  outline: none;
}

.btn-submit {
  background: #1f4b99;
  color: white;
  border: none;
  padding: 12px;
  border-radius: 8px;
  font-weight: 700;
  cursor: pointer;
  transition: background 0.2s;
  display: flex;
  justify-content: center;
  align-items: center;
}
.btn-submit:hover:not(:disabled) {
  background: #163a7a;
}
.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-text.danger {
  background: none;
  border: none;
  color: #d32f2f;
  font-weight: 600;
  cursor: pointer;
}
.alert-error {
  background: #feebee;
  color: #c62828;
  padding: 12px;
  border-radius: 8px;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
}

/* Panel Listado */
.list-panel {
  flex: 1;
}
.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.list-header h3 {
  margin: 0;
  font-size: 20px;
  color: #333;
}

.sort-controls {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  color: #666;
}
.sort-select {
  padding: 6px 10px;
  border-radius: 6px;
  border: 1px solid #ccc;
}

/* Grid de Salas */
.salas-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.sala-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid #eee;
  transition: all 0.2s ease;
  display: flex;
  flex-direction: column;
}
.sala-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
  border-color: #1f4b99;
}
.sala-card.editing {
  border: 2px solid #1f4b99;
  background: #f0f7ff;
}

.card-header {
  padding: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #f0f0f0;
}
.sala-name {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: #333;
}
.sala-id {
  font-size: 13px;
  color: #888;
  font-family: monospace;
}

.card-body {
  padding: 16px;
  flex-grow: 1;
}
.planta-badge {
  background: #e3f2fd;
  color: #1565c0;
  padding: 6px 12px;
  border-radius: 20px;
  font-weight: 600;
  font-size: 14px;
  display: inline-flex;
  align-items: center;
}

.card-actions {
  padding: 12px 16px;
  background: #fcfcfc;
  border-top: 1px solid #eee;
  display: flex;
  gap: 10px;
}

.btn-icon {
  flex: 1;
  padding: 8px;
  border-radius: 6px;
  border: 1px solid #ddd;
  background: #fff;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.2s;
}
.btn-icon.edit:hover {
  background: #e3f2fd;
  color: #1565c0;
  border-color: #1565c0;
}
.btn-icon.delete:hover {
  background: #feebee;
  color: #c62828;
  border-color: #c62828;
}

/* Estados */
.loading-state,
.empty-state {
  text-align: center;
  padding: 60px 0;
  color: #777;
}
.empty-state i {
  font-size: 48px;
  margin-bottom: 16px;
  color: #ccc;
}
.empty-state p {
  font-size: 18px;
  margin: 0;
  font-weight: 600;
}

/* Responsividad */
@media (max-width: 768px) {
  .content-layout {
    grid-template-columns: 1fr;
  } /* Formulario arriba en móviles */
  .form-panel {
    position: static;
  }
}
</style>
