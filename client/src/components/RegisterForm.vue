<template>
  <div class="container d-flex align-items-center justify-content-center min-vh-100 animate-slide-up">
    <div class="card shadow-lg border-0" style="max-width: 450px; width: 100%;">
      <div class="card-body p-5">
        <button class="btn btn-link text-decoration-none text-muted p-0 mb-4" @click="$router.back()">
          <i class="bi bi-arrow-left"></i> Volver atrás
        </button>
        
        <h2 class="card-title text-center mb-4 fw-bold">Registro de visitante</h2>
        
        <div class="mb-3">
          <label for="login" class="form-label">Usuario</label>
          <input 
            type="text" 
            class="form-control form-control-lg" 
            id="login" 
            v-model="auxLogin" 
            @keyup.enter="registrarme"
            placeholder="Elige un nombre de usuario"
          />
        </div>
        
        <div class="mb-3">
          <label for="pass" class="form-label">Contraseña</label>
          <input 
            type="password" 
            class="form-control form-control-lg" 
            id="pass" 
            v-model="auxPass" 
            @keyup.enter="registrarme"
            placeholder="Crea una contraseña"
          />
        </div>

        <div class="mb-4">
          <label for="confirmPass" class="form-label">Repetir contraseña</label>
          <input 
            type="password" 
            class="form-control form-control-lg" 
            id="confirmPass" 
            v-model="auxConfirmPass" 
            @keyup.enter="registrarme"
            placeholder="Repite tu contraseña"
          />
        </div>

        <div class="d-grid gap-2 mb-4">
          <button class="btn btn-primary btn-lg" @click="registrarme" :disabled="loading">
            <span v-if="loading" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
            <span v-else>Registrarme</span>
          </button>
        </div>

        <div class="text-center">
          <p class="mb-0">¿Ya tienes cuenta? <router-link to="/login" class="fw-bold text-primary text-decoration-none">Inicia sesión</router-link></p>
        </div>

        <div v-if="error" class="alert alert-danger mt-4 mb-0 d-flex align-items-center" role="alert">
          <i class="bi bi-exclamation-circle-fill me-2"></i>
          <div>{{ error }}</div>
        </div>
      </div>
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
/* Styles are now handled by global main.css */
</style>