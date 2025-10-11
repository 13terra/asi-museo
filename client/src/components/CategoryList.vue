<template>
  <div class="card-box">
    <h1 class="card-title">LISTA DE CATEGORIAS</h1>

    <ul class="list-unstyled">
      <li v-for="cat in categories" :key="cat.id">
        <router-link :to="{ name: 'NotesByCategory', params: { categoryId: cat.id } }">
          {{ cat.name }}
        </router-link>
      </li>
    </ul>
    <router-link to="/notes">Volver a la lista de notas</router-link>
  </div>
</template>

<script>
import auth from "../common/auth";
import CategoryRepository from "../repositories/CategoryRepository";

export default {
  data() {
    return {
      categories: []
    };
  },
  //mounted() se ejecuta automáticamente después de que el componente haya sido insertado en el DOM
  async mounted() {
    //si no eres admin vuelves a /notes
    if (!auth.isAdmin()) {
      this.$router.push("/notes");
      return;
    }
    await this.obtenerCategorias();
  },
  methods: {
    async obtenerCategorias() {
      try {
        this.categories = await CategoryRepository.findAll();
      } catch (e) {
        console.log(e);
        alert(e.response?.data?.message || "Error al recuperar las categorias");
      }
    }
  }
};
</script>
