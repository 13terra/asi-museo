<template>
  <div class="container animate-slide-up">
    <div v-if="edicion">
      <header class="mb-5">
        <div class="row align-items-center g-4">
          <div class="col-lg-8">
            <router-link class="btn btn-link text-decoration-none text-muted p-0 mb-3" :to="`/catalogo/${edicion.exposicion?.idExposicion}`">
              <i class="bi bi-arrow-left"></i> Volver a la exposición
            </router-link>
            <p class="text-uppercase text-muted small fw-bold mb-1">{{ edicion.exposicion?.titulo || 'Exposición' }}</p>
            <h1 class="display-4 mb-3">{{ edicion.nombre || `Edición ${edicion.idEdicion}` }}</h1>
            <p class="lead text-muted mb-3">{{ edicion.exposicion?.descripcion || 'Sin descripción' }}</p>
            <div class="d-flex gap-2 flex-wrap">
              <span class="badge rounded-pill" :class="stateClass(edicion.estado)">{{ edicion.estado }}</span>
              <span class="badge rounded-pill bg-light text-dark border">{{ rangoFechas }}</span>
            </div>
          </div>
          <div class="col-lg-4">
            <div class="card shadow-sm border-0 bg-light">
              <div class="card-body p-4 text-center">
                <h4 class="h5 fw-bold mb-2">Sesiones disponibles</h4>
                <p class="text-muted small mb-4">Reserva tus entradas en un par de clics.</p>
                <button class="btn btn-primary w-100 btn-lg" :disabled="!haySesionesDisponibles" @click="goFirstDisponible">
                  Reservar ahora
                </button>
                <p v-if="!haySesionesDisponibles" class="small text-muted mt-2 mb-0">No hay sesiones disponibles.</p>
              </div>
            </div>
          </div>
        </div>
      </header>

      <section class="mb-5">
        <div class="d-flex justify-content-between align-items-center mb-4">
          <h3 class="h4 fw-bold mb-0 text-primary">Sesiones</h3>
          <span class="badge bg-light text-dark border">{{ sesiones.length }} resultados</span>
        </div>

        <div v-if="loading" class="text-center py-5">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Cargando...</span>
          </div>
        </div>
        <div v-else-if="error" class="alert alert-danger shadow-sm" role="alert">
          <i class="bi bi-exclamation-triangle-fill me-2"></i> {{ error }}
        </div>
        <div v-else class="row g-3">
          <div class="col-md-6 col-lg-4" v-for="sesion in sesiones" :key="sesion.idSesion">
            <article class="card h-100 shadow-hover border-0">
              <div class="card-body d-flex flex-column">
                <div class="d-flex justify-content-between align-items-start mb-3">
                  <div>
                    <p class="text-uppercase text-muted small fw-bold mb-1">{{ formatFecha(sesion.horaInicio) }}</p>
                    <h4 class="h5 fw-bold mb-1">{{ formatHora(sesion.horaInicio) }} - {{ formatHora(sesion.horaFin) }}</h4>
                    <p class="text-muted small mb-0">Salas: {{ salasTexto(sesion.salas) }}</p>
                  </div>
                  <span class="badge rounded-pill" :class="sesionStateClass(sesion.estado)">{{ sesion.estado }}</span>
                </div>
                
                <div class="mb-3">
                  <span class="badge bg-light text-dark border">
                    Aforo: {{ sesion.aforoOcupado ?? sesion.ocupado ?? 0 }} / {{ sesion.aforo }}
                  </span>
                </div>

                <div class="mt-auto">
                  <button
                    class="btn btn-outline-primary w-100"
                    :disabled="sesion.estado !== estadosSesion.DISPONIBLE"
                    @click="irReserva(sesion.idSesion)"
                  >
                    Reservar entradas
                  </button>
                </div>
              </div>
            </article>
          </div>
        </div>
      </section>

      <section>
        <div class="d-flex justify-content-between align-items-center mb-4">
          <h3 class="h4 fw-bold mb-0 text-primary">Obras expuestas</h3>
          <span class="badge bg-light text-dark border">Agrupadas por sala</span>
        </div>

        <div v-if="piezasPorSalaKeys.length === 0" class="text-center py-5 border border-dashed rounded-3 bg-light">
          <p class="text-muted mb-0">Aún no hay piezas expuestas.</p>
        </div>
        
        <div v-else>
          <div v-for="sala in piezasPorSalaKeys" :key="sala" class="mb-5">
            <h5 class="border-bottom pb-2 mb-3 fw-bold">{{ sala }}</h5>
            <div class="row g-4">
              <div class="col-md-6 col-lg-4" v-for="pieza in piezasPorSala[sala]" :key="pieza.idPiezaExpuesta">
                <article class="card h-100 shadow-sm border-0">
                  <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center mb-2">
                      <span class="badge bg-primary" v-if="pieza.portada">Portada</span>
                      <span class="text-muted small ms-auto">Orden {{ pieza.orden }}</span>
                    </div>
                    <h4 class="h6 fw-bold mb-1">{{ pieza.obra?.titulo || 'Obra' }}</h4>
                    <p class="text-muted small mb-2">{{ pieza.obra?.autor }}</p>
                    <p class="card-text small text-muted">{{ pieza.textoCuratorial || 'Sin texto curatorial' }}</p>
                  </div>
                </article>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
    
    <div v-else-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Cargando...</span>
      </div>
    </div>
    
    <div v-else-if="error" class="alert alert-danger shadow-sm mt-5" role="alert">
      <i class="bi bi-exclamation-triangle-fill me-2"></i> {{ error }}
    </div>
  </div>
