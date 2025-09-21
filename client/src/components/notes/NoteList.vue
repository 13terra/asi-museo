<template>
  <div class="container">
    <h1>Lista de notas</h1>
    <!-- Esto será el tema de la lista de las notas -->
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
    this.notes = await NoteRepository.findAll();
  }
};
</script>
