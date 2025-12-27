<template>
  <div class="container py-5 animate-slide-up" v-if="!loading && expo">
    <!-- Header & Breadcrumbs -->
    <div class="d-flex justify-content-between align-items-center mb-4">
      <nav aria-label="breadcrumb">
        <ol class="breadcrumb mb-0">
          <li class="breadcrumb-item"><router-link to="/gestor" class="text-decoration-none text-muted">Exposiciones</router-link></li>
          <li class="breadcrumb-item active text-dark fw-bold" aria-current="page">{{ expo.titulo }}</li>
        </ol>
      </nav>
      <span class="badge rounded-pill" :class="badgeClass(expo.estadoExpo)">{{ expo.estadoExpo }}</span>
    </div>

    <!-- Hero Section -->
    <div class="card shadow-sm border-0 mb-5">
      <div class="card-body p-4 p-lg-5">
        <div class="row align-items-center g-4">
          <div class="col-lg-8">
            <p class="text-uppercase text-gold ls-1 mb-2 fw-bold small">Detalle de exposición</p>
            <h1 class="display-5 font-playfair mb-3">{{ expo.titulo }}</h1>
            <p class="lead text-muted mb-4">{{ expo.descripcion || 'Sin descripción disponible.' }}</p>
            
            <div class="d-flex flex-wrap gap-3">
              <span class="badge bg-light text-dark border">
                <i class="bi bi-person-circle me-1"></i>Creador: {{ expo.creador?.login || 'ADMIN' }}
              </span>
              <span class="badge bg-light text-dark border">
                <i class="bi bi-people me-1"></i>Gestores: {{ expo.gestores?.length || 0 }}
              </span>
              <span class="badge bg-light text-dark border">
                <i class="bi bi-calendar-event me-1"></i>Ediciones: {{ ediciones.length }}
              </span>
            </div>
          </div>
          <div class="col-lg-4 text-lg-end">
            <router-link class="btn btn-primary px-4 shadow-sm" :to="`/gestor/exposiciones/${expo.idExposicion}/permisos`">
              <i class="bi bi-shield-lock me-2"></i>Gestionar Permisos
            </router-link>
          </div>
        </div>
      </div>
    </div>

    <!-- Ediciones Section -->
    <section class="card shadow-sm border-0">
      <div class="card-header bg-white border-bottom-0 pt-4 px-4 d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center gap-3">
        <div>
          <p class="text-uppercase text-muted ls-1 mb-1 fw-bold small">Gestión de Ediciones</p>
          <h3 class="font-playfair h4 mb-0">Ediciones asociadas</h3>
        </div>
        
        <div class="d-flex flex-wrap gap-2 align-items-center bg-light p-2 rounded-3">
          <input type="date" v-model="edicionForm.fechaInicio" class="form-control form-control-sm" style="width: auto;" />
          <span class="text-muted small">a</span>
          <input type="date" v-model="edicionForm.fechaFin" class="form-control form-control-sm" style="width: auto;" />
          <button class="btn btn-sm btn-primary" @click="createEdicion" :disabled="edSaving">
            <span v-if="edSaving" class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span>
            Crear edición
          </button>
        </div>
      </div>

      <div class="card-body px-4 pb-4">
        <div v-if="edError" class="alert alert-danger py-2 mb-3 small">
          <i class="bi bi-exclamation-circle me-2"></i>{{ edError }}
        </div>

        <div v-if="ediciones.length === 0" class="alert alert-light text-center border border-dashed py-5 text-muted mb-0">
          <i class="bi bi-calendar-x fs-1 d-block mb-3"></i>
          <p class="mb-0">Aún no hay ediciones creadas para esta exposición.</p>
        </div>

        <div v-else class="list-group list-group-flush">
          <div v-for="ed in ediciones" :key="ed.idEdicion" class="list-group-item px-0 py-3 border-bottom">
            <div class="d-flex justify-content-between align-items-center flex-wrap gap-3">
              <div>
                <h5 class="mb-1 fw-bold">{{ ed.nombre || 'Edición sin nombre' }}</h5>
                <div class="d-flex align-items-center text-muted small">
                  <i class="bi bi-calendar3 me-2"></i>
                  {{ ed.fechaInicio }} <i class="bi bi-arrow-right mx-2"></i> {{ ed.fechaFin }}
                </div>
              </div>
              
              <div class="d-flex align-items-center gap-3">
                <span class="badge rounded-pill" :class="edBadgeClass(ed.estadoEdicion)">{{ ed.estadoEdicion }}</span>
                <router-link :to="`/gestor/exposiciones/${expo.idExposicion}/ediciones/${ed.idEdicion}`" class="btn btn-sm btn-outline-primary">
                  Gestionar <i class="bi bi-chevron-right ms-1"></i>
                </router-link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>

  <div v-else class="text-center py-5">
    <div class="spinner-border text-gold" role="status">
      <span class="visually-hidden">Cargando...</span>
    </div>
  </div>
</template>

<script>
import ExpoRepository from '@/repositories/ExpoRepository';
import EdicionRepository from '@/repositories/EdicionRepository';

export default {
  name: 'ExposicionDetalle',
  data() {
    return {
      expo: null,
      loading: true,
      ediciones: [],
      edSaving: false,
      edError: '',
      edicionForm: { fechaInicio: '', fechaFin: '' }
    };
  },
  created() { this.load(); },
  methods: {
    badgeClass(estado) {
      const map = { 
        ACTIVA: 'bg-success', 
        EN_PREPARACION: 'bg-warning text-dark', 
        BORRADOR: 'bg-secondary', 
        ARCHIVADA: 'bg-dark' 
      };
      return map[estado] || 'bg-secondary';
    },
    edBadgeClass(estado) {
      const map = {
        PUBLICADA: 'bg-success',
        BORRADOR: 'bg-secondary',
        FINALIZADA: 'bg-dark',
        CANCELADA: 'bg-danger'
      };
      return map[estado] || 'bg-secondary';
    },
    async load() {
      this.loading = true; this.edError = '';
      try {
        const id = this.$route.params.idExposicion || this.$route.params.id;
        this.expo = await ExpoRepository.detailAdmin(id);
        await this.loadEdiciones();
      } catch (e) { this.edError = 'No se pudo cargar el detalle'; }
      finally { this.loading = false; }
    },
    async loadEdiciones() {
      const id = this.$route.params.idExposicion || this.$route.params.id;
      this.ediciones = await EdicionRepository.getByExposicion(id);
    },
    async createEdicion() {
      if (!this.edicionForm.fechaInicio || !this.edicionForm.fechaFin) return;
      this.edSaving = true; this.edError = '';
      try {
        const id = this.$route.params.idExposicion || this.$route.params.id;
        await EdicionRepository.create(id, { ...this.edicionForm });
        this.edicionForm = { fechaInicio: '', fechaFin: '' };
        await this.loadEdiciones();
      } catch (e) { this.edError = 'No se pudo crear la edición'; }
      finally { this.edSaving = false; }
    }
  }
};
</script>

<style scoped>
/* Scoped styles removed in favor of Bootstrap classes */
</style>