</template>

<script>
import EdicionRepository from '@/repositories/EdicionRepository';
import SesionRepository from '@/repositories/SesionRepository';
import PiezaExpuestaRepository from '@/repositories/PiezaExpuestaRepository';
import { ESTADOS_EDICION, ESTADOS_SESION } from '@/constants';

export default {
  name: 'PublicEditionView',
  data() {
    return {
      edicion: null,
      sesiones: [],
      piezas: [],
      loading: true,
      error: ''
    };
  },
  computed: {
    estadosSesion() {
      return ESTADOS_SESION;
    },
    rangoFechas() {
      if (!this.edicion?.fechaInicio || !this.edicion?.fechaFin) return '';
      return `${this.formatFecha(this.edicion.fechaInicio)} - ${this.formatFecha(this.edicion.fechaFin)}`;
    },
    piezasPorSala() {
      const grouped = {};
      this.piezas.forEach(p => {
        const sala = p.sala?.nombre || 'Sala sin nombre';
        if (!grouped[sala]) grouped[sala] = [];
        grouped[sala].push(p);
      });
      return grouped;
    },
    piezasPorSalaKeys() {
      return Object.keys(this.piezasPorSala);
    },
    haySesionesDisponibles() {
      return this.sesiones.some(s => s.estado === ESTADOS_SESION.DISPONIBLE);
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
        const [edicion, sesiones, piezas] = await Promise.all([
          EdicionRepository.getDetallePublic(id),
          SesionRepository.getByEdicion(id),
          PiezaExpuestaRepository.getByEdicion(id)
        ]);
        this.edicion = edicion;
        this.sesiones = sesiones || [];
        this.piezas = piezas || [];
      } catch (e) {
        this.error = 'No se pudo cargar la edición.';
      } finally {
        this.loading = false;
      }
    },
    formatFecha(value) {
      if (!value) return '';
      let d;
      if (Array.isArray(value)) {
        d = new Date(value[0], value[1] - 1, value[2], value[3], value[4]);
      } else {
        d = new Date(value);
      }
      return isNaN(d.getTime()) ? '' : d.toLocaleDateString();
    },
    formatHora(value) {
      if (!value) return '';
      let d;
      if (Array.isArray(value)) {
        d = new Date(value[0], value[1] - 1, value[2], value[3], value[4]);
      } else {
        d = new Date(value);
      }
      return isNaN(d.getTime()) ? '' : d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    },
    stateClass(estado) {
      return {
        [ESTADOS_EDICION.BORRADOR]: 'bg-secondary',
        [ESTADOS_EDICION.PUBLICADA]: 'bg-success',
        [ESTADOS_EDICION.FINALIZADA]: 'bg-dark',
        [ESTADOS_EDICION.CANCELADA]: 'bg-danger'
      }[estado] || 'bg-secondary';
    },
    sesionStateClass(estado) {
      return {
        [ESTADOS_SESION.DISPONIBLE]: 'bg-success',
        [ESTADOS_SESION.COMPLETA]: 'bg-dark',
        [ESTADOS_SESION.CANCELADA]: 'bg-danger'
      }[estado] || 'bg-secondary';
    },
    salasTexto(salas = []) {
      if (!salas.length) return 'Salas sin asignar';
      return salas.map(s => s.nombre || s.idSala).join(', ');
    },
    irReserva(idSesion) {
      this.$router.push({ name: 'ReservaEntradas', params: { id: idSesion }, query: { redirect: this.$route.fullPath } });
    },
    goFirstDisponible() {
      const sesion = this.sesiones.find(s => s.estado === ESTADOS_SESION.DISPONIBLE);
      if (sesion) this.irReserva(sesion.idSesion);
    }
  }
};
</script>

<style scoped>
/* Styles are now handled by global main.css */
</style>
