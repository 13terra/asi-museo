<template>
  <div class="page">
    <header class="head" v-if="reserva">
      <div>
        <p class="eyebrow">Reserva #{{ reserva.idReserva }}</p>
        <h1>{{ reserva.nombreExposicion || 'Exposición' }}</h1>
        <p class="muted">{{ formatFecha(reserva.fechaHoraSesion) }} · {{ formatHora(reserva.fechaHoraSesion) }}</p>
      </div>
      <span class="chip" :class="stateClass(reserva.estado)">{{ reserva.estado }}</span>
    </header>

    <div v-if="loading" class="center"><div class="spinner-border" role="status"></div></div>
    <div v-else-if="error" class="alert alert-danger">{{ error }}</div>
    <div v-else-if="!reserva" class="empty">Reserva no encontrada.</div>
    <div v-else class="grid">
      <section class="card">
        <h3>Datos del comprador</h3>
        <p class="body">{{ reserva.nombrePila }} {{ reserva.apellido1 }} {{ reserva.apellido2 }}</p>
        <p class="muted">{{ reserva.email }} · {{ reserva.telefono }} · {{ reserva.pais }}</p>
        <div class="pill price-pill">{{ formatPrice(reserva.importeTotal || 0) }}</div>
      </section>

      <section class="card">
        <h3>Entradas</h3>
        <div v-if="entradas.length === 0" class="empty">Sin entradas asociadas.</div>
        <div class="list" v-else>
          <article v-for="entrada in entradas" :key="entrada.idEntrada" class="entrada">
            <div>
              <strong>#{{ entrada.idEntrada }}</strong>
              <p class="muted">{{ entrada.tipoEntrada }} · {{ formatPrice(entrada.precio) }}</p>
              <p class="body">{{ entrada.nombreCompletoAsistente }} · {{ entrada.dni }}</p>
            </div>
            <router-link :to="{ name: 'EntradaDetalle', params: { idEntrada: entrada.idEntrada } }" class="btn-ghost">Ver entrada</router-link>
          </article>
        </div>
        <div class="actions">
          <button class="btn-danger" :disabled="!puedeCancelar" @click="pedirConfirmacion">Cancelar reserva</button>
        </div>
      </section>
    </div>

    <div v-if="showModal" class="modal-overlay">
      <div class="modal-box">
        <h3>¿Cancelar reserva?</h3>
        <p>Esta acción es irreversible y anulará todas las entradas asociadas. ¿Estás seguro?</p>
        <div class="modal-actions">
          <button class="btn-ghost" @click="showModal = false">No, volver</button>
          <button class="btn-danger" @click="confirmarCancelacion">Sí, cancelar</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import ReservaRepository from '@/repositories/ReservaRepository';
import EntradaRepository from '@/repositories/EntradaRepository';
import { ESTADOS_RESERVA } from '@/constants';
import { setNotification } from '@/common/store';

