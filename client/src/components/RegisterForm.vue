<template>
  <div class="auth-shell">
    <div class="auth-card">
      <button class="btn-ghost" @click="$router.back()">Volver atrás</button>
      <h2 class="auth-title">Registro de visitante</h2>
      <div class="auth-field">
        <label for="login">Login nuevo</label>
        <input type="text" id="login" v-model="auxLogin" @keyup.enter="registrarme" />
      </div>
      <div class="auth-field">
        <label for="pass">Contraseña</label>
        <input type="password" id="pass" v-model="auxPass" @keyup.enter="registrarme" />
      </div>
      <div class="auth-field">
        <label for="confirmPass">Repetir contraseña</label>
        <input type="password" id="confirmPass" v-model="auxConfirmPass" @keyup.enter="registrarme" />
      </div>
      <button class="btn-primary" @click="registrarme" :disabled="loading">Registrarme</button>
      <p class="auth-link">¿Ya tienes cuenta? <router-link to="/login">Inicia sesión</router-link></p>
      <p v-if="error" class="auth-error">{{ error }}</p>
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
      auxConfirmPass: null,
      loading: false,
      error: ""
    };
  },
  methods: {
    // DEBERÍA IMPLEMENTAR AQUÍ EL MANEJO DEL ERROR O EN LA FUNCION DE AUTH?
    async registrarme() {
      this.loading = true;
      this.error = "";
      try {
        if (this.auxPass !== this.auxConfirmPass) {
          this.error = "Las contraseñas no coinciden";
          this.loading = false;
          return;
        }

        // try registering if passwords match
        await auth.register({
          login: this.auxLogin,
          password: this.auxPass,
          passwordConfirm: this.auxConfirmPass
        });
        this.$router.push({ name: "CatalogoPublico" });
      } catch (e) {
        this.error = e.response?.data?.message || "No se pudo registrar";
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>

<style scoped>
.auth-shell {
  min-height: 80vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f7f9fc 0%, #eef2f7 100%);
}

.auth-card {
  width: min(420px, 90vw);
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.auth-title {
  margin: 4px 0 12px;
  font-weight: 800;
  letter-spacing: 0.2px;
}

.auth-field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.auth-field input {
  border-radius: 10px;
  border: 1px solid #d9deea;
  padding: 12px 14px;
  font-size: 16px;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.auth-field input:focus {
  outline: none;
  border-color: #1f4b99;
  box-shadow: 0 0 0 3px rgba(31, 75, 153, 0.15);
}

.btn-primary {
  background: #1f4b99;
  color: #fff;
  border: none;
  border-radius: 10px;
  padding: 12px 14px;
  font-weight: 700;
  cursor: pointer;
  transition: transform 0.1s ease, box-shadow 0.2s ease, opacity 0.2s ease;
}

.btn-primary:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}

.btn-primary:hover:not(:disabled) {
  box-shadow: 0 10px 24px rgba(31, 75, 153, 0.2);
  transform: translateY(-1px);
}

.btn-ghost {
  align-self: flex-start;
  background: transparent;
  border: 1px solid #d9deea;
  border-radius: 10px;
  padding: 8px 12px;
  font-weight: 600;
  cursor: pointer;
}

.auth-link {
  margin: 0;
  font-weight: 600;
}

.auth-link a {
  color: #1f4b99;
}

.auth-error {
  color: #b3261e;
  font-weight: 600;
}
</style>