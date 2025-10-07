<template>
  <div class="container mt-3">
    <div class="d-flex justify-content-between align-items-center flex-wrap gap-2">
      <h1 class="m-0">Lista de notas</h1>
      <router-link v-if="!isAdmin" to="/notes/new" class="btn btn-success btn-sm">
        + Crear Nota
      </router-link>
      <button class="btn btn-outline-secondary btn-sm" @click="loadArchivadas">
        {{ showArchived ? "Ocultar Archivadas" : "Mostrar archivadas" }}
      </button>
    </div>

    <!-- CONTADOR [FUNC-5] -->
    <p class="text-muted mt-2">
      Mostrando {{ notes.length }} nota<span v-if="notes.length !== 1">s</span>
    </p>
    <p v-if="notes.length === 0" class="fst-italic">No hay notas para mostrar.</p>

    <!-- A partir de aquí aparece la lista de notas en una "caja" grande -->
    <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 row-cols-xxl-4">
      <div class="col mb-3" v-for="note in notes" :key="note.id">
        <NoteCard :note="note" @cambioArchivado="loadNotes" @notaEliminada="loadNotes"></NoteCard>
      </div>
    </div>
  </div>
</template>

<script>
import NoteRepository from "@/repositories/NoteRepository";
import NoteCard from "./NoteCard.vue";
import auth from "@/common/auth";

export default {
  props: {
    categoryId: { type: String, required: false } //llega desde la ruta dinámica porque en el router se puso props:true
  },
  components: { NoteCard },
  data() {
    return {
      notes: [],
      showArchived: false
    };
  },
  watch: {
    //watcher sobre categoryId
    categoryId: {
      immediate: true,
      handler() {
        this.loadNotes();
      }
    }
  },
  mounted() {
    if (!this.categoryId) this.loadNotes();
  },
  computed: {
    isAdmin() {
      return auth.isAdmin();
    }
  },
  methods: {
    async loadNotes() {
      const todas = await NoteRepository.findAll(); //recuperamos todas
      let lista = todas;
      if (this.showArchived) {
        lista = lista.filter((n) => n.archived); //mostramos archivadas
      } else {
        lista = lista.filter((n) => !n.archived); //NO mostramos archivadas
      }

      if (this.categoryId) {
        //si tenemos categoria procedemos a filtrar
        const idNum = Number(this.categoryId);
        lista = lista.filter(
          (n) => Array.isArray(n.categories) && n.categories.some((cat) => cat.id === idNum)
        );
      }

      lista.sort((a, b) => b.timestamp.getTime() - a.timestamp.getTime());
      this.notes = lista;
    },
    loadArchivadas() {
      this.showArchived = !this.showArchived;
      this.loadNotes();
    }
  }
};
</script>
