<template>
  <div class="container mt-4" style="max-width: 640px">
    <h3 class="mb-3">Nueva Nota</h3>
    <form @submit.prevent="crearNota">
      <!-- .prevent evita que se recargue la página -->

      <!-- TÍTULO -->
      <div class="mb-3">
        <label class="form-label">Título (opcional)</label>
        <input type="text" v-model="title" maxlength="300" class="form-control" />
        <small class="text-muted"> {{ title.length }}/300</small>
      </div>

      <!-- CONTENID0 -->
      <div class="mb-3">
        <label class="form-label">Contenido (opcional)</label>
        <textarea rows="4" v-model="content" class="form-control"> </textarea>
      </div>

      <!--CATEGORIAS EXISTENTES -->
      <div class="mb-3">
        <label class="form-label">
          Categorías (puedes no asociar ninguna, lo habitual es una o ninguna)
        </label>
        <select
          multiple
          size="5"
          v-model="selectedCategories"
          class="form-select"
          :disabled="categories.length === 0"
        >
          <option v-for="cat in categories" :key="cat.id" :value="cat.id">
            {{ cat.name }}
          </option>
        </select>
        <button
          type="button"
          class="mt-3 btn btn-outline-secondary"
          @click="clearCategories"
          :disabled="!selectedCategories.length"
          title="Limpiar selección"
        >
          Limpiar
        </button>
        <small class="text-muted d-block mt-1">
          Pulsa CTRL/CMD a la vez que clicas para seleccionar varias.
        </small>
      </div>

      <!-- VALIDACION -->
      <div class="alert alert-warning py-2" v-if="!isValid">
        Debes indicar al menos título o contenido.
      </div>

      <!-- BOTONES -->
      <div class="d-flex gap-2">
        <button type="submit" class="btn btn-primary" :disabled="!isValid">Guardar</button>
        <router-link to="/notes" class="btn btn-secondary"> Volver a lista de notas </router-link>
      </div>
    </form>
  </div>
</template>

<script>
import NoteRepository from "@/repositories/NoteRepository";
import CategoryRepository from "@/repositories/CategoryRepository";

export default {
  data() {
    //saltaba error de nulos porque al tener title y content como null y llamar a trim() --> ROMPEEE
    return {
      title: "",
      content: "",
      categories: [], // [{id, name}]
      selectedCategories: [] // ids (strings inicialmente)
    };
  },
  computed: {
    // funcion de validacion basica para el input de la nueva nota
    isValid() {
      const correct_title = this.title.trim();
      const correct_content = this.content.trim();
      return (correct_title || correct_content) && correct_title.length <= 300; // NO USAR LOS () DESPUES DE correct_title, ES UNA PROPIEDAD
    }
  },
  async mounted() {
    //cargamos las categorías existentes, solo lectura
    try {
      this.categories = await CategoryRepository.findAll();
    } catch (e) {
      console.error("Error cargando las categorías.", e);
      this.categories = [];
    }
  },
  methods: {
    clearCategories() {
      this.selectedCategories = [];
    },

    async crearNota() {
      if (!this.isValid) return;

      const payload = {}; //aquí formamos el cuerpo de la petición
      const title = this.title.trim();
      const content = this.content.trim();

      if (title) payload.title = title;
      if (content) payload.content = content;
      if (this.selectedCategories.length) {
        payload.categories = this.selectedCategories.map((id) => ({ id })); //metemos en el id las categorias
      }

      try {
        await NoteRepository.create(payload);
        this.$router.push("/notes"); //una vez se ejecuta la creación te lleve a la lista de notas
      } catch (e) {
        console.error(e);
        alert(e.response?.data?.message || "Error al crear la nota");
      }
    }
  }
};
</script>
