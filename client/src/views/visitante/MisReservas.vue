<template>
  <div class="page">
    <header class="head">
      <h1>Mis reservas</h1>
      <div class="filters">
        <label>
          Estado
          <select v-model="estado" @change="load">
            <option value="">Todas</option>
            <option value="CONFIRMADA">Confirmada</option>
            <option value="FINALIZADA">Finalizada</option>
            <option value="CANCELADA">Cancelada</option>
          </select>
        </label>
      </div>
    </header>

    <div v-if="loading" class="center"><div class="spinner-border" role="status"></div></div>
    <div v-else-if="error" class="alert alert-danger">{{ error }}</div>
    <div v-else-if="reservas.length === 0" class="empty">No tienes reservas todavía.</div>
    <div v-else class="grid">
      <article v-for="reserva in reservas" :key="reserva.idReserva" class="card">
        <div class="card-top">
          <div>
            <p class="eyebrow">Reserva #{{ reserva.idReserva }}</p>
            <h3>{{ reserva.exposicion?.titulo || 'Exposición' }}</h3>
            <p class="muted">{{ formatFecha(reserva.sesion?.fecha) }} · {{ formatHora(reserva.sesion?.horaInicio) }}</p>
          </div>
          <span class="chip" :class="stateClass(reserva.estado)">{{ reserva.estado }}</span>
        </div>
        <p class="muted">{{ reserva.edicion?.nombre || reserva.edicion?.idEdicion }}</p>
        <div class="pill">{{ reserva.entradas?.length || reserva.numEntradas || 0 }} entradas · {{ formatPrice(reserva.importeTotal || 0) }}</div>
        <div class="actions">
          <router-link :to="{ name: 'ReservaDetalle', params: { idReserva: reserva.idReserva } }" class="btn-ghost">Ver detalle</router-link>
          <button class="btn-primary" :disabled="!puedeCancelar(reserva)" @click="cancelar(reserva.idReserva)">Cancelar</button>
        </div>
      </article>
    </div>
  </div>
</template>

<script>
import ReservaRepository from '@/repositories/ReservaRepository';
import { ESTADOS_RESERVA } from '@/constants';

export default {
  name: 'MisReservas',
  data() {
    return { reservas: [], estado: '', loading: false, error: '' };
  },
  async created() { await this.load(); },
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
    formatFecha(v) { return v ? new Date(v).toLocaleDateString() : ''; },
    formatHora(v) { return v ? new Date(v).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : ''; },
    formatPrice(v) { return `${Number(v || 0).toFixed(2)} €`; },
    stateClass(estado) {
      return {
        [ESTADOS_RESERVA.CONFIRMADA]: 'chip-green',
        [ESTADOS_RESERVA.CANCELADA]: 'chip-red',
        [ESTADOS_RESERVA.FINALIZADA]: 'chip-dark'
      }[estado] || 'chip-gray';
    },
    puedeCancelar(reserva) { return reserva.estado === ESTADOS_RESERVA.CONFIRMADA; }
  }
};
</script>

<style scoped>
.page { max-width: 1100px; margin: 0 auto; padding: 28px 18px 48px; display: flex; flex-direction: column; gap: 16px; }
.head { display: flex; justify-content: space-between; align-items: center; }
.filters { display: flex; gap: 12px; }
select, input { border: 1px solid #d9deea; border-radius: 10px; padding: 8px; }
.center { display: flex; justify-content: center; padding: 30px 0; }
.empty { padding: 14px; background: #f6f8ff; border-radius: 10px; color: #4a5460; }
.grid { display: grid; grid-template-columns: repeat(auto-fit,minmax(280px,1fr)); gap: 12px; }
.card { background: #fff; border: 1px solid #e9ecf5; border-radius: 14px; padding: 14px; box-shadow: 0 8px 18px rgba(0,0,0,0.05); display: flex; flex-direction: column; gap: 10px; }
.card-top { display: flex; justify-content: space-between; gap: 10px; }
.eyebrow { text-transform: uppercase; letter-spacing: .08em; font-size: 12px; color: #5b6472; margin: 0; }
.muted { color: #5b6472; margin: 0; }
.pill { background: #eef1f6; padding: 6px 10px; border-radius: 10px; font-weight: 700; width: fit-content; }
.chip { padding: 6px 10px; border-radius: 999px; font-weight: 700; font-size: 12px; background: #eef1f6; color: #2a2f36; }
.chip-green { background: #e3f7e9; color: #1f7a3d; }
.chip-red { background: #fff0f0; color: #d23b3b; }
.chip-dark { background: #dfe2e7; color: #2a2f36; }
.chip-gray { background: #eef1f6; color: #4a5460; }
.actions { display: flex; gap: 10px; }
.btn-primary { border: none; background: linear-gradient(135deg,#1f4b99,#153a7a); color: #fff; padding: 10px 14px; border-radius: 10px; font-weight: 700; cursor: pointer; }
.btn-ghost { border: 1px solid #d9deea; background: #fff; color: #1f4b99; padding: 10px 14px; border-radius: 10px; font-weight: 700; cursor: pointer; text-decoration: none; }
</style>
