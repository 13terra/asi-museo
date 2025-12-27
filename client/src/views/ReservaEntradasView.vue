<template>
  <div class="container py-5 animate-slide-up">
    <!-- Header de la Sesión -->
    <header class="mb-5 text-center" v-if="sesion && edicion">
      <p class="text-uppercase text-gold ls-1 mb-2 fw-bold">{{ edicion.exposicion?.titulo || 'Exposición' }}</p>
      <h1 class="display-5 font-playfair mb-3">Reserva para {{ formatFecha(sesion.horaInicio) }}</h1>
      <div class="d-flex justify-content-center align-items-center gap-3 text-muted">
        <span><i class="bi bi-clock me-1"></i>{{ formatHora(sesion.horaInicio) }} - {{ formatHora(sesion.horaFin) }}</span>
        <span><i class="bi bi-geo-alt me-1"></i>Salas {{ salasTexto(sesion.salas) }}</span>
      </div>
      <div class="mt-3">
        <span class="badge bg-light text-dark border">
          Aforo: {{ sesion.aforoOcupado ?? 0 }} / {{ sesion.aforo }}
        </span>
      </div>
    </header>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-gold" role="status">
        <span class="visually-hidden">Cargando...</span>
      </div>
    </div>

    <div v-else-if="error" class="alert alert-danger shadow-sm" role="alert">
      <i class="bi bi-exclamation-triangle-fill me-2"></i>{{ error }}
    </div>

    <div v-else class="row g-4">
      <!-- Selección de Entradas -->
      <div class="col-lg-5">
        <section class="card shadow-sm border-0 h-100">
          <div class="card-header bg-white border-bottom-0 pt-4 px-4">
            <h3 class="font-playfair h4 mb-0">Selección de Entradas</h3>
          </div>
          <div class="card-body px-4">
            <div class="d-flex flex-column gap-3">
              <div v-for="tipo in tipos" :key="tipo.idTipoEntrada" class="card border-light bg-light">
                <div class="card-body d-flex justify-content-between align-items-center p-3">
                  <div>
                    <h5 class="mb-1 fw-bold">{{ tipo.nombre }}</h5>
                    <p class="small text-muted mb-0">{{ tipo.descripcion || 'Entrada general' }}</p>
                    <div class="text-gold fw-bold mt-1">{{ formatPrice(tipo.precio) }}</div>
                  </div>
                  <div class="d-flex align-items-center gap-2 bg-white rounded-pill border p-1 shadow-sm">
                    <button 
                      class="btn btn-sm btn-link text-dark text-decoration-none px-2" 
                      @click="dec(tipo.idTipoEntrada)" 
                      :disabled="cantidades[tipo.idTipoEntrada] <= 0"
                    >
                      <i class="bi bi-dash-lg"></i>
                    </button>
                    <span class="fw-bold px-2" style="min-width: 20px; text-align: center;">
                      {{ cantidades[tipo.idTipoEntrada] || 0 }}
                    </span>
                    <button 
                      class="btn btn-sm btn-link text-dark text-decoration-none px-2" 
                      @click="inc(tipo.idTipoEntrada)" 
                      :disabled="isMaxReached"
                    >
                      <i class="bi bi-plus-lg"></i>
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <div class="mt-4 pt-3 border-top">
              <div class="d-flex justify-content-between align-items-center mb-2">
                <span class="text-muted">Total entradas</span>
                <span class="fw-bold">{{ totalEntradas }}</span>
              </div>
              <div class="d-flex justify-content-between align-items-center">
                <span class="h5 mb-0 font-playfair">Total a pagar</span>
                <span class="h4 mb-0 text-gold fw-bold">{{ formatPrice(totalPrecio) }}</span>
              </div>
            </div>

            <div v-if="isMaxReached" class="alert alert-warning mt-3 mb-0 d-flex align-items-center">
              <i class="bi bi-info-circle me-2"></i>
              <small v-if="totalEntradas >= 10">Máximo 10 entradas por compra.</small>
              <small v-else>Aforo completo para esta sesión.</small>
            </div>
          </div>
        </section>
      </div>

      <!-- Formulario de Datos -->
      <div class="col-lg-7">
        <section class="card shadow-sm border-0 h-100">
          <div class="card-header bg-white border-bottom-0 pt-4 px-4">
            <h3 class="font-playfair h4 mb-0">Datos de la Reserva</h3>
          </div>
          <div class="card-body px-4">
            <form @submit.prevent="reservar">
              <h5 class="mb-3 text-muted border-bottom pb-2">Datos del Comprador</h5>
              <div class="row g-3 mb-4">
                <div class="col-md-6">
                  <label class="form-label small fw-bold text-uppercase text-muted">Nombre *</label>
                  <input v-model="comprador.nombrePila" class="form-control" placeholder="Ej: Juan" required maxlength="50" />
                </div>
                <div class="col-md-6">
                  <label class="form-label small fw-bold text-uppercase text-muted">Primer Apellido *</label>
                  <input v-model="comprador.apellido1" class="form-control" placeholder="Ej: Pérez" required maxlength="50" />
                </div>
                <div class="col-md-6">
                  <label class="form-label small fw-bold text-uppercase text-muted">Segundo Apellido</label>
                  <input v-model="comprador.apellido2" class="form-control" placeholder="Ej: García" maxlength="50" />
                </div>
                <div class="col-md-6">
                  <label class="form-label small fw-bold text-uppercase text-muted">País</label>
                  <input v-model="comprador.pais" class="form-control" placeholder="España" maxlength="50" />
                </div>
                <div class="col-md-6">
                  <label class="form-label small fw-bold text-uppercase text-muted">Email *</label>
                  <input v-model="comprador.email" type="email" class="form-control" placeholder="ejemplo@correo.com" required maxlength="100" />
                </div>
                <div class="col-md-6">
                  <label class="form-label small fw-bold text-uppercase text-muted">Teléfono *</label>
                  <input v-model="comprador.telefono" type="tel" class="form-control" placeholder="+34 600 000 000" required maxlength="20" />
                </div>
              </div>

              <h5 class="mb-3 text-muted border-bottom pb-2 d-flex justify-content-between align-items-center">
                Datos de Asistentes
                <small class="fw-normal fs-6 text-muted" v-if="totalEntradas > 0">Completa los datos para cada entrada</small>
              </h5>
              
              <div v-if="totalEntradas === 0" class="alert alert-light text-center border border-dashed py-4 text-muted">
                <i class="bi bi-ticket-perforated fs-3 d-block mb-2"></i>
                Selecciona entradas para añadir los datos de los asistentes.
              </div>
              
              <div v-else class="d-flex flex-column gap-3 mb-4">
                <template v-for="tipo in tipos" :key="tipo.idTipoEntrada">
                  <div v-for="(asistente, idx) in asistentes[tipo.idTipoEntrada]" :key="`${tipo.idTipoEntrada}-${idx}`" class="card bg-light border-0">
                    <div class="card-body">
                      <div class="d-flex align-items-center mb-3">
                        <span class="badge bg-dark me-2">{{ tipo.nombre }}</span>
                        <span class="text-muted small">Asistente #{{ idx + 1 }}</span>
                      </div>
                      <div class="row g-2">
                        <div class="col-md-6">
                          <input v-model="asistente.nombrePila" class="form-control form-control-sm" placeholder="Nombre *" required maxlength="50" />
                        </div>
                        <div class="col-md-6">
                          <input v-model="asistente.apellido1" class="form-control form-control-sm" placeholder="Primer Apellido *" required maxlength="50" />
                        </div>
                        <div class="col-md-6">
                          <input v-model="asistente.apellido2" class="form-control form-control-sm" placeholder="Segundo Apellido" maxlength="50" />
                        </div>
                        <div class="col-md-6">
                          <input v-model="asistente.dni" class="form-control form-control-sm" placeholder="DNI/Pasaporte *" required maxlength="15" />
                        </div>
                      </div>
                    </div>
                  </div>
                </template>
              </div>

              <div class="d-flex justify-content-between align-items-center pt-3 border-top">
                <p class="small text-muted mb-0">
                  <i class="bi bi-info-circle me-1"></i>
                  El pago se realizará presencialmente en taquilla.
                </p>
                <div class="d-flex gap-2">
                  <button type="button" class="btn btn-outline-secondary" @click="$router.back()">Cancelar</button>
                  <button type="submit" class="btn btn-primary px-4" :disabled="submitting">
                    <span v-if="submitting" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                    {{ submitting ? 'Procesando...' : 'Confirmar Reserva' }}
                  </button>
                </div>
              </div>
            </form>
          </div>
        </section>
      </div>
    </div>
  </div>
