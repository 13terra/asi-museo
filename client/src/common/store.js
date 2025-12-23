import { ref } from "vue";

const store = ref({
  state: {
    user: {
      id: null,
      authority: "",
      estado: "",
      login: "",
      logged: false
    }
  }
});

export { getStore };

function getStore() {
  return store.value;
}
