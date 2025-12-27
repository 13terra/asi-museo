<template>
  <div class="page" v-if="!loading && entrada">
    <header class="head">
      <div>
        <p class="eyebrow">Entrada #{{ entrada.idEntrada }}</p>
        <h1>{{ entrada.tipoEntrada || 'Entrada' }}</h1>
        <p class="muted">Reserva #{{ entrada.idReserva }} · {{ formatFecha(entrada.fechaHoraSesion) }}</p>
      </div>
      <span class="chip" :class="stateClass(entrada.estadoReserva)">{{ entrada.estadoReserva }}</span>
    </header>

    <section class="card">
      <h3>Datos del asistente</h3>
      <p class="body">{{ entrada.nombrePila }} {{ entrada.apellido1 }} {{ entrada.apellido2 }}</p>
      <p class="muted">DNI: {{ entrada.dni || 'N/D' }}</p>
    </section>

    <section class="card">
      <h3>Datos de la sesión</h3>
      <p class="body">{{ entrada.nombreExposicion || 'Exposición' }}</p>
      <p class="muted">{{ formatFecha(entrada.fechaHoraSesion) }} · {{ formatHora(entrada.fechaHoraSesion) }} - {{ formatHora(entrada.horaFinSesion) }}</p>
      <p class="muted">Salas: {{ entrada.nombresSalas || 'Sin salas' }}</p>
    </section>

    <section class="card">
      <h3>Precio</h3>
      <p class="pill">{{ formatPrice(entrada.precio) }}</p>
    </section>
  </div>
  <div v-else class="center"><div class="spinner-border" role="status"></div></div>
</template>

<script>
import EntradaRepository from '@/repositories/EntradaRepository';
import { ESTADOS_RESERVA } from '@/constants';

export default {
  name: 'EntradaDetalle',
  data() {
    return { entrada: null, loading: true, error: '' };
  },
  async created() {
    await this.load();
  },
  methods: {
    async load() {
      this.loading = true; this.error = '';
      try {
        const id = this.$route.params.idEntrada || this.$route.params.id;
        this.entrada = await EntradaRepository.getById(id);
      } catch (e) {
        this.error = 'No se pudo cargar la entrada.';
      } finally {
        this.loading = false;
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
.page { max-width: 900px; margin: 0 auto; padding: 28px 18px 48px; display: flex; flex-direction: column; gap: 14px; }
.head { display: flex; justify-content: space-between; align-items: center; gap: 12px; }
.eyebrow { text-transform: uppercase; letter-spacing: .08em; font-size: 12px; color: #5b6472; margin: 0; }
.muted { color: #5b6472; margin: 0; }
.body { color: #2f3540; margin: 0; }
.chip { padding: 6px 10px; border-radius: 999px; font-weight: 700; font-size: 12px; background: #eef1f6; color: #2a2f36; }
.chip-green { background: #e3f7e9; color: #1f7a3d; }
.chip-red { background: #fff0f0; color: #d23b3b; }
.chip-dark { background: #dfe2e7; color: #2a2f36; }
.chip-gray { background: #eef1f6; color: #4a5460; }
.card { background: #fff; border: 1px solid #e9ecf5; border-radius: 14px; padding: 16px; box-shadow: 0 8px 18px rgba(0,0,0,0.05); display: flex; flex-direction: column; gap: 8px; }
.pill { padding: 6px 10px; border-radius: 10px; background: #eef1f6; font-weight: 700; width: fit-content; }
.center { display: flex; justify-content: center; padding: 30px 0; }
</style>
