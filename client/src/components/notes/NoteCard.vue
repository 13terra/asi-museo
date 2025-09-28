<template>
  <div class="card h-100" style="width: 18rem">
    <!-- supongo que esto sólo se mostarará si el usuario es admin-->
    <div v-if="isAdmin" class="card-header">
      {{ note.owner }}
    </div>
    <div class="card-body">
      <!--Se muestra el titulo solo si existe -->
      <h5 class="card-title" v-if="note.title && note.title.trim()">
        {{ note.title }}
      </h5>

      <h6 class="card-subtitle mb-2 text-muted" v-if="formattedDate">
        {{ formattedDate }}
      </h6>
      <p class="card-text" v-if="note.content && note.content.trim()">
        {{ note.content }}
      </p>
      <p class="card-text mb-0">
        <router-link :to="{ name: 'DetalleNota', params: { noteId: note.id } }">
          Detalle de la nota
        </router-link>
      </p>
    </div>

    <!-- Aquí aparecerian formateadas las categorias -->
    <div v-if="categoriesAsString" class="card-footer">
      {{ categoriesAsString }}
    </div>
  </div>
</template>

<script>
import auth from "@/common/auth";

//convierte un numero a string y si tiene solo 1 dígito le añade un 0 delante
function pad(n) {
  return n.toString().padStart(2, "0");
}

export default {
  props: {
    note: {
      type: Object,
      required: true
    }
  },
  computed: {
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
