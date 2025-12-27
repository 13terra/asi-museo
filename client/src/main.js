import "bootstrap/dist/css/bootstrap.css";
import "bootstrap/dist/js/bootstrap.bundle.min.js";
import "bootstrap-icons/font/bootstrap-icons.css";
import "./assets/main.css"; // Custom styles
import { createApp } from "vue";
import App from "./App.vue"; //importamos la raiz desde otro archivo
import router from "./router";

const app = createApp(App); // creas la instancia de la app (sabe como renderizar la raiz)

app.use(router);

app.mount("#app"); // devuelve el componente raiz en lugar de la instancia de la app
