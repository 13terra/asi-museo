<template>
  <div class="container mt-3">
    <div class="d-flex justify-content-between align-items-center flex-wrap gap-2">
      <h1 class="m-0">Lista de notas</h1>
      <router-link to="/notes/new" class="btn btn-success btn-sm"> + Crear Nota </router-link>
    </div>

    <!-- CONTADOR [FUNC-5] -->
    <p class="text-muted mt-2">
      Mostrando {{ notes.length }} nota<span v-if="notes.length !== 1">s</span>
    </p>
    <p v-if="notes.length === 0" class="fst-italic">No hay notas para mostrar.</p>

    <!-- A partir de aquí aparece la lista de notas en una "caja" grande -->
    <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 row-cols-xxl-4">
      <div class="col mb-3" v-for="note in notes" :key="note.id">
        <NoteCard :note="note"></NoteCard>
      </div>
    </div>
  </div>
</template>

<script>
import NoteRepository from "@/repositories/NoteRepository";
import NoteCard from "./NoteCard.vue";

export default {
  data() {
    return {
      notes: []
    };
  },
  components: { NoteCard },
  async mounted() {
    const todas = await NoteRepository.findAll(); //recuperamos todas

    //filtramos archivadas y ordenamos de manera descendente
    /* Si b es más reciente (ms mayores) el rtdo es positivo --> a se coloca detrás 
    Si a es más reciente el rtdo es negativo --> a se coloca antes 
    así es como se ordenan las notas*/
    this.notes = todas
      .filter((n) => !n.archived)
      .sort((a, b) => b.timestamp.getTime() - a.timestamp.getTime());
  }
};
</script>
