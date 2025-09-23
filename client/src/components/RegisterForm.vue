<template>
  <div>
    <div class="mb-3 mt-3">
      <label for="login">Login: </label>
      <input type="text" id="login" v-model="auxLogin" @keyup.enter="registrarme()" />
    </div>
    <div class="mb-3 mt-3">
      <label for="pass">Password: </label>
      <input type="password" id="pass" v-model="auxPass" @keyup.enter="registrarme()" />
    </div>
    <div class="mb-3 mt-3">
      <label for="confirmPass">Confirm Password: </label>
      <input
        type="password"
        id="confirmPass"
        v-model="auxConfirmPass"
        @keyup.enter="registrarme()"
      />
    </div>
    <div>
      <button @click="registrarme()">Register</button>
    </div>
  </div>
</template>

<script>
// Importamos AccountRepository porque en ese lugar tenemos la función registrartse
//import AccountRepository from '../repositories/AccountRepository';

import auth from "@/common/auth";

export default {
  data() {
    return {
      auxLogin: null,
      auxPass: null,
      auxConfirmPass: null
    };
  },
  methods: {
    // DEBERÍA IMPLEMENTAR AQUÍ EL MANEJO DEL ERROR O EN LA FUNCION DE AUTH?
    async registrarme() {
      try {
        if (this.auxPass !== this.auxConfirmPass) {
          alert("Las contraseñas no coinciden.");
          return;
        }

        // try registering if passwords match
        await auth.register({
          login: this.auxLogin,
          password: this.auxPass
        });
        this.$router.push("/notes"); // it takes u to the notes view
      } catch (e) {
        console.error(e);
        if (e.response?.data?.message) {
          alert(e.response.data.message);
        } else {
          alert(e.message);
        }
      }
    }
  }
};
</script>
