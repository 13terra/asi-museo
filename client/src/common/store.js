import { ref } from "vue";

const store = ref({
  state: {
    user: {
      authority: "",
      login: "",
      logged: false,
      idUser: null
    },
    //estado global para mensajes/notificaciones
    notification: {
      show: false,
      message: '',
      type: '' //success, error, warning, info
    }
  }
});

export { getStore, setNotification, clearNotification };

function getStore() {
  return store.value;
}

/**
 * Muestra una notificación global
 * @param {string} message - Mensaje a mostrar
 * @param {string} type - Tipo:  success, error, warning, info
 */
function setNotification(message, type = 'info') {
  store.value.state.notification = {
    show: true,
    message,
    type
  };
  
  // Auto-ocultar después de 5 segundos
  setTimeout(() => {
    clearNotification();
  }, 5000);
}

function clearNotification() {
  store.value.state.notification = {
    show: false,
    message: '',
    type: ''
  };
}