export default {
  name: 'ReservaDetalle',
  data() {
    return {
      reserva: null,
      entradas: [],
      loading: true,
      error: '',
      showModal: false // Controla la visibilidad del modal
    };
  },
  computed: {
    puedeCancelar() { return this.reserva?.estado === ESTADOS_RESERVA.CONFIRMADA; }
  },
  async created() { await this.load(); },
  methods: {
    async load() {
      this.loading = true; this.error = '';
      try {
        const id = this.$route.params.idReserva || this.$route.params.id;
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
    pedirConfirmacion() {
      if (!this.puedeCancelar) return;
      this.showModal = true; // Abre el modal en lugar del window.confirm
    },
    async confirmarCancelacion() {
      this.showModal = false; // Cierra el modal
      try {
        await ReservaRepository.cancelar(this.reserva.idReserva);
        setNotification('Reserva cancelada correctamente.', 'success');
        await this.load(); // Recarga para ver el nuevo estado
      } catch (e) {
        // LÓGICA DE ERROR AMIGABLE
        // Calculamos si faltan menos de 24h para mostrar el mensaje correcto
        const fechaSesion = this.parseFechaJava(this.reserva.fechaHoraSesion);
        const ahora = new Date();
        const horasRestantes = (fechaSesion - ahora) / (1000 * 60 * 60);

        if (horasRestantes < 24 && horasRestantes > -5) { // -5 margen por si ya pasó
          setNotification('No es posible cancelar: faltan menos de 24 horas para la visita.', 'error');
        } else {
          setNotification('Ocurrió un error al intentar cancelar la reserva.', 'error');
        }
      }
    },
    // Convierte el array [2025, 12, 28, 10, 0] de Java a Date JS
    parseFechaJava(v) {
      if (!Array.isArray(v)) return new Date(v);
      // Mes en JS es 0-11
      return new Date(v[0], v[1] - 1, v[2], v[3] || 0, v[4] || 0);
    },
    formatFecha(v) {
      if (!v) return '';
      if (Array.isArray(v)) return new Date(v[0], v[1] - 1, v[2]).toLocaleDateString();
      return new Date(v).toLocaleDateString();
    },
    formatHora(v) {
      if (!v) return '';
      if (Array.isArray(v)) {
        if (v.length === 2) return `${v[0].toString().padStart(2, '0')}:${v[1].toString().padStart(2, '0')}`;
        if (v.length >= 5) return `${v[3].toString().padStart(2, '0')}:${v[4].toString().padStart(2, '0')}`;
      }
      if (typeof v === 'string' && v.includes(':') && !v.includes('T')) return v.substring(0, 5);
      return new Date(v).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    },
    formatPrice(v) { return `${Number(v || 0).toFixed(2)} €`; },
    stateClass(estado) {
      return {
        [ESTADOS_RESERVA.CONFIRMADA]: 'chip-green',
        [ESTADOS_RESERVA.CANCELADA]: 'chip-red',
        [ESTADOS_RESERVA.FINALIZADA]: 'chip-dark'
      }[estado] || 'chip-gray';
    }
  }
};
</script>

<style scoped>
.page { max-width: 1100px; margin: 0 auto; padding: 28px 18px 48px; display: flex; flex-direction: column; gap: 20px; }
.head { display: flex; justify-content: space-between; align-items: flex-start; gap: 12px; }
.eyebrow { text-transform: uppercase; letter-spacing: .05em; font-size: 11px; color: #94a3b8; margin: 0; font-weight: 700; }
.muted { color: #64748b; margin: 0; }

/* CHIPS (Estilo unificado) */
.chip {
  font-size: 11px; font-weight: 700; padding: 4px 8px; border-radius: 6px;
  text-transform: uppercase; letter-spacing: 0.03em; border: 1px solid transparent;
}
.chip-green { background-color: #f0fdf4; color: #15803d; border-color: #bbf7d0; }
.chip-red { background-color: #fef2f2; color: #b91c1c; border-color: #fecaca; }
.chip-dark { background-color: #f1f5f9; color: #475569; border-color: #e2e8f0; }
.chip-gray { background-color: #f8fafc; color: #64748b; border-color: #e2e8f0; }

.center { display: flex; justify-content: center; padding: 30px 0; }
.empty { padding: 14px; background: #f8fafc; border-radius: 10px; color: #64748b; border: 1px dashed #cbd5e1; }
.grid { display: grid; grid-template-columns: repeat(auto-fit,minmax(350px,1fr)); gap: 20px; }

.card { background: #fff; border: 1px solid #e2e8f0; border-radius: 12px; padding: 20px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); display: flex; flex-direction: column; gap: 14px; }
.card h3 { margin: 0 0 10px 0; font-size: 1.1rem; color: #0f172a; }

.pill { background: #f1f5f9; padding: 6px 12px; border-radius: 8px; font-weight: 600; font-size: 14px; color: #334155; display: inline-block; }
.price-pill { font-size: 1.2rem; color: #0f172a; }

.list { display: flex; flex-direction: column; gap: 12px; }
.entrada { border: 1px solid #f1f5f9; border-radius: 10px; padding: 12px; display: flex; justify-content: space-between; gap: 12px; align-items: center; transition: background 0.2s; }
.entrada:hover { background: #f8fafc; }
.body { color: #334155; margin: 0; font-size: 14px; font-weight: 500; }

.btn-ghost { border: 1px solid #cbd5e1; background: #fff; color: #475569; padding: 8px 14px; border-radius: 8px; font-weight: 600; cursor: pointer; text-decoration: none; transition: all 0.2s; }
.btn-ghost:hover { background: #f1f5f9; border-color: #94a3b8; }

.actions { display: flex; justify-content: flex-end; margin-top: 10px; }
.btn-danger { border: none; background: #ef4444; color: #fff; padding: 10px 18px; border-radius: 8px; font-weight: 600; cursor: pointer; transition: background 0.2s; box-shadow: 0 2px 4px rgba(239, 68, 68, 0.2); }
.btn-danger:hover { background: #dc2626; }
.btn-danger:disabled { background: #e2e8f0; color: #94a3b8; cursor: not-allowed; box-shadow: none; }

/* ESTILOS DEL MODAL */
.modal-overlay {
  position: fixed; top: 0; left: 0; width: 100%; height: 100%;
  background: rgba(15, 23, 42, 0.6); backdrop-filter: blur(2px);
  display: flex; justify-content: center; align-items: center; z-index: 1000;
}
.modal-box {
  background: white; padding: 24px; border-radius: 16px; width: 90%; max-width: 400px;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
  text-align: center; animation: popIn 0.2s ease-out;
}
.modal-box h3 { margin-top: 0; color: #0f172a; }
.modal-box p { color: #64748b; line-height: 1.5; margin-bottom: 24px; }
.modal-actions { display: flex; justify-content: center; gap: 12px; }

@keyframes popIn {
  from { transform: scale(0.95); opacity: 0; }
  to { transform: scale(1); opacity: 1; }
}
</style>
