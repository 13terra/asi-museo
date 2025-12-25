<template>
  <div class="page">
    <header class="hero" v-if="edicion">
      <div>
        <p class="eyebrow">{{ edicion.exposicion?.titulo || 'Exposición' }}</p>
        <h1>{{ edicion.nombre || `Edición ${edicion.idEdicion}` }}</h1>
        <p class="muted">{{ edicion.exposicion?.descripcion || 'Sin descripción' }}</p>
        <div class="chips">
          <span class="chip" :class="stateClass(edicion.estado)">{{ edicion.estado }}</span>
          <span class="chip">{{ rangoFechas }}</span>
        </div>
      </div>
      <div class="cta-box">
        <h4>Sesiones disponibles</h4>
        <p class="muted">Reserva tus entradas en un par de clics.</p>
        <button class="btn-primary" :disabled="!haySesionesDisponibles" @click="goFirstDisponible">
          Reservar ahora
        </button>
        <p v-if="!haySesionesDisponibles" class="small muted">No hay sesiones disponibles.</p>
      </div>
    </header>

    <section class="section">
      <div class="section-head">
        <h3>Sesiones</h3>
        <span class="muted">{{ sesiones.length }} resultados</span>
      </div>
      <div v-if="loading" class="center"><div class="spinner-border" role="status"></div></div>
      <div v-else-if="error" class="alert alert-danger">{{ error }}</div>
      <div v-else class="session-grid">
        <article v-for="sesion in sesiones" :key="sesion.idSesion" class="card">
          <div class="card-top">
            <div>
              <p class="eyebrow">{{ formatFecha(sesion.fecha) }}</p>
              <h4>{{ formatHora(sesion.horaInicio) }} · {{ formatHora(sesion.horaFin) }}</h4>
              <p class="muted">Salas: {{ salasTexto(sesion.salas) }}</p>
            </div>
            <span class="chip" :class="sesionStateClass(sesion.estado)">{{ sesion.estado }}</span>
          </div>
          <div class="pill">Aforo: {{ sesion.aforoOcupado ?? sesion.ocupado ?? 0 }} / {{ sesion.aforo }}</div>
          <div class="actions">
            <button class="btn-primary" :disabled="sesion.estado !== estadosSesion.DISPONIBLE" @click="irReserva(sesion.idSesion)">
              Reservar entradas
            </button>
          </div>
        </article>
      </div>
    </section>

    <section class="section">
      <div class="section-head">
        <h3>Obras expuestas</h3>
        <span class="muted">Agrupadas por sala</span>
      </div>
      <div v-if="piezasPorSalaKeys.length === 0" class="empty">Aún no hay piezas expuestas.</div>
      <div v-else class="sala-stack">
        <div v-for="sala in piezasPorSalaKeys" :key="sala" class="sala-block">
          <h5>{{ sala }}</h5>
          <div class="pieza-grid">
            <article v-for="pieza in piezasPorSala[sala]" :key="pieza.idPiezaExpuesta" class="card pieza">
              <div class="pieza-header">
                <span class="badge" v-if="pieza.portada">Portada</span>
                <span class="muted">Orden {{ pieza.orden }}</span>
              </div>
              <h4>{{ pieza.obra?.titulo || 'Obra' }}</h4>
              <p class="muted">{{ pieza.obra?.autor }}</p>
              <p class="body">{{ pieza.textoCuratorial || 'Sin texto curatorial' }}</p>
            </article>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
import EdicionRepository from '@/repositories/EdicionRepository';
import SesionRepository from '@/repositories/SesionRepository';
import PiezaExpuestaRepository from '@/repositories/PiezaExpuestaRepository';
import { ESTADOS_EDICION, ESTADOS_SESION } from '@/constants';

