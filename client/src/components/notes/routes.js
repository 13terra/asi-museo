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
    path: "/notes/category/:categoryId",
    name: "NotesByCategory",
    component: NoteList,
    props: true // pasa { categoryId: '...' } como prop
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
  } /*, {
    path: "/notes/:noteId/edit",
    name: "EditarNota",
    component: CreateNote //PUEDES HACER UNA IGUAL PERO CUIDADO 
  }*/
];
