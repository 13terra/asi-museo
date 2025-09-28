import NoteList from "./NoteList.vue";
import NoteDetail from "./NoteDetail.vue";
import CreateNote from "./CreateNote.vue";

export default [
  {
    path: "/notes",
    name: "NoteList",
    component: NoteList
  },
  {
    path: "/notes/:noteId",
    name: "DetalleNota",
    component: NoteDetail
  },
  {
    path: "/notes/new",
    name: "CrearNota",
    component: CreateNote
  }
];
