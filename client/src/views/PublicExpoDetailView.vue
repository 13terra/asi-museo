<template>
  <div class="container animate-slide-up">
    <div v-if="!loading && expo">
      <div class="mb-4">
        <router-link class="btn btn-link text-decoration-none text-muted p-0 mb-3" to="/catalogo">
          <i class="bi bi-arrow-left"></i> Volver al catálogo
        </router-link>
        <div class="d-flex align-items-center gap-3">
          <h1 class="display-4 mb-0">{{ expo.titulo }}</h1>
          <span class="badge rounded-pill" :class="badgeClass(expo.estadoExpo)">{{ expo.estadoExpo }}</span>
        </div>
      </div>

      <div class="row g-5">
        <div class="col-lg-8">
          <div class="card shadow-sm border-0 mb-4">
            <div class="card-body p-4">
              <h3 class="h5 fw-bold mb-3 text-primary">Descripción</h3>
              <p class="lead text-muted">{{ expo.descripcion || "Sin descripción disponible." }}</p>
            </div>
          </div>

          <div class="card shadow-sm border-0">
            <div class="card-header bg-white border-0 pt-4 px-4">
              <h3 class="h5 fw-bold mb-0 text-primary">Ediciones Publicadas</h3>
            </div>
            <div class="card-body p-4">
              <div v-if="ediciones.length === 0" class="text-center py-4 text-muted">
                <i class="bi bi-calendar-x display-4 d-block mb-2"></i>
                Aún no hay ediciones publicadas.
              </div>
              <div v-else class="list-group list-group-flush">
                <div v-for="ed in ediciones" :key="ed.idEdicion" class="list-group-item d-flex justify-content-between align-items-center py-3 px-0 border-bottom">
                  <div>
                    <h5 class="mb-1 fw-bold">{{ ed.nombre || 'Edición sin nombre' }}</h5>
                    <p class="mb-0 text-muted small">
                      <i class="bi bi-calendar-event me-1"></i>
                      {{ ed.fechaInicio }} <i class="bi bi-arrow-right mx-1"></i> {{ ed.fechaFin }}
                    </p>
                  </div>
                  <span class="badge rounded-pill" :class="edBadge(ed.estadoEdicion)">{{ ed.estadoEdicion }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="col-lg-4">
          <div class="card bg-light border-0 shadow-sm">
            <div class="card-body p-4 text-center">
              <i class="bi bi-info-circle display-4 text-primary mb-3"></i>
              <h4 class="h5">Información</h4>
              <p class="text-muted small">Consulta las ediciones disponibles para planificar tu visita.</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-else-if="error" class="alert alert-danger shadow-sm mt-5" role="alert">
      <i class="bi bi-exclamation-triangle-fill me-2"></i> {{ error }}
    </div>

    <div v-else class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Cargando...</span>
      </div>
    </div>
  </div>
</template>

<script>
import ExpoRepository from "@/repositories/ExpoRepository";
import EdicionRepository from "@/repositories/EdicionRepository";
import { ESTADOS_EDICION } from "@/constants";

export default {
  name: "PublicExpoDetailView",
  data() {
    return {
      expo: null,
      ediciones: [],
      loading: true,
      error: ""
    };
  },
  async created() {
    await this.load();
  },
  methods: {
    badgeClass(estado) {
      const map = {
        ACTIVA: "bg-success",
        EN_PREPARACION: "bg-secondary",
        BORRADOR: "bg-secondary",
        ARCHIVADA: "bg-dark"
      };
      return map[estado] || "bg-secondary";
    },
    edBadge(estado) {
      const map = {
        PUBLICADA: "bg-success",
        BORRADOR: "bg-secondary",
        FINALIZADA: "bg-dark",
        CANCELADA: "bg-danger"
      };
      return map[estado] || "bg-secondary";
    },
    async load() {
      this.loading = true;
      this.error = "";
      try {
        const id = this.$route.params.id;
        this.expo = await ExpoRepository.detailPublic(id);
        const ediciones = await EdicionRepository.getByExposicionPublic(id);
        this.ediciones = (ediciones || []).filter(ed => ed.estadoEdicion === ESTADOS_EDICION.PUBLICADA);
      } catch (e) {
        this.error = "No se pudo cargar la exposición";
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
