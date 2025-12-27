<template>
  <div class="container animate-slide-up">
    <header class="d-flex justify-content-between align-items-center mb-5">
      <h1 class="display-5 mb-0">Mis Reservas</h1>
      <div class="d-flex align-items-center gap-2">
        <label class="form-label mb-0 me-2 fw-bold text-muted">Filtrar por estado:</label>
        <select v-model="estado" @change="load" class="form-select w-auto">
          <option value="">Todas</option>
          <option value="CONFIRMADA">Confirmada</option>
          <option value="FINALIZADA">Finalizada</option>
          <option value="CANCELADA">Cancelada</option>
        </select>
      </div>
    </header>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Cargando...</span>
      </div>
    </div>

    <div v-else-if="error" class="alert alert-danger shadow-sm" role="alert">
      <i class="bi bi-exclamation-triangle-fill me-2"></i> {{ error }}
    </div>

    <div v-else-if="reservas.length === 0" class="text-center py-5">
      <div class="p-5 border border-dashed rounded-3 bg-light">
        <i class="bi bi-ticket-perforated display-4 text-muted mb-3"></i>
        <p class="h5 text-muted">No tienes reservas todavía.</p>
        <router-link to="/catalogo" class="btn btn-primary mt-3">Explorar catálogo</router-link>
      </div>
    </div>

    <div v-else class="row g-4">
      <div class="col-md-6 col-lg-4" v-for="reserva in reservas" :key="reserva.idReserva">
        <article class="card h-100 shadow-hover border-0">
          <div class="card-body d-flex flex-column">
            <div class="d-flex justify-content-between align-items-start mb-3">
              <div>
                <p class="text-uppercase text-muted small fw-bold mb-1">Reserva #{{ reserva.idReserva }}</p>
                <h3 class="h5 fw-bold mb-1">{{ reserva.nombreExposicion || 'Exposición' }}</h3>
                <p class="text-muted small mb-0">
                  <i class="bi bi-calendar-event me-1"></i> {{ formatFecha(reserva.fechaSesion) }}
                  <span class="mx-1">·</span>
                  <i class="bi bi-clock me-1"></i> {{ formatHora(reserva.fechaSesion) }}
                </p>
              </div>
              <span class="badge rounded-pill" :class="stateClass(reserva.estado)">{{ reserva.estado }}</span>
            </div>
            
            <div class="mb-3">
              <p class="text-muted small mb-2">{{ reserva.edicion?.nombre || reserva.edicion?.idEdicion }}</p>
              <div class="bg-light rounded p-2 d-inline-block">
                <span class="fw-bold text-primary">{{ reserva.entradas?.length || reserva.numEntradas || 0 }} entradas</span>
                <span class="mx-2 text-muted">|</span>
                <span class="fw-bold">{{ formatPrice(reserva.importeTotal || 0) }}</span>
              </div>
            </div>

            <div class="mt-auto d-flex gap-2">
              <router-link :to="{ name: 'ReservaDetalle', params: { id: reserva.idReserva } }" class="btn btn-outline-primary flex-grow-1">
                Ver detalle
              </router-link>
              <button class="btn btn-danger" :disabled="!puedeCancelar(reserva)" @click="cancelar(reserva.idReserva)" v-if="puedeCancelar(reserva)">
                Cancelar
              </button>
            </div>
          </div>
        </article>
      </div>
    </div>
  </div>
</template>

<script>
import ReservaRepository from '@/repositories/ReservaRepository';
import { ESTADOS_RESERVA } from '@/constants';

export default {
  name: 'MisReservasView',
  data() {
    return {
      reservas: [],
      estado: '',
      loading: false,
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
        this.reservas = await ReservaRepository.getMisReservas({ estado: this.estado });
      } catch (e) {
        this.error = 'No se pudieron cargar tus reservas.';
      } finally {
        this.loading = false;
      }
    },
    async cancelar(id) {
      if (!confirm('¿Cancelar esta reserva?')) return;
      try {
        await ReservaRepository.cancelar(id);
        await this.load();
      } catch (e) {
        alert('No se pudo cancelar la reserva.');
      }
    },
    formatFecha(v) {
      if (!v) return '';
      let d;
      if (Array.isArray(v)) {
        d = new Date(v[0], v[1] - 1, v[2], v[3], v[4]);
      } else {
        d = new Date(v);
      }
      return isNaN(d.getTime()) ? '' : d.toLocaleDateString();
    },
    formatHora(v) {
      if (!v) return '';
      let d;
      if (Array.isArray(v)) {
        d = new Date(v[0], v[1] - 1, v[2], v[3], v[4]);
      } else {
        d = new Date(v);
      }
      return isNaN(d.getTime()) ? '' : d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    },
    formatPrice(v) { return `${Number(v || 0).toFixed(2)} €`; },
    stateClass(estado) {
      return {
        [ESTADOS_RESERVA.CONFIRMADA]: 'bg-success',
        [ESTADOS_RESERVA.CANCELADA]: 'bg-danger',
        [ESTADOS_RESERVA.FINALIZADA]: 'bg-dark'
      }[estado] || 'bg-secondary';
    },
    puedeCancelar(reserva) {
      return reserva.estado === ESTADOS_RESERVA.CONFIRMADA;
    }
  }
};
</script>

<style scoped>
/* Styles are now handled by global main.css */
</style>
