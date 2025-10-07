<template>
  <div class="container mt-3">
    <div class="d-flex justify-content-between align-items-center flex-wrap gap-2">
      <h1 class="m-0">Lista de usuarios</h1>
    </div>
    <ul class="text-start list-unstyled">
      <li v-for="u in users" :key="u.id">{{ u.login }} ({{ u.contadorNotas }})</li>
      <li v-if="users.length === 0" class="fst-italic">Sin usuarios</li>
    </ul>
    <router-link to="/notes">Volver</router-link>
  </div>
</template>
<script>
import UserRepository from "../repositories/UserRepository";

export default {
  data() {
    return {
      users: []
    };
  },
  async mounted() {
    try {
      const lista = await UserRepository.findAll();
      for (const user of lista) {
        try {
          const info = await UserRepository.findOne(user.id);
          user.contadorNotas = Array.isArray(info.notes) ? info.notes.length : 0;
        } catch (e) {
          console.log(e);
          alert("ERROR FINDONE NOTAS USERLIST");
        }
      }
      this.users = lista;
    } catch (e) {
      console.log(e);
      alert("No se pudo cargar la lista de usuarios.");
    }
  }
};
</script>
