<template>
  <div v-if="note != null" class="container mt-4">
    <!-- ERROR DE LOS TÍPICOS MUY IMPORTANTE ENTENDERLO, SI LA NOTA NO ES NULA ENTONCES...-->
    <div class="card">
      <div>
        <router-link :to="'notes/' + note.id + '/edit'" custom>
          <button class="btn btn-primary">EDITAR</button>
        </router-link>
      </div>

      <!--Cabecera con propietario y fecha -->
      <div class="card-header d-flex justify-content-between">
        <h3 v-if="isAdmin">
          {{ note.owner }}
        </h3>
        <small>
          <!--ACORDARSE QUE LAS LLAMADAS A LAS FUNCIONES ACABAN EN ()-->
          {{ formattedDate }}
        </small>
      </div>
      <!--Cuerpo de la nota -->
      <div class="card-body">
        <h5 class="card-title">{{ note.title }}</h5>
        <p class="card-text">{{ note.content }}</p>
        <!-- El contenido de la nota es un String para tenerlo en cuenta -->
      </div>
      <!-- boton ARCHIVAR/DESARCHIVAR -->
      <div class="card-body">
        <button class="btn btn-warning" @click="archivarDesarchivar">
          {{ note.archived ? "Desarchivar" : "Archivar" }}
        </button>
      </div>
      <!-- Categorías -->
      <div v-if="categoriesAsString" class="card-footer">
        <strong>Categorías: </strong>{{ categoriesAsString }}
      </div>
      <div class="card-footer">
        <router-link to="/notes" class="btn btn-primary"> Volver a la lista </router-link>
      </div>
    </div>
  </div>
</template>

<script>
import NoteRepository from "@/repositories/NoteRepository.js";
import auth from "@/common/auth";

//convierte un numero a string y si tiene solo 1 dígito le añade un 0 delante
function pad(n) {
  return n.toString().padStart(2, "0");
}

// cuando intentes cargar este componente devuelve esto: return...
export default {
  data() {
    //es la propiedad + importante
    return {
      note: null
    };
  },
  async mounted() {
    //codigo que queremos que se ejecute
    this.note = await NoteRepository.findOne(this.$route.params.noteId);
    //HAY QUE PONER EL ASYNC Y EL AWAIT
  }, // lo de $route.params.noteId creo que es porque te recupera un monton de cosas

  /* COMPUTED: Crea una propiedad de solo lectura que actualiza automaticamente cuando cambian las dependencias
   */
  computed: {
    categoriesAsString() {
      return this.note.categories.map((t) => t.name).join(", ");
      // map((t) => t.name)  note(listaCategorias(id - name))
    },
    isAdmin() {
      return auth.isAdmin();
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
    }
  },
  methods: {
    async archivarDesarchivar() {
      const payload = {
        id: this.note.id,
        title: this.note.title ?? "",
        content: this.note.content ?? null,
        archived: !this.note.archived,
        categories: Array.isArray(this.note.categories)
          ? this.note.categories.map((c) => ({ id: c.id }))
          : []
      };
      try {
        await NoteRepository.update(payload);
        this.$router.push("/notes"); // ACORDARSE ROUTERRRRRRR CON RRRRRRRR
      } catch (e) {
        console.error(e);
        alert(e.response?.data?.message || "Error al archivar/desarchivar la nota");
      }
    }
  }
};
</script>
