<template>
  <div class="card-box">
    <h1>CAMBIAR CREADOR DE UNA NOTA</h1>
    <form @submit.prevent="cambiarCreador">
      <div class="card-title">ID: {{ noteData.id }}</div>
      <div class="card-title">Titulo: {{ noteData.title }}</div>
      <div class="card-title">Contenido: {{ noteData.content }}</div>
      <div class="card-title">Archivada? {{ noteData.archived ? "Si" : "No" }}</div>
      <div>
        <span>Usuarios: </span>
        <!-- el select DEBE trabajar con IDs y está trabajando con logins -->
        <select v-model="noteData.owner">
          <option v-for="u in usuarios" :value="u.login" :key="u.id">
            {{ u.login }}
          </option>
        </select>
        <button type="submit">Guardar</button>
      </div>
    </form>
    <div class="card-body">
      <router-link to="/notes"> Volver a lista de notas </router-link>
    </div>
  </div>
</template>
<script>
import NoteRepository from "../../repositories/NoteRepository";
import UserRepository from "../../repositories/UserRepository";

let noteId;

export default {
  data() {
    return {
      noteData: {
        categories: []
      },
      usuarios: []
    };
  },
  async mounted() {
    this.usuarios = await UserRepository.findAll();
    noteId = this.$route.params.noteId;
    if (noteId) {
      this.noteData = await NoteRepository.findOne(noteId);
    }
  },
  computed: {
    //Traduce el login seleccionado a su id (sin tocar el template)
    selectOwnerId() {
      const u = this.usuarios.find((u) => u.login === this.noteData.owner);
      return u ? u.id : null;
    }
  },
  methods: {
    async cambiarCreador() {
      try {
        //le meto el body que pide el PUT que es JUSTO (userId: ...)
        await NoteRepository.owner(this.noteData.id, { userId: this.selectOwnerId });
        this.$router.push("/notes");
      } catch (e) {
        console.log(e);
        alert(e?.response?.data?.message || "No se pudo cambiar el creador");
      }
    }
  }
};
</script>
