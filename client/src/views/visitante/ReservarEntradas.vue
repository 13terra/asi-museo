<template>
  <div class="page">
    <header class="header" v-if="sesion && edicion">
      <div>
        <p class="eyebrow">{{ edicion.exposicion?.titulo || 'Exposición' }}</p>
        <h1>Reserva para {{ formatFecha(sesion.fecha) }}</h1>
        <p class="muted">{{ formatHora(sesion.horaInicio) }} · {{ formatHora(sesion.horaFin) }} · Salas {{ salasTexto(sesion.salas) }}</p>
      </div>
      <div class="pill">Aforo: {{ sesion.aforoOcupado ?? 0 }} / {{ sesion.aforo }}</div>
    </header>

    <div v-if="loading" class="center"><div class="spinner-border" role="status"></div></div>
    <div v-else-if="error" class="alert alert-danger">{{ error }}</div>
    <div v-else class="grid">
      <section class="card">
        <h3>Entradas</h3>
        <div class="ticket-list">
          <div v-for="tipo in tipos" :key="tipo.idTipoEntrada" class="ticket-row">
            <div>
              <strong>{{ tipo.nombre }}</strong>
              <p class="muted">{{ tipo.descripcion || 'Sin descripción' }}</p>
            </div>
            <div class="price">{{ formatPrice(tipo.precio) }}</div>
            <div class="qty">
              <button @click="dec(tipo.idTipoEntrada)">-</button>
              <span>{{ cantidades[tipo.idTipoEntrada] || 0 }}</span>
              <button @click="inc(tipo.idTipoEntrada)">+</button>
            </div>
          </div>
        </div>
        <div class="summary">
          <span>Total entradas: {{ totalEntradas }}</span>
          <strong>{{ formatPrice(totalPrecio) }}</strong>
        </div>
      </section>

      <section class="card form">
        <h3>Datos del comprador</h3>
        <div class="form-grid">
          <label>Nombre<input v-model="comprador.nombrePila" /></label>
          <label>Apellido 1<input v-model="comprador.apellido1" /></label>
          <label>Apellido 2<input v-model="comprador.apellido2" /></label>
          <label>Email<input v-model="comprador.email" type="email" /></label>
          <label>Teléfono<input v-model="comprador.telefono" /></label>
          <label>País<input v-model="comprador.pais" /></label>
        </div>

        <div class="asistentes">
          <h4>Datos de asistentes</h4>
          <p class="muted">Introduce nombre y DNI para cada entrada.</p>
          <div v-if="totalEntradas === 0" class="empty">Añade entradas para completar los datos.</div>
          <div v-else class="assist-grid">
            <div v-for="(entrada, index) in entradasDetalle" :key="index" class="assist-card">
              <div class="muted">{{ entrada.tipo.nombre }}</div>
              <label>Nombre<input v-model="entrada.nombrePila" /></label>
              <label>Apellido 1<input v-model="entrada.apellido1" /></label>
              <label>Apellido 2<input v-model="entrada.apellido2" /></label>
              <label>DNI<input v-model="entrada.dni" /></label>
            </div>
          </div>
        </div>

        <div class="actions">
          <button class="btn-ghost" @click="$router.back()">Cancelar</button>
          <button class="btn-primary" :disabled="!canSubmit || submitting" @click="reservar">
            {{ submitting ? 'Procesando...' : 'Reservar' }}
          </button>
        </div>
        <p class="note">El pago se realizará presencialmente al hacer check-in.</p>
      </section>
    </div>
  </div>
</template>

<script>
import ReservaRepository from '@/repositories/ReservaRepository';
import SesionRepository from '@/repositories/SesionRepository';
import EdicionRepository from '@/repositories/EdicionRepository';
import TipoEntradaRepository from '@/repositories/TipoEntradaRepository';
import { ESTADOS_SESION } from '@/constants';

