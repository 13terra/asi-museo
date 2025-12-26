<template>
  <div class="page">
    <header class="head">
      <div>
        <p class="eyebrow">Gestor</p>
        <h1>Catálogo de obras</h1>
        <p class="muted">Consulta, crea o edita obras disponibles.</p>
      </div>
    </header>

    <section class="card met-section">
      <div class="section-head">
        <div>
          <h3>🔍 Buscar en The MET</h3>
          <p class="muted">Importa obras del Metropolitan Museum of Art</p>
        </div>
      </div>
      <div class="search-met">
        <input
          v-model="metSearchQuery"
          type="search"
          placeholder="Ej: Van Gogh, Picasso, impressionism..."
          @keyup.enter="buscarEnMET"
        />
        <button class="btn primary" @click="buscarEnMET" :disabled="metLoading || !metSearchQuery">
          {{ metLoading ? "Buscando..." : "Buscar" }}
        </button>
        <button
          class="btn ghost"
          @click="limpiarBusquedaMET"
          :disabled="metLoading || (!metResultados.length && !metSearchQuery)"
        >
          Limpiar
        </button>
      </div>

      <div v-if="metError" class="error">{{ metError }}</div>

      <div v-if="metResultados.length > 0" class="met-results">
        <p class="eyebrow">{{ metResultados.length }} resultados del MET</p>
        <div class="met-grid">
          <article v-for="obra in metResultados" :key="obra.idExterno" class="met-card">
            <div class="met-image">
              <img v-if="obra.imagen" :src="obra.imagen" :alt="obra.titulo" />
              <div v-else class="placeholder">Sin imagen</div>
            </div>
            <div class="met-body">
              <h4>{{ obra.titulo || "Sin título" }}</h4>
              <p class="muted">{{ obra.autor || "Autor desconocido" }}</p>
              <p class="small">ID MET: {{ obra.idExterno }}</p>
              <button class="btn ghost" @click="importarDeMET(obra)">
                <i class="bi bi-download"></i> Importar
              </button>
            </div>
          </article>
        </div>
      </div>
    </section>

    <section class="card">
      <h3>Filtrar obras locales</h3>
      <div class="form-grid">
        <label>Autor<input v-model="filters.autor" @input="load" /></label>
        <label>Técnica<input v-model="filters.tecnica" @input="load" /></label>
        <label
          >Estado
          <select v-model="filters.estado" @change="load">
            <option value="">Todos</option>
            <option value="EN_ALMACEN">EN_ALMACEN</option>
            <option value="EXHIBIDA">EXHIBIDA</option>
            <option value="EN_RESTAURACION">EN_RESTAURACION</option>
          </select>
        </label>
      </div>
    </section>

    <section class="card">
      <div class="section-head">
        <div>
          <p class="eyebrow">Formulario</p>
          <h3>{{ editId ? "Editar obra" : "Nueva obra" }}</h3>
        </div>
        <button v-if="editId" class="btn ghost" @click="reset">Limpiar</button>
      </div>
      <div class="form-grid">
        <label>Título *<input v-model="form.titulo" required /></label>
        <label>Autor *<input v-model="form.autor" required /></label>
        <label
          >Año creación
          <input v-model.number="form.añoCreacion" type="number" min="0" max="2100" />
        </label>
        <label
          >Técnica *
          <input v-model="form.tecnica" required />
        </label>
        <label
          >Dimensiones *
          <input v-model="form.dimensiones" required />
        </label>
        <label
          >Estado
          <select v-model="form.estado">
            <option value="EN_ALMACEN">EN_ALMACEN</option>
            <option value="EXHIBIDA">EXHIBIDA</option>
            <option value="EN_RESTAURACION">EN_RESTAURACION</option>
          </select>
        </label>

        <label
          >Imagen (URL)
          <input
            v-model="form.imagenTexto"
            type="url"
            placeholder="https://..."
            @input="limpiarFichero"
          />
        </label>
        <label
          >O subir archivo (.jpg/.png)
          <input type="file" ref="fileInput" accept="image/*" @change="handleFileUpload" />
        </label>

        <label
          >ID Externo (MET)<input v-model.number="form.idExterno" type="number" readonly
        /></label>
      </div>

      <div class="actions">
        <button class="btn primary" :disabled="saving || !form.titulo" @click="save">
          {{ saving ? "Guardando..." : editId ? "Actualizar" : "Crear" }}
        </button>
        <button class="btn ghost" v-if="editId" @click="reset">Cancelar</button>
      </div>
    </section>

    <section class="card">
      <div class="section-head">
        <div>
          <p class="eyebrow">Obras</p>
          <h3>Listado local</h3>
        </div>
        <small class="muted">{{ obras.length }} resultados</small>
      </div>
      <div v-if="loading" class="center">
        <div class="spinner-border" role="status"></div>
      </div>
      <p v-else-if="error" class="error">{{ error }}</p>
      <div v-else-if="obras.length === 0" class="empty">No hay obras con los filtros actuales.</div>
      <div v-else class="grid">
        <article v-for="obra in obras" :key="obra.idObra" class="item">
          <div class="item-image">
            <img v-if="obra.imagen" :src="obra.imagen" :alt="obra.titulo" />
            <div v-else class="placeholder">Sin imagen</div>
          </div>
          <div class="item-body">
            <p class="eyebrow">Obra #{{ obra.idObra }}</p>
            <h4>{{ obra.titulo }}</h4>
            <p class="muted">
              {{ obra.autor || "Autor desconocido" }} ·
              {{ obra.añoCreacion || obra.anoCreacion || obra.anioCreacion || "-" }}
            </p>
            <p class="muted">{{ obra.tecnica || "Sin técnica" }}</p>
            <p class="pill">Estado: {{ obra.estado || "EN_ALMACEN" }}</p>
            <div class="item-actions">
              <button class="btn" @click="startEdit(obra)">
                <i class="bi bi-pencil"></i> Editar
              </button>
              <button class="btn danger" @click="remove(obra.idObra)">
                <i class="bi bi-trash"></i> Eliminar
              </button>
            </div>
          </div>
        </article>
      </div>
    </section>
  </div>
