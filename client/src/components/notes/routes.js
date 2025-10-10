import NoteList from "./NoteList.vue";
import NoteDetail from "./NoteDetail.vue";
import NoteForm from "./NoteForm.vue";
import ChangeCreator from "./ChangeCreator.vue";

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
    path: "/notes/:noteId/owner",
    name: "CambiarCreador",
    component: ChangeCreator
  },
  {
    path: "/notes/new",
    name: "CrearNota",
    component: NoteForm
  },
  {
    path: "/notes/:noteId/edit",
    name: "EditarNota",
    component: NoteForm
  },
  {
    path: "/notes/user/:login",
    name: "ListByUser",
    component: NoteList,
    props: true
  }
];
