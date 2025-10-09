<template>
  <div class="container mt-3">
    <div class="d-flex justify-content-between align-items-center flex-wrap gap-2">
      <h1 class="m-0">Lista de notas</h1>
      <router-link v-if="!isAdmin" to="/notes/new" class="btn btn-success btn-sm">
        + Crear Nota
      </router-link>
      <button v-if="!isAdmin" class="btn btn-outline-secondary btn-sm" @click="loadArchivadas">
        {{ showArchived ? "Ocultar Archivadas" : "Mostrar archivadas" }}
      </button>
    </div>

    <!-- CONTADOR [FUNC-5] -->
    <p class="text-muted mt-2">
      Mostrando {{ visibleNotes.length }} nota<span v-if="visibleNotes.length !== 1">s</span>
    </p>
    <p v-if="visibleNotes.length === 0" class="fst-italic">No hay notas para mostrar.</p>

    <!-- A partir de aquí aparece la lista de notas en una "caja" grande -->
    <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 row-cols-xxl-4">
      <div class="col mb-3" v-for="note in visibleNotes" :key="note.id">
        <NoteCard
          :note="note"
          @cambioArchivado="onCambioArchivado"
          @notaEliminada="loadNotes"
        ></NoteCard>
      </div>
    </div>
  </div>
</template>

<script>
import NoteRepository from "@/repositories/NoteRepository";
import NoteCard from "./NoteCard.vue";
import auth from "@/common/auth";
import UserRepository from "../../repositories/UserRepository";

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
    //este watcher ya llama a loadNotes al crear el componente
    //watcher sobre categoryId
    categoryId: {
      immediate: true,
      handler() {
        this.loadNotes();
      }
    }
  },
  //ACTUALIZA CUANDO CAMBIE ALGUNO DE LOS PROPIEDADES DE NOTELIST
  computed: {
    //hacerla aquí la función para actualizar
    isAdmin() {
      return auth.isAdmin();
    },
    //Lista visible: filtra y orden en cliente
    visibleNotes() {
      let lista = [...this.notes];

      //admin ve siempre archivadas y no archivadas
      if (!this.isAdmin) {
        lista = this.showArchived
          ? lista.filter((n) => n.archived)
          : lista.filter((n) => !n.archived);
      }

      //filtro por categoría (si la ruta trae categoryId)
      if (this.categoryId) {
        //si tenemos categoria procedemos a filtrar
        const idNum = Number(this.categoryId);
        lista = lista.filter(
          (n) => Array.isArray(n.categories) && n.categories.some((cat) => cat.id === idNum)
        );
      }
      //orden por fecha desc
      lista.sort((a, b) => b.timestamp.getTime() - a.timestamp.getTime());
      return lista;
    }
  },
  methods: {
    async loadNotes() {
      let inactivos = null;

      //solo si eres admin intentas recuperar TODOS LOS USUARIOS
      if (this.isAdmin) {
        try {
          const users = await UserRepository.findAll();
          inactivos = new Set(users.filter((u) => !u.active).map((u) => u.login));
        } catch (e) {
          console.log(e);
          inactivos = null;
        }
      }
      try {
        const todas = await NoteRepository.findAll();
        //NO FILTRAR AQUÍ POR ARCHIVADAS/CATEGORIA: lo hace visibleNotes
        this.notes = inactivos ? todas.filter((n) => !inactivos.has(n.owner)) : todas;
      } catch (e) {
        console.log(e);
        this.notes = [];
      }
    },
    loadArchivadas() {
      this.showArchived = !this.showArchived;
    },
    async onCambioArchivado(note) {
      try {
        const payload = { ...note, archived: !note.archived }; //que hacen los 3 puntos
        await NoteRepository.update(payload);
        await this.loadNotes();
      } catch (e) {
        console.log(e);
        alert(e.response?.data?.message || "Error al archivar/desarchivar la nota");
      }
    }
  }
};
</script>
