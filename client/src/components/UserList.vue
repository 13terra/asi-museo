<template>
  <div class="container mt-3">
    <div class="d-flex justify-content-between align-items-center flex-wrap gap-2">
      <h1 class="m-0">Lista de usuarios</h1>
    </div>
    <p class="text-muted mt-2">
      Mostrando {{ users.length }} usuario<span v-if="users.length !== 1">s</span>
    </p>
    <p v-if="users.length === 0" class="fst-italic">Sin usuarios></p>
    <ul class="text-start list-unstyled">
      <li v-for="user in users" :key="user.id">
        Usuario: {{ user.login }} tiene: {{ user.contadorNotas }} nota<span
          v-if="user.contadorNotas !== 1"
          >s</span
        >
      </li>
      <li v-if="users.length === 0" class="fst-italic">Sin usuarios</li>
    </ul>
    <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 row-cols-xxl-4">
      <div class="col mb-3" v-for="user in users" :key="user.id">
        <UserCard :user="user" @usuarioEliminado="loadUsuarios"> </UserCard>
      </div>
    </div>
    <router-link to="/notes">Volver</router-link>
  </div>
</template>
<script>
import UserRepository from "../repositories/UserRepository";
import UserCard from "./UserCard.vue";

export default {
  data() {
    return {
      users: []
    };
  },
  components: { UserCard },
  async mounted() {
    this.loadUsuarios();
  },
  methods: {
    async loadUsuarios() {
      try {
        const lista = await UserRepository.findAll();
        for (const u of lista) {
          u.contadorNotas = "?";
          try {
            const detalle = await UserRepository.findOne(u.id);
            u.contadorNotas = Array.isArray(detalle.notes) ? detalle.notes.length : 0;
          } catch {
            //dejo vacío
          }
        }
        this.users = lista;
      } catch (e) {
        console.log(e);
        alert("No se pudo cargar la lista de usuarios");
      }
    }
  }
};
</script>
