<template>
  <div v-if="note != null" class="container mt-4">
    <!-- ERROR DE LOS TÍPICOS MUY IMPORTANTE ENTENDERLO, SI LA NOTA NO ES NULA ENTONCES...-->
    <div class="card">
      <!--Cabecera con propietario y fecha -->
      <div class="card-header d-flex justify-content-between">
        <h3>
          {{ note.owner }}
        </h3>
        <small>
          <!--ACORDARSE QUE LAS LLAMADAS A LAS FUNCIONES ACABAN EN ()-->
          {{ note.timestamp.toLocaleDateString() }} - {{ note.timestamp.toLocaleTimeString() }}
        </small>
      </div>
      <!--Cuerpo de la nota -->
      <div class="card-body">
        <h5 class="card-title">{{ note.title }}</h5>
        <p class="card-text">{{ note.content }}</p>
        <!-- El contenido de la nota es un String para tenerlo en cuenta -->
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
    }
  }
};
</script>
