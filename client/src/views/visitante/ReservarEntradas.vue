<template>
  <div class="page">
    <header class="header" v-if="sesion && edicion">
      <div>
        <p class="eyebrow">{{ edicion.tituloExpo || 'Exposición' }}</p>
        <h1>Reserva para {{ formatFecha(sesion.horaInicio) }}</h1>
        <p class="muted">{{ formatHora(sesion.horaInicio) }} · {{ formatHora(sesion.horaFin) }} · {{ salasTexto(sesion.salas) }}</p>
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
        <form @submit.prevent="reservar">
          <h3>Datos del comprador</h3>
          <div class="form-grid">
            <label>Nombre *<input v-model="comprador.nombrePila" placeholder="Ej: Juan" required maxlength="50" /></label>
            <label>Apellido 1 *<input v-model="comprador.apellido1" placeholder="Ej: Pérez" required maxlength="50" /></label>
            <label>Apellido 2<input v-model="comprador.apellido2" placeholder="Ej: García" maxlength="50" /></label>
            <label>Email *<input v-model="comprador.email" type="email" placeholder="ejemplo@correo.com" required maxlength="100" /></label>
            <label>Teléfono *<input v-model="comprador.telefono" type="tel" placeholder="+34 600 000 000" required maxlength="20" /></label>
            <label>País<input v-model="comprador.pais" placeholder="España" maxlength="50" /></label>
          </div>

          <div class="asistentes">
            <h4>Datos de asistentes</h4>
            <p class="muted">Introduce nombre y DNI para cada entrada.</p>
            <div v-if="totalEntradas === 0" class="empty">Añade entradas para completar los datos.</div>
            <div v-else class="assist-grid">
              <div v-for="(entrada, index) in entradasDetalle" :key="index" class="assist-card">
                <div class="muted">{{ entrada.tipo.nombre }}</div>
                <label>Nombre *<input v-model="entrada.nombrePila" placeholder="Nombre" required maxlength="50" /></label>
                <label>Apellido 1 *<input v-model="entrada.apellido1" placeholder="Primer apellido" required maxlength="50" /></label>
                <label>Apellido 2<input v-model="entrada.apellido2" placeholder="Segundo apellido" maxlength="50" /></label>
                <label>DNI *<input v-model="entrada.dni" placeholder="12345678Z" required maxlength="15" /></label>
              </div>
            </div>
          </div>

          <div class="actions">
            <button type="button" class="btn-ghost" @click="$router.back()">Cancelar</button>
            <button type="submit" class="btn-primary" :disabled="submitting">
              {{ submitting ? 'Procesando...' : 'Reservar' }}
            </button>
          </div>
          <p class="note">El pago se realizará presencialmente al hacer check-in.</p>
        </form>
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
import { setNotification } from '@/common/store';