</template>

<script>
import ObraRepository from "@/repositories/ObraRepository";
import MetRepository from "@/repositories/MetRepository";
import Swal from "sweetalert2";

const DEFAULT_IMAGE = "https://upload.wikimedia.org/wikipedia/commons/1/14/No_Image_Available.jpg";

export default {
  name: "CatalogoObras",
  data() {
    return {
      obras: [],
      loading: false,
      saving: false,
      error: "",
      editId: null,
      filters: { autor: "", tecnica: "", estado: "" },
      form: {
        titulo: "",
        autor: "",
        añoCreacion: null,
        tecnica: "",
        dimensiones: "",
        estado: "EN_ALMACEN",
        idExterno: null,
        // Híbrido:
        imagen: null, // Lo que se enviará (File o URL)
        imagenTexto: "" // Lo que se escribe en el input URL
      },
      metSearchQuery: "",
      metResultados: [],
      metLoading: false,
      metError: ""
    };
  },
  async created() {
    await this.load();
  },
  methods: {
    // --- GESTIÓN DE FICHEROS ---
    handleFileUpload(event) {
      const file = event.target.files[0];
      if (file) {
        this.form.imagen = file; // Guardamos el binario
        this.form.imagenTexto = ""; // Limpiamos la URL visual
      }
    },
    limpiarFichero() {
      // Si escriben URL, esa es la prioridad
      this.form.imagen = this.form.imagenTexto;
      if (this.$refs.fileInput) this.$refs.fileInput.value = "";
    },

    // --- CRUD ---
    reset() {
      this.editId = null;
      this.form = {
        titulo: "",
        autor: "",
        añoCreacion: null,
        tecnica: "",
        dimensiones: "",
        estado: "EN_ALMACEN",
        idExterno: null,
        imagen: null,
        imagenTexto: ""
      };
      if (this.$refs.fileInput) this.$refs.fileInput.value = "";
    },

    async load() {
      this.loading = true;
      try {
        this.obras = await ObraRepository.getAll({ ...this.filters });
      } catch (e) {
        console.error(e);
        this.error = "No se pudo cargar el catálogo.";
      } finally {
        this.loading = false;
      }
    },

    startEdit(obra) {
      this.editId = obra.idObra;
      const imgActual = obra.imagen || "";

      this.form = {
        titulo: obra.titulo || "",
        autor: obra.autor || "",
        // Mapeo robusto del año
        añoCreacion: obra.añoCreacion || obra.anoCreacion || obra.anioCreacion || null,
        tecnica: obra.tecnica || "",
        dimensiones: obra.dimensiones || "",
        estado: obra.estado || "EN_ALMACEN",
        idExterno: obra.idExterno || null,
        imagen: imgActual,
        imagenTexto: imgActual // Si es URL, la mostramos
      };

      if (this.$refs.fileInput) this.$refs.fileInput.value = "";
      window.scrollTo({ top: 300, behavior: "smooth" });
    },

    async save() {
      // 1. VALIDACIÓN ESTRICTA (Coincidiendo con Obra.java)
      if (!this.form.titulo || !this.form.autor || !this.form.tecnica || !this.form.dimensiones) {
        Swal.fire({
          title: "Faltan datos",
          text: "Título, Autor, Técnica y Dimensiones son obligatorios.",
          icon: "warning"
        });
        return;
      }

      if (!this.form.titulo) {
        return Swal.fire("Faltan datos", "El título es obligatorio", "warning");
      }

      // Sincronización final imagen
      if (this.form.imagenTexto && typeof this.form.imagen !== "object") {
        this.form.imagen = this.form.imagenTexto;
      }
      // Imagen por defecto
      if (!this.form.imagen) {
        this.form.imagen = DEFAULT_IMAGE;
      }

      this.saving = true;

      const payload = {
        ...this.form,
        añoCreacion: this.form.añoCreacion ? Number(this.form.añoCreacion) : null
      };

      try {
        if (this.editId) {
          await ObraRepository.update(this.editId, payload);
        } else {
          await ObraRepository.create(payload);
        }

        // SweetAlert de Éxito
        Swal.fire({
          title: "¡Guardado!",
          text: "La obra se ha registrado correctamente.",
          icon: "success",
          timer: 2000,
          showConfirmButton: false
        });

        this.reset();
        await this.load();
      } catch (e) {
        console.error(e);
        let msg = "No se pudo guardar la obra.";

        // Mensaje específico si falla el año (SQL)
        if (JSON.stringify(e).includes("ano_creacion")) {
          msg = "El Año de Creación es obligatorio.";
        } else if (e.response?.data?.message) {
          msg = e.response.data.message;
        }

        Swal.fire({ title: "Error", text: msg, icon: "error" });
      } finally {
        this.saving = false;
      }
    },

    async remove(idObra) {
      // SweetAlert Confirmación (Mucho más bonito que el alert gris)
      const result = await Swal.fire({
        title: "¿Estás seguro?",
        text: "No podrás revertir esta acción.",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#d33",
        cancelButtonColor: "#3085d6",
        confirmButtonText: "Sí, eliminar",
        cancelButtonText: "Cancelar"
      });

      if (!result.isConfirmed) return;

      try {
        await ObraRepository.delete(idObra);

        Swal.fire("Eliminado", "La obra ha sido eliminada.", "success");
        await this.load();
      } catch (e) {
        // Ignoramos 'e' si no lo usamos, pero mostramos alerta
        Swal.fire("Error", "No se pudo eliminar (puede estar en una edición).", "error");
      }
    },

    // --- MET ---
    async buscarEnMET() {
      if (!this.metSearchQuery.trim()) return;
      this.metLoading = true;
      this.metError = "";
      this.metResultados = [];
      try {
        const res = await MetRepository.buscarObras(this.metSearchQuery);
        this.metResultados = res;
        if (res.length === 0) this.metError = "No se encontraron resultados.";
      } catch (e) {
        this.metError = "Error al conectar con The MET.";
      } finally {
        this.metLoading = false;
      }
    },

    async importarDeMET(obraMET) {
      this.metLoading = true;
      try {
        const detalle = await MetRepository.getObraDetalle(obraMET.idExterno);

        let img = detalle.imagen || obraMET.imagen;
        let placeholder = false;
        if (!img) {
          img = DEFAULT_IMAGE;
          placeholder = true;
        }

        this.form = {
          titulo: detalle.titulo || obraMET.titulo || "",
          autor: detalle.autor || obraMET.autor || "",
          añoCreacion: detalle.añoCreacion || detalle.anoCreacion || null,
          tecnica: detalle.tecnica || "",
          dimensiones: detalle.dimensiones || "",
          estado: "EN_ALMACEN",
          imagen: img,
          imagenTexto: img, // Mostramos la URL
          idExterno: obraMET.idExterno
        };

        this.editId = null;
        if (this.$refs.fileInput) this.$refs.fileInput.value = "";
        window.scrollTo({ top: 300, behavior: "smooth" });

        // Feedback con Toasts
        const Toast = Swal.mixin({
          toast: true,
          position: "top-end",
          showConfirmButton: false,
          timer: 3000,
          timerProgressBar: true
        });

        if (placeholder) {
          Toast.fire({ icon: "warning", title: "Importado sin imagen (usando por defecto)." });
        } else {
          Toast.fire({ icon: "success", title: "Datos importados correctamente." });
        }
      } catch (e) {
        Swal.fire("Error", "No se pudieron traer los detalles.", "error");
      } finally {
        this.metLoading = false;
      }
    },

    limpiarBusquedaMET() {
      this.metSearchQuery = "";
      this.metResultados = [];
      this.metError = "";
    }
  }
};
</script>

