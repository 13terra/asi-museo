<template>
  <div class="card h-100" style="width: 18rem">
    <!-- supongo que esto sólo se mostarará si el usuario es admin-->
    <div v-if="isAdmin" class="card-header">
      <router-link :to="{ name: 'ListByUser', params: { login: note.owner } }">
        {{ note.owner }}
      </router-link>
    </div>
    <div v-if="isAdmin" class="card-body">
      <strong>
        {{ note.archived ? "Archivada" : "Desarchivada" }}
      </strong>
    </div>
    <div class="card-body">
      <!--Se muestra el titulo solo si existe -->
      <h5 class="card-title" v-if="note.title && note.title.trim()">
        {{ note.title }}
      </h5>
      <!-- link al detalle de la nota -->
      <h6 class="card-subtitle mb-2 text-muted" v-if="formattedDate">
        <router-link :to="{ name: 'DetalleNota', params: { noteId: note.id } }">
          {{ formattedDate }}
        </router-link>
      </h6>
      <p class="card-text" v-if="note.content && note.content.trim()">
        {{ note.content }}
      </p>
    </div>
    <!-- Boton ARCHIVAR/DESARCHIVAR -->
    <div class="card-body">
      <button v-if="!isAdmin" class="btn btn-warning" @click="archivarDesarchivar">
        {{ note.archived ? "Desarchivar" : "Archivar" }}
      </button>
    </div>
    <!-- Boton EDITAR -->
    <div v-if="!isAdmin">
      <router-link :to="{ name: 'EditarNota', params: { noteId: note.id } }">
        <!-- QUE HACE CUSTOM -->
        <button class="btn btn-primary btn-sm">Editar Nota</button>
      </router-link>
    </div>
    <!-- Boton ELIMINAR -->
    <div v-if="!isAdmin">
      <button class="btn btn-danger btn-sm" @click="borrarNota">Eliminar Nota</button>
    </div>
    <!-- Boton CAMBIAR/CREADOR -->
    <div class="card-body" v-if="isAdmin">
      <router-link :to="{ name: 'CambiarCreador', params: { noteId: note.id } }">
        Cambiar creador de la nota?
      </router-link>
    </div>

    <!-- Aquí aparecerian formateadas las categorias -->
    <div v-if="Array.isArray(note.categories) && note.categories.length" class="card-footer">
      <span v-for="(cat, i) in note.categories" :key="cat.id">
        <router-link :to="{ name: 'NotesByCategory', params: { categoryId: cat.id } }">
          {{ cat.name }} </router-link
        ><span v-if="i < note.categories.length - 1">, </span>
      </span>
    </div>
  </div>
</template>

<script>
import auth from "@/common/auth";
import NoteRepository from "@/repositories/NoteRepository";
//convierte un numero a string y si tiene solo 1 dígito le añade un 0 delante
function pad(n) {
  return n.toString().padStart(2, "0");
}

export default {
  props: {
    //las props son de solo lectura en el hijo
    note: {
      type: Object,
      required: true
    }
  },
  emits: ["cambioArchivado", "notaEliminada"], //el hijo muestra y dispara eventos, pero NO POSEE el estado fuente
  methods: {
    async archivarDesarchivar() {
      try {
        //no mutar props ni llamar al repo aquí
        this.$emit("cambioArchivado", this.note);
      } catch (e) {
        console.error(e);
        alert(e.response?.data?.message || "Error al archivar/desarchivar la nota");
      }
    },
    async borrarNota() {
      const ok = confirm("¿Eliminar la nota?");
      if (!ok) return;
      try {
        await NoteRepository.delete(this.note.id);
        this.$emit("notaEliminada", this.note.id);
      } catch (e) {
        console.log(e);
        alert(e?.response?.data?.message || "No se pudo eliminar la nota");
      }
    }
  },
  computed: {
    //se calculan cuando cambie user en el padre
    categoriesAsString() {
      return this.note?.categories.map((t) => t.name).join(", ");
    },
    formattedDate() {
      if (!this.note?.timestamp) return "";
      const date = this.note.timestamp; // ya es date
      const dd = pad(date.getDate());
      const mm = pad(date.getMonth() + 1); //getMonth devuelve los meses base (enero:0, febrero:1...)
      const YYYY = date.getFullYear();
      const hh = pad(date.getHours());
      const min = pad(date.getMinutes());
      return `${dd}/${mm}/${YYYY}, a las ${hh}:${min}`;
    },
    //lo hago ya en vista a futuro
    // no tiene sentido que a un usuario le salga su propio nombre en cada nota
    isAdmin() {
      return auth.isAdmin();
    }
  }
};
</script>
