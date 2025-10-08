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
    <button
      class="btn btn-sm"
      @click="toggleActivo"
      :class="user.active ? 'btn-warning' : 'btn-success'"
    >
      {{ user.active ? "Desactivar" : "Activar" }} usuario
    </button>
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
  emits: ["usuarioEliminado", "usuarioActualizado"], //el hijo UserCard muestra y dispara eventos, pero no posee el estado fuente
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
    },
    async toggleActivo() {
      const accion = this.user.active ? "desactivar" : "activar";
      const ok = confirm(`Seguro que quieres ${accion} el usuario?`);
      if (!ok) return;
      try {
        if (this.user.active) {
          await UserRepository.deactivate(this.user.id);
        } else {
          await UserRepository.activate(this.user.id);
        }
        this.$emit("usuarioActualizado");
      } catch (e) {
        console.log(e);
        alert(e?.response?.data?.message || `No se puedo ${accion} el usuario.`);
      }
    }
  }
};
</script>
