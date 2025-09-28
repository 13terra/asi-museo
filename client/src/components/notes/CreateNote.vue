<template>
  <div class="container mt-4" style="max-width: 640px">
    <h3 class="mb-3">Nueva Nota</h3>
    <form @submit.prevent="guardar">
      <div></div>
    </form>
  </div>
</template>

<script>
//import NoteRepository from '@/repositories/NoteRepository';
//import CategoryRepository from '@/repositories/CategoryRepository';

//serie de funciones para ayudar con la normalizacion de los inputs (3)

//elimina espacios en blanco (principio/fin)
// busca secuencias de 1 o + chars de espacio en blanco
// y los reemplaza por un único espacio normal
/*function normalizarNombre(n) {
    return n.trim().replace(/\s+/g, " "); 
  }

  function parsearValorInput(raw){
    
  }*/
export default {
  data() {
    return {
      title: null,
      content: null,
      categories: ""
    };
  },
  computed: {
    // funcion de validacion basica para el input de la nueva nota
    isValid() {
      const correct_title = this.title.trim();
      const correct_content = this.content.trim();
      return (correct_title || correct_content) && correct_title.length() <= 300;
    }
  },
  methods: {
    addCategories(e) {
      const value = e.target.value;
      if (!value.trim()) return;
    }
  }
};
</script>
