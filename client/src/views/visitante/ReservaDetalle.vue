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
        <div class="pill">{{ formatPrice(reserva.importeTotal || 0) }}</div>
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
          <button class="btn-primary" :disabled="!puedeCancelar" @click="cancelar">Cancelar reserva</button>
        </div>
      </section>
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
    return { reserva: null, entradas: [], loading: true, error: '' };
  },
  computed: {
    puedeCancelar() { return this.reserva?.estado === ESTADOS_RESERVA.CONFIRMADA; }
  },
  async created() { await this.load(); },
  methods: {
    async load() {
      this.loading = true;
      this.error = '';
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
    async cancelar() {
      if (!this.puedeCancelar) return;
      if (!confirm('¿Cancelar esta reserva?')) return;
      try {
        await ReservaRepository.cancelar(this.reserva.idReserva);
        await this.load();
      } catch (e) {
        setNotification('No se pudo cancelar la reserva.', 'error');
      }
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
      if (typeof v === 'string' && v.includes(':') && !v.includes('T')) {
        return v.substring(0, 5);
      }
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
.page { max-width: 1100px; margin: 0 auto; padding: 28px 18px 48px; display: flex; flex-direction: column; gap: 16px; }
.head { display: flex; justify-content: space-between; align-items: center; gap: 12px; }
.eyebrow { text-transform: uppercase; letter-spacing: .08em; font-size: 12px; color: #5b6472; margin: 0; }
.muted { color: #5b6472; margin: 0; }
.chip { padding: 6px 10px; border-radius: 999px; font-weight: 700; font-size: 12px; background: #eef1f6; color: #2a2f36; }
.chip-green { background: #e3f7e9; color: #1f7a3d; }
.chip-red { background: #fff0f0; color: #d23b3b; }
.chip-dark { background: #dfe2e7; color: #2a2f36; }
.chip-gray { background: #eef1f6; color: #4a5460; }
.center { display: flex; justify-content: center; padding: 30px 0; }
.empty { padding: 14px; background: #f6f8ff; border-radius: 10px; color: #4a5460; }
.grid { display: grid; grid-template-columns: repeat(auto-fit,minmax(320px,1fr)); gap: 12px; }
.card { background: #fff; border: 1px solid #e9ecf5; border-radius: 14px; padding: 14px; box-shadow: 0 8px 18px rgba(0,0,0,0.05); display: flex; flex-direction: column; gap: 10px; }
.pill { background: #eef1f6; padding: 6px 10px; border-radius: 10px; font-weight: 700; width: fit-content; }
.list { display: flex; flex-direction: column; gap: 10px; }
.entrada { border: 1px solid #eef1f6; border-radius: 12px; padding: 10px; display: flex; justify-content: space-between; gap: 10px; align-items: center; }
.body { color: #2f3540; margin: 0; }
.btn-ghost { border: 1px solid #d9deea; background: #fff; color: #1f4b99; padding: 8px 12px; border-radius: 10px; font-weight: 700; cursor: pointer; text-decoration: none; }
.actions { display: flex; justify-content: flex-end; }
.btn-primary { border: none; background: linear-gradient(135deg,#1f4b99,#153a7a); color: #fff; padding: 10px 14px; border-radius: 10px; font-weight: 700; cursor: pointer; }
</style>
