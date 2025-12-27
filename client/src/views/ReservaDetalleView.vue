<template>
  <div class="container animate-slide-up">
    <div v-if="reserva">
      <header class="d-flex justify-content-between align-items-center mb-5">
        <div>
          <router-link class="btn btn-link text-decoration-none text-muted p-0 mb-3" to="/mis-reservas">
            <i class="bi bi-arrow-left"></i> Volver a mis reservas
          </router-link>
          <p class="text-uppercase text-muted small fw-bold mb-1">Reserva #{{ reserva.idReserva }}</p>
          <h1 class="display-5 mb-1">{{ reserva.nombreExposicion || 'Exposición' }}</h1>
          <p class="text-muted mb-0">
            <i class="bi bi-calendar-event me-1"></i> {{ formatFecha(reserva.fechaHoraSesion) }}
            <span class="mx-1">·</span>
            <i class="bi bi-clock me-1"></i> {{ formatHora(reserva.fechaHoraSesion) }}
          </p>
        </div>
        <span class="badge rounded-pill fs-6" :class="stateClass(reserva.estado)">{{ reserva.estado }}</span>
      </header>

      <div class="row g-4">
        <div class="col-lg-4">
          <div class="card shadow-sm border-0 h-100">
            <div class="card-header bg-white border-0 pt-4 px-4">
              <h3 class="h5 fw-bold mb-0 text-primary">Datos del comprador</h3>
            </div>
            <div class="card-body p-4">
              <div class="mb-4">
                <h5 class="fw-bold mb-1">{{ reserva.nombrePila }} {{ reserva.apellido1 }} {{ reserva.apellido2 }}</h5>
                <p class="text-muted mb-0">{{ reserva.email }}</p>
                <p class="text-muted mb-0">{{ reserva.telefono }}</p>
                <p class="text-muted mb-0">{{ reserva.pais }}</p>
              </div>
              <div class="bg-light rounded p-3 text-center">
                <p class="text-muted small mb-1">Importe Total</p>
                <h4 class="fw-bold text-primary mb-0">{{ formatPrice(reserva.importeTotal || 0) }}</h4>
              </div>
            </div>
          </div>
        </div>

        <div class="col-lg-8">
          <div class="card shadow-sm border-0 h-100">
            <div class="card-header bg-white border-0 pt-4 px-4 d-flex justify-content-between align-items-center">
              <h3 class="h5 fw-bold mb-0 text-primary">Entradas</h3>
              <span class="badge bg-light text-dark">{{ entradas.length }} entradas</span>
            </div>
            <div class="card-body p-4">
              <div v-if="entradas.length === 0" class="text-center py-4 text-muted">
                <i class="bi bi-ticket-perforated display-4 d-block mb-2"></i>
                Sin entradas asociadas.
              </div>
              <div v-else class="list-group list-group-flush">
                <div v-for="entrada in entradas" :key="entrada.idEntrada" class="list-group-item py-3 px-0 border-bottom">
                  <div class="d-flex justify-content-between align-items-center flex-wrap gap-3">
                    <div>
                      <div class="d-flex align-items-center gap-2 mb-1">
                        <span class="badge bg-secondary">#{{ entrada.idEntrada }}</span>
                        <span class="fw-bold">{{ entrada.tipoEntrada?.nombre }}</span>
                      </div>
                      <p class="mb-1">{{ entrada.nombrePila }} {{ entrada.apellido1 }} {{ entrada.apellido2 }}</p>
                      <p class="text-muted small mb-0">DNI: {{ entrada.dni }}</p>
                    </div>
                    <div class="text-end">
                      <p class="fw-bold text-primary mb-2">{{ formatPrice(entrada.precio || entrada.tipoEntrada?.precio) }}</p>
                      <router-link :to="{ name: 'EntradaDetalle', params: { id: entrada.idEntrada } }" class="btn btn-sm btn-outline-primary">
                        Ver entrada
                      </router-link>
                    </div>
                  </div>
                </div>
              </div>
              
              <div class="mt-4 text-end" v-if="puedeCancelar">
                <button class="btn btn-danger" @click="cancelar">
                  <i class="bi bi-x-circle me-1"></i> Cancelar reserva
                </button>
              </div>
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
        <i class="bi bi-search display-4 text-muted mb-3"></i>
        <p class="h5 text-muted">Reserva no encontrada.</p>
      </div>
    </div>
  </div>
</template>

<script>
import ReservaRepository from '@/repositories/ReservaRepository';
import EntradaRepository from '@/repositories/EntradaRepository';
import { ESTADOS_RESERVA } from '@/constants';

export default {
  name: 'ReservaDetalleView',
  data() {
    return {
      reserva: null,
      entradas: [],
      loading: true,
      error: ''
    };
  },
  computed: {
    puedeCancelar() {
      return this.reserva?.estado === ESTADOS_RESERVA.CONFIRMADA;
    }
  },
  async created() {
    await this.load();
  },
  methods: {
    async load() {
      this.loading = true;
      this.error = '';
      try {
        const id = this.$route.params.id;
        const [reserva, entradas] = await Promise.all([
          ReservaRepository.getById(id),
          EntradaRepository.getByReserva(id)
        ]);
        this.reserva = reserva;
        this.entradas = entradas || [];
      } catch (e) {
        this.error = 'No se pudo cargar la reserva.';
      } finally {
        this.loading = false;
      }
    },
    async cancelar() {
      if (!this.puedeCancelar) return;
      if (!confirm('¿Cancelar esta reserva?')) return;
      try {
        await ReservaRepository.cancelar(this.reserva.idReserva);
        await this.load();
      } catch (e) {
        alert('No se pudo cancelar la reserva.');
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
    formatPrice(v) { return `${Number(v || 0).toFixed(2)} €`; },
    stateClass(estado) {
      return {
        [ESTADOS_RESERVA.CONFIRMADA]: 'bg-success',
        [ESTADOS_RESERVA.CANCELADA]: 'bg-danger',
        [ESTADOS_RESERVA.FINALIZADA]: 'bg-dark'
      }[estado] || 'bg-secondary';
    }
  }
};
</script>

<style scoped>
/* Styles are now handled by global main.css */
</style>
