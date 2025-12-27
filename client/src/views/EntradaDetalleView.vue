<template>
  <div class="container animate-slide-up">
    <div v-if="entrada">
      <header class="d-flex justify-content-between align-items-center mb-5">
        <div>
          <router-link class="btn btn-link text-decoration-none text-muted p-0 mb-3" :to="{ name: 'ReservaDetalle', params: { id: entrada.idReserva } }">
            <i class="bi bi-arrow-left"></i> Volver a la reserva
          </router-link>
          <p class="text-uppercase text-muted small fw-bold mb-1">Entrada #{{ entrada.idEntrada }}</p>
          <h1 class="display-5 mb-1">{{ entrada.nombreExposicion || 'Exposición' }}</h1>
          <p class="text-muted mb-0">
            <i class="bi bi-calendar-event me-1"></i> {{ formatFecha(entrada.fechaHoraSesion) }}
            <span class="mx-1">·</span>
            <i class="bi bi-clock me-1"></i> {{ formatHora(entrada.fechaHoraSesion) }}
          </p>
        </div>
        <span class="badge rounded-pill bg-success fs-6">{{ entrada.estado || 'VÁLIDA' }}</span>
      </header>

      <div class="row justify-content-center">
        <div class="col-md-8 col-lg-6">
          <div class="card shadow-lg border-0">
            <div class="card-header bg-primary text-white text-center py-4">
              <h3 class="h5 fw-bold mb-0">Ticket de Acceso</h3>
            </div>
            <div class="card-body p-5">
              <div class="text-center mb-4">
                <h4 class="fw-bold mb-1">{{ entrada.nombrePila }} {{ entrada.apellido1 }} {{ entrada.apellido2 }}</h4>
                <p class="text-muted mb-0">DNI: {{ entrada.dni }}</p>
              </div>

              <hr class="my-4">

              <div class="row g-4 mb-4">
                <div class="col-6">
                  <p class="text-uppercase text-muted small fw-bold mb-1">Tipo de Entrada</p>
                  <p class="fw-bold mb-0">{{ entrada.tipoEntrada }}</p>
                  <p class="text-primary small mb-0">{{ formatPrice(entrada.precio) }}</p>
                </div>
                <div class="col-6 text-end">
                  <p class="text-uppercase text-muted small fw-bold mb-1">Sala(s)</p>
                  <p class="fw-bold mb-0">{{ entrada.nombresSalas || '—' }}</p>
                </div>
              </div>

              <div class="bg-light rounded-3 p-4 text-center border border-dashed">
                <i class="bi bi-qr-code-scan display-1 text-dark mb-3"></i>
                <p class="fw-bold font-monospace mb-0 text-break">{{ entrada.idEntrada }}</p>
                <p class="small text-muted mt-2 mb-0">Presenta este código en la entrada</p>
              </div>
            </div>
            <div class="card-footer bg-white text-center py-3 border-top-0">
              <small class="text-muted">Museo de Arte y Diseño</small>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-else-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Cargando...</span>
      </div>
    </div>

    <div v-else-if="error" class="alert alert-danger shadow-sm mt-5" role="alert">
      <i class="bi bi-exclamation-triangle-fill me-2"></i> {{ error }}
    </div>

    <div v-else class="text-center py-5">
      <div class="p-5 border border-dashed rounded-3 bg-light">
        <i class="bi bi-ticket-detailed display-4 text-muted mb-3"></i>
        <p class="h5 text-muted">Entrada no encontrada.</p>
      </div>
    </div>
  </div>
</template>

<script>
import EntradaRepository from '@/repositories/EntradaRepository';

export default {
  name: 'EntradaDetalleView',
  data() {
    return {
      entrada: null,
      loading: true,
      error: ''
    };
  },
  async created() {
    await this.load();
  },
  methods: {
    async load() {
      this.loading = true;
      this.error = '';
      try {
        this.entrada = await EntradaRepository.getById(this.$route.params.id);
      } catch (e) {
        this.error = 'No se pudo cargar la entrada.';
      } finally {
        this.loading = false;
      }
    },
    formatFecha(v) {
      if (!v) return '';
      if (Array.isArray(v)) return new Date(v[0], v[1] - 1, v[2]).toLocaleDateString();
      const d = new Date(v);
      return isNaN(d.getTime()) ? '' : d.toLocaleDateString();
    },
    formatHora(v) {
      if (!v) return '';
      if (Array.isArray(v)) {
        if (v.length === 2) return `${v[0].toString().padStart(2, '0')}:${v[1].toString().padStart(2, '0')}`;
        if (v.length >= 5) return `${v[3].toString().padStart(2, '0')}:${v[4].toString().padStart(2, '0')}`;
      }
      const d = new Date(v);
      return isNaN(d.getTime()) ? '' : d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    },
    formatPrice(v) { return `${Number(v || 0).toFixed(2)} €`; }
  }
};
</script>

<style scoped>
/* Styles are now handled by global main.css */
</style>