</template>

<script>
import ReservaRepository from '@/repositories/ReservaRepository';
import SesionRepository from '@/repositories/SesionRepository';
import EdicionRepository from '@/repositories/EdicionRepository';
import TipoEntradaRepository from '@/repositories/TipoEntradaRepository';
import { ESTADOS_SESION } from '@/constants';
import Swal from 'sweetalert2';

export default {
  name: 'ReservaEntradasView',
  data() {
    return {
      sesion: null,
      edicion: null,
      tipos: [],
      cantidades: {},
      asistentes: {}, // Map<idTipoEntrada, Array<Attendee>>
      comprador: {
        nombrePila: '',
        apellido1: '',
        apellido2: '',
        email: '',
        telefono: '',
        pais: ''
      },
      submitting: false,
      loading: true,
      error: ''
    };
  },
  computed: {
    totalEntradas() {
      return Object.values(this.cantidades).reduce((a, b) => a + b, 0);
    },
    totalPrecio() {
      return this.tipos.reduce((acc, t) => acc + (this.cantidades[t.idTipoEntrada] || 0) * (t.precio || 0), 0);
    },
    isMaxReached() {
      const aforoLibre = (this.sesion?.aforo || 0) - (this.sesion?.aforoOcupado || 0);
      return this.totalEntradas >= 10 || this.totalEntradas >= aforoLibre;
    },
    canSubmit() {
      const hasTickets = this.totalEntradas > 0;
      const buyerOk = this.comprador.nombrePila && this.comprador.apellido1 && this.comprador.email && this.comprador.telefono;
      
      // Validar todos los asistentes
      let asistentesOk = true;
      for (const tipo of this.tipos) {
        const list = this.asistentes[tipo.idTipoEntrada] || [];
        if (list.some(a => !a.nombrePila || !a.apellido1 || !a.dni)) {
          asistentesOk = false;
          break;
        }
      }

      const sesionDisponible = this.sesion?.estado === ESTADOS_SESION.DISPONIBLE;
      return hasTickets && buyerOk && asistentesOk && sesionDisponible;
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
        const idSesion = this.$route.params.id;
        const [sesion, tipos] = await Promise.all([
          SesionRepository.getById(idSesion),
          TipoEntradaRepository.getAll()
        ]);
        this.sesion = sesion;
        this.tipos = tipos || [];
        this.edicion = await EdicionRepository.getDetallePublic(sesion.idEdicion);
        
        // Inicializar asistentes array para cada tipo
        this.tipos.forEach(t => {
          this.asistentes[t.idTipoEntrada] = [];
        });
        console.log('ReservaEntradasView loaded', this.sesion);
      } catch (e) {
        console.error('Error loading reserva view', e);
        this.error = 'No se pudo cargar la sesión o los tipos de entrada.';
      } finally {
        this.loading = false;
      }
    },
    formatFecha(value) {
      if (!value) return '';
      if (Array.isArray(value)) return new Date(value[0], value[1] - 1, value[2]).toLocaleDateString();
      const d = new Date(value);
      return isNaN(d.getTime()) ? 'Fecha inválida' : d.toLocaleDateString();
    },
    formatHora(value) {
      if (!value) return '';
      if (Array.isArray(value)) {
        if (value.length === 2) return `${value[0].toString().padStart(2, '0')}:${value[1].toString().padStart(2, '0')}`;
        if (value.length >= 5) return `${value[3].toString().padStart(2, '0')}:${value[4].toString().padStart(2, '0')}`;
      }
      const d = new Date(value);
      return isNaN(d.getTime()) ? '' : d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    },
    formatPrice(value) {
      const v = Number(value || 0).toFixed(2);
      return `${v} €`;
    },
    salasTexto(salas = []) {
      return salas.length ? salas.map(s => s.nombre || s.idSala).join(', ') : 'Sin salas';
    },
    inc(idTipo) {
      if (this.isMaxReached) return;
      
      this.cantidades[idTipo] = (this.cantidades[idTipo] || 0) + 1;
      
      if (!this.asistentes[idTipo]) this.asistentes[idTipo] = [];
      this.asistentes[idTipo].push({ nombrePila: '', apellido1: '', apellido2: '', dni: '' });
    },
    dec(idTipo) {
      const current = this.cantidades[idTipo] || 0;
      if (current > 0) {
        this.cantidades[idTipo] = current - 1;
        if (this.asistentes[idTipo]) this.asistentes[idTipo].pop();
      }
    },
    buildPayload() {
      const entradas = [];
      this.tipos.forEach(tipo => {
        const list = this.asistentes[tipo.idTipoEntrada] || [];
        list.forEach(detalle => {
          entradas.push({
            idTipoEntrada: tipo.idTipoEntrada,
            nombrePila: detalle.nombrePila,
            apellido1: detalle.apellido1,
            apellido2: detalle.apellido2,
            dni: detalle.dni
          });
        });
      });
      return {
        comprador: { ...this.comprador },
        entradas
      };
    },
    validate() {
      if (this.totalEntradas <= 0) return 'Debes seleccionar al menos una entrada.';
      if (!this.comprador.nombrePila || !this.comprador.apellido1 || !this.comprador.email || !this.comprador.telefono) {
        return 'Por favor, completa todos los datos del comprador.';
      }
      
      for (const tipo of this.tipos) {
        const list = this.asistentes[tipo.idTipoEntrada] || [];
        if (list.some(a => !a.nombrePila || !a.apellido1 || !a.dni)) {
          return 'Por favor, completa los datos de todos los asistentes.';
        }
      }

      if (this.sesion?.estado !== ESTADOS_SESION.DISPONIBLE) {
        return 'Esta sesión no está disponible para reservas.';
      }
      return null;
    },
    async reservar() {
      console.log('Attempting to reserve...');
      const errorMsg = this.validate();
      if (errorMsg) {
        console.warn('Validation failed:', errorMsg);
        Swal.fire('Datos incompletos', errorMsg, 'warning');
        return;
      }

      this.submitting = true;
      this.error = '';
      try {
        const payload = this.buildPayload();
        await ReservaRepository.create(this.sesion.idSesion, payload);
        
        await Swal.fire('¡Reserva confirmada!', 'Te esperamos en el museo.', 'success');
        
        this.$router.push({ name: 'MisReservas' });
      } catch (e) {
        this.error = e.response?.data?.message || 'No se pudo completar la reserva.';
      } finally {
        this.submitting = false;
      }
    }
  }
};
</script>
