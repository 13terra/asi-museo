import NoteList from "./NoteList.vue";
import NoteDetail from "./NoteDetail.vue";

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
  }
];
