<template>
  <div>
    <!--Habría que poner otro div aquí para poner un título flow PORTAL LOGIN/ PORTAL REGISTRO-->
    <div class="mb-3 mt-3">
      <!--Que hace esta línea  -->
      <label for="login">Login: </label>
      <input type="text" id="login" v-model="auxLogin" @keyup.enter="autenticarme()" />
    </div>
    <div class="mb-3">
      <label for="pass">Password: </label>
      <input type="password" id="pass" v-model="auxPass" @keyup.enter="autenticarme()" />
    </div>
    <div>
      <button @click="autenticarme()">autenticarse</button>
    </div>
  </div>
</template>

<script>
import auth from "../common/auth.js";

export default {
  data() {
    return {
      auxLogin: null,
      auxPass: null
    };
  },
  methods: {
    async autenticarme() {
      try {
        await auth.login({
          login: this.auxLogin,
          password: this.auxPass
        });
        this.$router.push("/notes");
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
