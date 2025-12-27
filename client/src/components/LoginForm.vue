<template>
  <div class="container d-flex align-items-center justify-content-center min-vh-100 animate-slide-up">
    <div class="card shadow-lg border-0" style="max-width: 450px; width: 100%;">
      <div class="card-body p-5">
        <button class="btn btn-link text-decoration-none text-muted p-0 mb-4" @click="$router.push('/')">
          <i class="bi bi-arrow-left"></i> Volver al inicio
        </button>
        
        <h2 class="card-title text-center mb-4 fw-bold">Iniciar sesión</h2>
        
        <div class="mb-3">
          <label for="login" class="form-label">Usuario</label>
          <input 
            type="text" 
            class="form-control form-control-lg" 
            id="login" 
            v-model="auxLogin" 
            @keyup.enter="autenticarme"
            placeholder="Ingresa tu usuario"
          />
        </div>
        
        <div class="mb-4">
          <label for="pass" class="form-label">Contraseña</label>
          <input 
            type="password" 
            class="form-control form-control-lg" 
            id="pass" 
            v-model="auxPass" 
            @keyup.enter="autenticarme"
            placeholder="Ingresa tu contraseña"
          />
        </div>

        <div class="d-grid gap-2 mb-4">
          <button class="btn btn-primary btn-lg" @click="autenticarme" :disabled="loading">
            <span v-if="loading" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
            <span v-else>Entrar</span>
          </button>
        </div>

        <div class="text-center">
          <p class="mb-0">¿No tienes cuenta? <router-link to="/register" class="fw-bold text-primary text-decoration-none">Regístrate</router-link></p>
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
import auth from "../common/auth.js";
import { clearNotification, getStore } from "../common/store.js";
import { ROLES } from "@/constants";

export default {
  data() {
    return {
      auxLogin:   null,
      auxPass:  null,
      loading: false,
      error: ""
    };
  },
  methods: {
    async autenticarme() {
      this.loading = true;
      this.error = "";
      
      try {
        // ✅ Login real
        await auth.login({
          login: this.auxLogin,
          password: this.auxPass
        });
        
        clearNotification();

        // ✅ CORRECCIÓN: Pequeño delay para que el token se propague
        await new Promise(resolve => setTimeout(resolve, 100));

        // ✅ Redirigir según ROL
        const userAuthority = getStore().state.user. authority;
        const redirectPath = this.$route.query.redirect;
        
        if (redirectPath) {
          // ✅ Usar replace para evitar volver al login con el botón atrás
          await this.$router.replace(redirectPath);
        } else {
          // Redirigir según rol
          switch (userAuthority) {
            case ROLES.ADMIN:  
              await this.$router.replace({ name: 'PanelAdmin' });
              break;
            case ROLES.GESTOR:  
              await this.$router.replace({ name: 'PanelGestor' });
              break;
            case ROLES.VISITANTE:   
              await this.$router. replace({ name: 'CatalogoPublico' });
              break;
            default:
              await this.$router.replace({ name: 'HomeLanding' });
          }
        }
      } catch (e) {
        console.error('Error en login:', e);
        this.error = e.response?.data?.message || "Credenciales incorrectas.  Verifica tu login y contraseña.";
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