export default {
  name: 'EdicionPublicaDetalle',
  data() {
    return { edicion: null, sesiones: [], piezas: [], loading: true, error: '' };
  },
  computed: {
    estadosSesion() { return ESTADOS_SESION; },
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
    piezasPorSalaKeys() { return Object.keys(this.piezasPorSala); },
    haySesionesDisponibles() { return this.sesiones.some(s => s.estado === ESTADOS_SESION.DISPONIBLE); }
  },
  async created() {
    await this.load();
  },
  methods: {
    async load() {
      this.loading = true;
      this.error = '';
      try {
        const idEdicion = this.$route.params.idEdicion || this.$route.params.id;
        const [edicion, sesiones, piezas] = await Promise.all([
          EdicionRepository.getDetallePublic(idEdicion),
          SesionRepository.getByEdicion(idEdicion),
          PiezaExpuestaRepository.getByEdicion(idEdicion)
        ]);
        this.edicion = { ...edicion, estado: edicion.estado || edicion.estadoEdicion };
        this.sesiones = sesiones || [];
        this.piezas = piezas || [];
      } catch (e) {
        this.error = 'No se pudo cargar la edición.';
      } finally {
        this.loading = false;
      }
    },
    formatFecha(value) { return value ? new Date(value).toLocaleDateString() : ''; },
    formatHora(value) { return value ? new Date(value).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : ''; },
    stateClass(estado) {
      return {
        [ESTADOS_EDICION.BORRADOR]: 'chip-gray',
        [ESTADOS_EDICION.PUBLICADA]: 'chip-green',
        [ESTADOS_EDICION.FINALIZADA]: 'chip-dark',
        [ESTADOS_EDICION.CANCELADA]: 'chip-red'
      }[estado] || 'chip-gray';
    },
    sesionStateClass(estado) {
      return {
        [ESTADOS_SESION.DISPONIBLE]: 'chip-green',
        [ESTADOS_SESION.COMPLETA]: 'chip-dark',
        [ESTADOS_SESION.CANCELADA]: 'chip-red'
      }[estado] || 'chip-gray';
    },
    salasTexto(salas = []) { return salas.length ? salas.map(s => s.nombre || s.idSala).join(', ') : 'Sin salas'; },
    irReserva(idSesion) { this.$router.push({ name: 'ReservarEntradas', params: { idSesion } }); },
    goFirstDisponible() {
      const sesion = this.sesiones.find(s => s.estado === ESTADOS_SESION.DISPONIBLE);
      if (sesion) this.irReserva(sesion.idSesion);
    }
  }
};
</script>

<style scoped>
.page { max-width: 1200px; margin: 0 auto; padding: 32px 20px 48px; display: flex; flex-direction: column; gap: 28px; }
.hero { display: grid; grid-template-columns: 1.6fr 1fr; gap: 20px; align-items: center; background: linear-gradient(135deg, #f6f8ff, #eef2fb); border: 1px solid #e0e5f4; border-radius: 16px; padding: 18px 20px; }
.eyebrow { text-transform: uppercase; letter-spacing: .08em; font-size: 12px; color: #5b6472; margin: 0; }
.muted { color: #5b6472; margin: 0; }
.chips { display: flex; gap: 10px; margin-top: 6px; }
.chip { padding: 6px 10px; border-radius: 999px; font-weight: 700; font-size: 12px; background: #eef1f6; color: #2a2f36; }
.chip-green { background: #e3f7e9; color: #1f7a3d; }
.chip-gray { background: #eef1f6; color: #4a5460; }
.chip-dark { background: #dfe2e7; color: #2a2f36; }
.chip-red { background: #fff0f0; color: #d23b3b; }
.cta-box { background: #fff; border: 1px solid #e5e9f4; border-radius: 12px; padding: 14px; display: flex; flex-direction: column; gap: 8px; box-shadow: 0 6px 16px rgba(0,0,0,0.05); }
.btn-primary { border: none; background: linear-gradient(135deg,#1f4b99,#153a7a); color: #fff; padding: 10px 14px; border-radius: 10px; font-weight: 700; cursor: pointer; }
.section { display: flex; flex-direction: column; gap: 12px; }
.section-head { display: flex; justify-content: space-between; align-items: center; }
.session-grid { display: grid; grid-template-columns: repeat(auto-fit,minmax(280px,1fr)); gap: 12px; }
.card { background: #fff; border: 1px solid #e9ecf5; border-radius: 14px; padding: 14px; box-shadow: 0 8px 18px rgba(0,0,0,0.05); display: flex; flex-direction: column; gap: 10px; }
.card-top { display: flex; justify-content: space-between; gap: 10px; }
.pill { background: #eef1f6; padding: 6px 10px; border-radius: 10px; font-weight: 700; width: fit-content; }
.actions { display: flex; justify-content: flex-end; }
.sala-stack { display: flex; flex-direction: column; gap: 12px; }
.sala-block { border: 1px solid #e9ecf5; border-radius: 12px; padding: 12px; background: #fff; }
.pieza-grid { display: grid; grid-template-columns: repeat(auto-fit,minmax(220px,1fr)); gap: 10px; }
.pieza { gap: 6px; }
.pieza-header { display: flex; justify-content: space-between; gap: 8px; align-items: center; }
.badge { padding: 6px 8px; border-radius: 8px; background: #eef1f6; font-weight: 700; font-size: 12px; }
.empty { padding: 14px; background: #f6f8ff; border-radius: 10px; color: #4a5460; }
.center { display: flex; justify-content: center; padding: 30px 0; }
</style>
