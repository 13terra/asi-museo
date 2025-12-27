<template>
  <div class="container animate-slide-up">
    <header class="d-flex justify-content-between align-items-center mb-5">
      <div>
        <h1 class="display-5 mb-2">Catálogo Público</h1>
        <p class="text-muted">Exposiciones activas con ediciones publicadas vigentes.</p>
      </div>
      <div class="d-flex gap-3 align-items-center">
        <template v-if="!user.logged">
          <router-link class="btn btn-outline-primary" to="/login">Iniciar sesión</router-link>
          <router-link class="btn btn-primary" to="/register">Registrarse</router-link>
        </template>
        <button v-else class="btn btn-outline-danger" @click="logout">
          <i class="bi bi-box-arrow-right"></i> Salir
        </button>
      </div>
    </header>

    <section class="row justify-content-center mb-5">
      <div class="col-md-8">
        <div class="input-group input-group-lg shadow-sm">
          <input 
            v-model="searchTerm" 
            type="search" 
            class="form-control border-0" 
            placeholder="Buscar exposiciones por título..." 
            @keyup.enter="search" 
          />
          <button class="btn btn-primary px-4" @click="search" :disabled="loading">
            <i class="bi bi-search"></i> Buscar
          </button>
        </div>
      </div>
    </section>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Cargando...</span>
      </div>
    </div>

    <div v-else-if="error" class="alert alert-danger shadow-sm" role="alert">
      <i class="bi bi-exclamation-triangle-fill me-2"></i> {{ error }}
    </div>

    <div v-else class="row g-4">
      <div v-if="expos.length === 0" class="col-12 text-center py-5">
        <div class="p-5 border border-dashed rounded-3 bg-light">
          <i class="bi bi-inbox display-4 text-muted mb-3"></i>
          <p class="h5 text-muted">No hay exposiciones disponibles.</p>
        </div>
      </div>
      
      <div class="col-md-6 col-lg-4" v-for="expo in expos" :key="expo.idExposicion">
        <article class="card h-100 shadow-hover border-0">
          <div class="card-img-top bg-light d-flex align-items-center justify-content-center" style="height: 200px;">
            <i class="bi bi-image text-muted display-4"></i>
          </div>
          <div class="card-body d-flex flex-column">
            <div class="mb-2">
              <span class="badge rounded-pill" :class="badgeClass(expo.estadoExpo)">{{ expo.estadoExpo }}</span>
            </div>
            <h3 class="card-title h5 fw-bold mb-3">{{ expo.titulo }}</h3>
            <p class="card-text text-muted flex-grow-1">{{ expo.descripcion || "Sin descripción disponible." }}</p>
            <router-link :to="`/catalogo/${expo.idExposicion}`" class="btn btn-outline-primary w-100 mt-3">
              Ver detalle <i class="bi bi-arrow-right ms-1"></i>
            </router-link>
          </div>
        </article>
      </div>
    </div>
  </div>
</template>

<script>
import ExpoRepository from "@/repositories/ExpoRepository";
import { getStore } from "@/common/store";
import auth from "@/common/auth";

export default {
  name: "PublicCatalogView",
  data() {
    return {
      expos: [],
      loading: false,
      error: "",
      searchTerm: "",
      user: getStore().state.user
    };
  },
  created() {
    this.loadPublic();
  },
  methods: {
    badgeClass(estado) {
      const map = {
        BORRADOR: "bg-secondary",
        ACTIVA: "bg-success",
        ARCHIVADA: "bg-dark"
      };
      return map[estado] || "bg-secondary";
    },
    async loadPublic() {
      this.loading = true;
      this.error = "";
      try {
        this.expos = await ExpoRepository.listPublic();
      } catch (e) {
        this.error = "No se pudo cargar el catálogo público";
      } finally {
        this.loading = false;
      }
    },
    async search() {
      if (!this.searchTerm) {
        return this.loadPublic();
      }
      this.loading = true;
      this.error = "";
      try {
        this.expos = await ExpoRepository.searchPublic(this.searchTerm);
      } catch (e) {
        this.error = "Error al buscar exposiciones";
      } finally {
        this.loading = false;
      }
    },
    logout() {
      auth.logout();
      this.$router.push("/catalogo");
    }
  }
};
</script>

<style scoped>
/* Styles are now handled by global main.css */
</style>