export default {
  name: 'ReservarEntradas',
  data() {
    return {
      sesion: null,
      edicion: null,
      tipos: [],
      cantidades: {},
      comprador: { nombrePila: '', apellido1: '', apellido2: '', email: '', telefono: '', pais: '' },
      submitting: false,
      loading: true,
      error: ''
    };
  },
  computed: {
    totalEntradas() { return Object.values(this.cantidades).reduce((a, b) => a + b, 0); },
    totalPrecio() { return this.tipos.reduce((acc, t) => acc + (this.cantidades[t.idTipoEntrada] || 0) * (t.precio || 0), 0); },
    entradasDetalle() {
      const list = [];
      this.tipos.forEach(tipo => {
        const count = this.cantidades[tipo.idTipoEntrada] || 0;
        for (let i = 0; i < count; i++) {
          const idx = list.length;
          const detalle = this.entradasDetalleBase[idx];
          list.push({ tipo, nombrePila: detalle?.nombrePila || '', apellido1: detalle?.apellido1 || '', apellido2: detalle?.apellido2 || '', dni: detalle?.dni || '' });
        }
      });
      return list;
    },
    entradasDetalleBase() {
      const base = [];
      this.tipos.forEach(tipo => {
        const count = this.cantidades[tipo.idTipoEntrada] || 0;
        for (let i = 0; i < count; i++) {
          base.push({ tipo, nombrePila: '', apellido1: '', apellido2: '', dni: '' });
        }
      });
      return base;
    },
    canSubmit() {
      const hasTickets = this.totalEntradas > 0;
      const buyerOk = this.comprador.nombrePila && this.comprador.apellido1 && this.comprador.email && this.comprador.telefono;
      const asistentesOk = this.entradasDetalle.every(e => e.nombrePila && e.apellido1 && e.dni);
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
        const idSesion = this.$route.params.idSesion || this.$route.params.id;
        const [sesion, tipos] = await Promise.all([
          SesionRepository.getDetallePublic(idSesion),
          TipoEntradaRepository.getAll()
        ]);
        this.sesion = sesion;
        this.tipos = tipos || [];
        this.edicion = await EdicionRepository.getDetallePublic(sesion.idEdicion);
      } catch (e) {
        this.error = 'No se pudo cargar la sesión o los tipos de entrada.';
      } finally {
        this.loading = false;
      }
    },
    formatFecha(value) { return new Date(value).toLocaleDateString(); },
    formatHora(value) { return new Date(value).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }); },
    formatPrice(value) { return `${Number(value || 0).toFixed(2)} €`; },
    salasTexto(salas = []) { return salas.length ? salas.map(s => s.nombre || s.idSala).join(', ') : 'Sin salas'; },
    inc(idTipo) { this.$set(this.cantidades, idTipo, (this.cantidades[idTipo] || 0) + 1); },
    dec(idTipo) { const current = this.cantidades[idTipo] || 0; if (current > 0) this.$set(this.cantidades, idTipo, current - 1); },
    buildPayload() {
      const entradas = [];
      this.tipos.forEach(tipo => {
        const count = this.cantidades[tipo.idTipoEntrada] || 0;
        for (let i = 0; i < count; i++) {
          const idx = entradas.length;
          const detalle = this.entradasDetalle[idx];
          entradas.push({
            idTipoEntrada: tipo.idTipoEntrada,
            nombrePila: detalle?.nombrePila,
            apellido1: detalle?.apellido1,
            apellido2: detalle?.apellido2,
            dni: detalle?.dni
          });
        }
      });
      return { comprador: { ...this.comprador }, entradas };
    },
    async reservar() {
      if (!this.canSubmit) return;
      this.submitting = true;
      this.error = '';
      try {
        const payload = this.buildPayload();
        await ReservaRepository.create(this.sesion.idSesion, payload);
        alert('Reserva creada correctamente.');
        this.$router.push({ name: 'MisReservas' });
      } catch (e) {
        this.error = 'No se pudo crear la reserva.';
      } finally {
        this.submitting = false;
      }
    }
  }
};
</script>

<style scoped>
.page { max-width: 1200px; margin: 0 auto; padding: 32px 20px 48px; display: flex; flex-direction: column; gap: 20px; }
.header { display: flex; justify-content: space-between; align-items: center; gap: 12px; }
.eyebrow { text-transform: uppercase; letter-spacing: .08em; font-size: 12px; color: #5b6472; margin: 0; }
.muted { color: #5b6472; margin: 0; }
.pill { background: #eef1f6; padding: 6px 10px; border-radius: 10px; font-weight: 700; }
.center { display: flex; justify-content: center; padding: 30px 0; }
.grid { display: grid; grid-template-columns: repeat(auto-fit,minmax(320px,1fr)); gap: 12px; }
.card { background: #fff; border: 1px solid #e9ecf5; border-radius: 14px; padding: 14px; box-shadow: 0 8px 18px rgba(0,0,0,0.05); display: flex; flex-direction: column; gap: 10px; }
.ticket-list { display: flex; flex-direction: column; gap: 10px; }
.ticket-row { display: grid; grid-template-columns: 1fr auto auto; gap: 10px; align-items: center; border: 1px solid #eef1f6; border-radius: 12px; padding: 10px; }
.price { font-weight: 700; }
.qty { display: flex; gap: 6px; align-items: center; }
.qty button { width: 28px; height: 28px; border-radius: 6px; border: 1px solid #d9deea; background: #fff; cursor: pointer; }
.summary { display: flex; justify-content: space-between; font-weight: 700; }
.form-grid { display: grid; grid-template-columns: repeat(auto-fit,minmax(220px,1fr)); gap: 10px; }
input { width: 100%; padding: 10px; border-radius: 10px; border: 1px solid #d9deea; }
.assist-grid { display: grid; grid-template-columns: repeat(auto-fit,minmax(220px,1fr)); gap: 10px; }
.assist-card { border: 1px solid #eef1f6; border-radius: 12px; padding: 10px; display: flex; flex-direction: column; gap: 6px; }
.actions { display: flex; justify-content: flex-end; gap: 10px; }
.btn-primary { border: none; background: linear-gradient(135deg,#1f4b99,#153a7a); color: #fff; padding: 10px 14px; border-radius: 10px; font-weight: 700; cursor: pointer; }
.btn-ghost { border: 1px solid #d9deea; background: #fff; color: #1f4b99; padding: 10px 14px; border-radius: 10px; font-weight: 700; cursor: pointer; text-decoration: none; }
.note { margin: 0; color: #5b6472; }
.empty { padding: 14px; background: #f6f8ff; border-radius: 10px; color: #4a5460; }
</style>
