<template>
  <form @submit.prevent="guardarCategoria">
    <div>
      <span>Nombre de la categoria: </span>
      <input type="text" v-model="catForm.name" />
    </div>
    <button type="submit">Guardar</button>
  </form>
  <div>
    <router-link to="/categories"> Volver </router-link>
  </div>
</template>

<script>
import CategoryRepository from "../repositories/CategoryRepository";

let catId;

export default {
  data() {
    return {
      catForm: { name: "" }
    };
  },
  async mounted() {
    catId = this.$route.params.catId;
    if (catId) {
      //si ya existe asigno el objeto completo
      this.catForm = await CategoryRepository.findOne(catId);
    }
  },
  methods: {
    async guardarCategoria() {
      try {
        const name = this.catForm.name?.trim();
        if (!name) {
          alert("El nombre es obligatorio");
          return;
        }
        if (catId) {
          await CategoryRepository.update({ id: catId, name });
        } else {
          await CategoryRepository.create({ name });
        }
        this.$router.push(`/categories`);
      } catch (e) {
        console.log(e);
        alert(e?.response?.data?.message || "No se pudo guardar la categoria");
      }
    }
  }
};
</script>