export default {
  name: 'ReservarEntradas',
  data() {
    return {
      sesion: null,
      edicion: null,
      tipos: [],
      cantidades: {},
      entradasDetalle: [],
      comprador: { nombrePila: '', apellido1: '', apellido2: '', email: '', telefono: '', pais: '' },
      submitting: false,
      loading: true,
      error: ''
    };
  },
  computed: {
    totalEntradas() { return Object.values(this.cantidades).reduce((a, b) => a + b, 0); },
    totalPrecio() { return this.tipos.reduce((acc, t) => acc + (this.cantidades[t.idTipoEntrada] || 0) * (t.precio || 0), 0); },
    canSubmit() {
      const hasTickets = this.totalEntradas > 0;
      const buyerOk = this.comprador.nombrePila && this.comprador.apellido1 && this.comprador.email && this.comprador.telefono;
      
      const dniRegex = /^\d{8}[A-Za-z]$/;
      const asistentesOk = this.entradasDetalle.every(e => 
        e.nombrePila && 
        e.apellido1 && 
        e.dni && 
        dniRegex.test(e.dni)
      );
      
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
        this.cantidades = {};
        this.syncEntradasDetalle();
        this.edicion = await EdicionRepository.getDetallePublic(sesion.idEdicion);
      } catch (e) {
        this.error = 'No se pudo cargar la sesión o los tipos de entrada.';
      } finally {
        this.loading = false;
      }
    },
    formatFecha(value) {
      if (!value) return '';
      if (Array.isArray(value)) return new Date(value[0], value[1] - 1, value[2]).toLocaleDateString();
      return new Date(value).toLocaleDateString();
    },
    formatHora(value) {
      if (!value) return '';
      if (Array.isArray(value)) {
        if (value.length === 2) return `${value[0].toString().padStart(2, '0')}:${value[1].toString().padStart(2, '0')}`;
        if (value.length >= 5) return `${value[3].toString().padStart(2, '0')}:${value[4].toString().padStart(2, '0')}`;
      }
      if (typeof value === 'string' && value.includes(':') && !value.includes('T')) {
        return value.substring(0, 5);
      }
      return new Date(value).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    },
    formatPrice(value) { return `${Number(value || 0).toFixed(2)} €`; },
    salasTexto(salas = []) {
      if (!salas.length) return 'Sin salas';
      const names = salas.map(s => s.nombre || s.idSala).join(', ');
      return salas.length === 1 ? `Sala: ${names}` : `Salas: ${names}`;
    },
    inc(idTipo) {
      const current = this.cantidades[idTipo] || 0;
      this.cantidades = { ...this.cantidades, [idTipo]: current + 1 };
      this.syncEntradasDetalle();
    },
    dec(idTipo) {
      const current = this.cantidades[idTipo] || 0;
      const next = Math.max(0, current - 1);
      this.cantidades = { ...this.cantidades, [idTipo]: next };
      this.syncEntradasDetalle();
    },
    syncEntradasDetalle() {
      const prevByTipo = {};
      this.entradasDetalle.forEach(e => {
        if (!prevByTipo[e.idTipoEntrada]) prevByTipo[e.idTipoEntrada] = [];
        prevByTipo[e.idTipoEntrada].push(e);
      });

      const next = [];
      this.tipos.forEach(tipo => {
        const count = this.cantidades[tipo.idTipoEntrada] || 0;
        for (let i = 0; i < count; i++) {
          const reuse = prevByTipo[tipo.idTipoEntrada]?.shift();
          next.push(reuse ? { ...reuse, tipo } : { idTipoEntrada: tipo.idTipoEntrada, tipo, nombrePila: '', apellido1: '', apellido2: '', dni: '' });
        }
      });
      this.entradasDetalle = next;
    },
    buildPayload() {
      // Filtrar cantidades > 0
      const entradasPorTipo = {};
      Object.keys(this.cantidades).forEach(k => {
        if (this.cantidades[k] > 0) entradasPorTipo[k] = this.cantidades[k];
      });

      const datosAsistentes = this.entradasDetalle.map(e => ({
        nombrePila: e.nombrePila,
        apellido1: e.apellido1,
        apellido2: e.apellido2,
        dni: e.dni
      }));

      return {
        idSesion: this.sesion.idSesion,
        ...this.comprador,
        entradasPorTipo,
        datosAsistentes
      };
    },
    validate() {
      if (this.totalEntradas <= 0) return 'Debes seleccionar al menos una entrada.';
      if (!this.comprador.nombrePila || !this.comprador.apellido1 || !this.comprador.email || !this.comprador.telefono) {
        return 'Por favor, completa todos los datos obligatorios del comprador.';
      }
      
      const dniRegex = /^\d{8}[A-Za-z]$/;
      for (const e of this.entradasDetalle) {
        if (!e.nombrePila || !e.apellido1 || !e.dni) {
          return `Faltan datos para la entrada de tipo ${e.tipo.nombre}.`;
        }
        if (!dniRegex.test(e.dni)) {
          return `El DNI ${e.dni} no es válido (8 números y 1 letra).`;
        }
      }

      if (this.sesion?.estado !== ESTADOS_SESION.DISPONIBLE) {
        return 'Esta sesión no está disponible para reservas.';
      }
      return null;
    },
    async reservar() {
      console.log('Reservar clicked');
      const errorMsg = this.validate();
      if (errorMsg) {
        console.warn('Cannot submit: validation failed', errorMsg);
        setNotification(errorMsg, 'warning');
        return;
      }
      this.submitting = true;
      this.error = '';
      try {
        const payload = this.buildPayload();
        console.log('Sending payload:', payload);
        // ReservaRepository.create espera (idSesion, reservaObject)
        // reservaObject se mezcla con idSesion dentro del repo, así que pasamos payload completo como 2o arg
        await ReservaRepository.create(this.sesion.idSesion, payload);
        setNotification('Reserva creada correctamente.', 'success');
        this.$router.push({ name: 'MisReservas' });
      } catch (e) {
        console.error('Error creating reserva:', e);
        this.error = e.response?.data?.message || 'No se pudo crear la reserva. Verifique los datos.';
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
