<template>
  <div class="page">
    <header class="head">
      <h1>Mis entradas</h1>
      <p class="muted">Listado de entradas asociadas a tus reservas.</p>
    </header>

    <div v-if="loading" class="center"><div class="spinner-border" role="status"></div></div>
    <div v-else-if="error" class="alert alert-danger">{{ error }}</div>
    <div v-else-if="entradas.length === 0" class="empty">No tienes entradas emitidas.</div>
    <div v-else class="grid">
      <article v-for="entrada in entradas" :key="entrada.idEntrada" class="card">
        <div class="card-top">
          <div>
            <p class="eyebrow">Entrada #{{ entrada.idEntrada }}</p>
            <h4>{{ entrada.nombreExposicion || 'Exposición' }}</h4>
            <p class="muted">{{ formatFecha(entrada.fechaHoraSesion) }} · {{ formatHora(entrada.fechaHoraSesion) }}</p>
            <p class="muted">Asistente: {{ entrada.nombreCompletoAsistente }}</p>
          </div>
          <span class="chip" :class="stateClass(entrada.estadoReserva)">{{ entrada.estadoReserva }}</span>
        </div>
        <p class="pill">{{ entrada.tipoEntrada || 'Tipo' }} · {{ formatPrice(entrada.precio) }}</p>
        <div class="actions">
          <router-link class="btn-ghost" :to="{ name: 'EntradaDetalle', params: { idEntrada: entrada.idEntrada } }">Ver detalle</router-link>
          <router-link class="btn-link" :to="{ name: 'ReservaDetalle', params: { idReserva: entrada.idReserva } }">Ver reserva</router-link>
        </div>
      </article>
    </div>
  </div>
</template>

<script>
import EntradaRepository from '@/repositories/EntradaRepository';
import { ESTADOS_RESERVA } from '@/constants';

export default {
  name: 'MisEntradas',
  data() {
    return { entradas: [], loading: true, error: '' };
  },
  async created() {
    await this.load();
  },
  methods: {
    async load() {
      this.loading = true; this.error = '';
      try {
        this.entradas = await EntradaRepository.getMisEntradas();
      } catch (e) {
        this.error = 'No se pudieron cargar tus entradas.';
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
.page { max-width: 1100px; margin: 0 auto; padding: 28px 18px 48px; display: flex; flex-direction: column; gap: 16px; }
.head { display: flex; flex-direction: column; gap: 4px; }
.muted { color: #5b6472; margin: 0; font-size: 0.95rem; }
.center { display: flex; justify-content: center; padding: 30px 0; }
.empty { padding: 14px; background: #f6f8ff; border-radius: 10px; color: #4a5460; }
.grid { display: grid; grid-template-columns: repeat(auto-fit,minmax(280px,1fr)); gap: 12px; }

.card { background: #fff; border: 1px solid #e9ecf5; border-radius: 14px; padding: 14px; box-shadow: 0 4px 12px rgba(0,0,0,0.03); display: flex; flex-direction: column; gap: 10px; }

/* FIX: align-items: flex-start evita que el chip se estire verticalmente */
.card-top { display: flex; justify-content: space-between; gap: 10px; align-items: flex-start; }

.eyebrow { text-transform: uppercase; letter-spacing: .08em; font-size: 11px; color: #6c757d; margin: 0; font-weight: 600; }

/* CHIP REDISEÑADO: Más sutil, borde suave, fuente ajustada */
.chip {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 4px 8px;
  border-radius: 6px;
  font-weight: 600;
  font-size: 11px;
  line-height: 1;
  white-space: nowrap;
  border: 1px solid transparent;
}
.chip-green { background: #f0fdf4; color: #166534; border-color: #dcfce7; }
.chip-red { background: #fef2f2; color: #991b1b; border-color: #fee2e2; }
.chip-dark { background: #f3f4f6; color: #374151; border-color: #e5e7eb; }
.chip-gray { background: #f9fafb; color: #6b7280; border-color: #e5e7eb; }

.pill { background: #f8f9fa; color: #495057; padding: 6px 10px; border-radius: 8px; font-weight: 600; font-size: 13px; width: fit-content; border: 1px solid #e9ecef; }

.actions { display: flex; gap: 10px; align-items: center; margin-top: auto; }
.btn-ghost { border: 1px solid #dee2e6; background: #fff; color: #1f4b99; padding: 6px 12px; border-radius: 8px; font-weight: 600; font-size: 13px; cursor: pointer; text-decoration: none; transition: all 0.2s; }
.btn-ghost:hover { background: #f8f9fa; border-color: #cdd4db; }
.btn-link { color: #1f4b99; text-decoration: none; font-weight: 600; font-size: 13px; }
.btn-link:hover { text-decoration: underline; }
</style>
