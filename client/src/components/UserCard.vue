<template>
  <div class="card h-100" style="width: 18rem">
    <div class="card-header">
      {{ user.login }}
    </div>
    <div class="card-body">
      Notas: {{ user.contadorNotas }}
      <!-- De donde sale contadorNotas -->
    </div>
    <button class="btn btn-sm btn-danger" @click="eliminarUsuario">Eliminar Usuario</button>
  </div>
</template>
<script>
import UserRepository from "../repositories/UserRepository";

export default {
  props: {
    //lo recibe desde el componente padre NoteCard
    user: {
      type: Object, //al declarar user como prop, vue lo registra en el sistema reactivo
      required: true
    }
  },
  emits: ["usuarioEliminado"], //el hijo UserCard muestra y dispara eventos, pero no posee el estado fuente
  methods: {
    async eliminarUsuario() {
      const ok = confirm("Seguro que quiere eliminar el usuario?");
      if (!ok) return;
      try {
        await UserRepository.delete(this.user.id);
        this.$emit("usuarioEliminado", this.user.id);
      } catch (e) {
        console.log(e);
        alert(e?.response?.data?.message || "No se pudo eliminar el usuario.");
      }
    }
  }
};
</script>
