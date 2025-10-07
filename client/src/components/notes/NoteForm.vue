<template>
  <form @submit.prevent="guardarNota">
    <div>
      <span>Titulo: </span>
      <input type="text" v-model="noteForm.title" maxlength="300" />
    </div>
    <div>
      <span>Contenido: </span>
      <input type="text" v-model="noteForm.content" />
    </div>
    <div>
      <span>Categorias: </span>
      <select v-model="noteForm.categories" multiple>
        <option v-for="cat in categorias" :value="cat" :key="cat.id">
          {{ cat.name }}
        </option>
      </select>
    </div>
    <button type="submit">Guardar</button>
  </form>
</template>
<script>
import CategoryRepository from "@/repositories/CategoryRepository";
import NoteRepository from "@/repositories/NoteRepository";

let noteId;

export default {
  data() {
    return {
      noteForm: {
        categories: []
      },
      categorias: []
    };
  },
  async mounted() {
    this.categorias = await CategoryRepository.findAll();
    noteId = this.$route.params.noteId;
    if (noteId) {
      this.noteForm = await NoteRepository.findOne(noteId); //se asigna el objeto completo a noteForm
    }
  },
  methods: {
    async guardarNota() {
      try {
        if (noteId) {
          await NoteRepository.update(this.noteForm);
          this.$router.push(`/notes/${noteId}`);
        } else {
          const noteBD = await NoteRepository.create(this.noteForm);
          this.$router.push(`/notes/${noteBD.id}`);
        }
      } catch (e) {
        console.log(e);
        alert(e?.response?.data?.message);
      }
    }
  }
};
</script>