<style scoped>
/* ESTILOS (Mantenemos los tuyos, son limpios y funcionan) */
.page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 28px 18px 48px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  font-family: "Segoe UI", sans-serif;
}
.head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}
.eyebrow {
  text-transform: uppercase;
  letter-spacing: 0.08em;
  font-size: 12px;
  color: #5b6472;
  margin: 0 0 6px;
  font-weight: 700;
}
h1 {
  margin: 0;
  font-size: 32px;
  font-weight: 800;
  color: #333;
}
.muted {
  color: #5b6472;
  margin: 0;
}
.btn {
  padding: 10px 14px;
  border-radius: 10px;
  border: 1px solid #d9deea;
  background: #fff;
  cursor: pointer;
  font-weight: 700;
  transition: all 0.2s ease;
}
.btn.primary {
  background: linear-gradient(135deg, #1f4b99, #153a7a);
  color: #fff;
  border: none;
}
.btn.primary:hover:not(:disabled) {
  box-shadow: 0 6px 16px rgba(31, 75, 153, 0.3);
  transform: translateY(-2px);
}
.btn.ghost {
  background: #fff;
}
.btn.danger {
  border-color: #e84a4a;
  color: #e84a4a;
  background: #fff3f3;
}
.btn.danger:hover:not(:disabled) {
  background: #e84a4a;
  color: #fff;
}
.btn:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}
.card {
  background: #fff;
  border: 1px solid #e9ecf5;
  border-radius: 14px;
  padding: 20px;
  box-shadow: 0 8px 18px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.met-section {
  background: linear-gradient(135deg, #fff9e6, #fff);
  border-color: #ffd966;
}
.search-met {
  display: flex;
  gap: 12px;
}
.search-met input {
  flex: 1;
  padding: 12px 16px;
  border-radius: 10px;
  border: 1px solid #d9deea;
  font-size: 16px;
}
.met-results {
  margin-top: 12px;
}
.met-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 16px;
  margin-top: 12px;
}
.met-card {
  border: 1px solid #eef1f6;
  border-radius: 12px;
  overflow: hidden;
  background: #fff;
  transition: all 0.2s ease;
}
.met-card:hover {
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
  transform: translateY(-4px);
}
.met-image {
  width: 100%;
  height: 180px;
  background: #f0f2f5;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}
.met-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.placeholder {
  color: #adb5bd;
  font-size: 14px;
}
.met-body {
  padding: 12px;
}
.met-body h4 {
  margin: 0 0 6px;
  font-size: 16px;
  font-weight: 700;
}
.small {
  font-size: 12px;
  color: #868e96;
  margin: 6px 0;
}
.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 12px;
  align-items: end;
}
.form-grid label {
  display: block;
  font-weight: 600;
  margin-bottom: 6px;
}
input,
select,
textarea {
  width: 100%;
  padding: 10px;
  border-radius: 10px;
  border: 1px solid #d9deea;
  font-size: 16px;
}
.actions {
  display: flex;
  gap: 8px;
  align-items: center;
}
.section-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
}
.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}
.item {
  border: 1px solid #eef1f6;
  border-radius: 12px;
  overflow: hidden;
  background: #fff;
}
.item-image {
  width: 100%;
  height: 200px;
  background: #f0f2f5;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}
.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.item-body {
  padding: 12px;
}
.item-body h4 {
  margin: 6px 0;
  font-size: 18px;
}
.item-actions {
  display: flex;
  gap: 8px;
  margin-top: 12px;
  flex-wrap: wrap;
}
.pill {
  display: inline-block;
  padding: 6px 10px;
  border-radius: 999px;
  background: #eef1f6;
  font-weight: 700;
  width: fit-content;
  font-size: 13px;
}
.error {
  color: #d23b3b;
  margin: 0;
  font-weight: 600;
}
.empty {
  padding: 20px;
  background: #f6f8ff;
  border-radius: 10px;
  color: #4a5460;
  text-align: center;
}
.center {
  display: flex;
  justify-content: center;
  padding: 32px 0;
}
.bi {
  margin-right: 4px;
}
</style>
