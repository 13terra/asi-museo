<template>
  <div class="page">
    <header class="header" v-if="sesion && edicion">
      <div>
        <p class="eyebrow">{{ edicion.exposicion?.titulo || 'Exposición' }}</p>
        <h1>Reserva para {{ formatFecha(sesion.horaInicio) }}</h1>
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
              <button @click="dec(tipo.idTipoEntrada)" :disabled="cantidades[tipo.idTipoEntrada] <= 0">-</button>
              <span>{{ cantidades[tipo.idTipoEntrada] || 0 }}</span>
              <button @click="inc(tipo.idTipoEntrada)" :disabled="isMaxReached">+</button>
            </div>
          </div>
        </div>
        <div class="summary">
          <span>Total entradas: {{ totalEntradas }}</span>
          <strong>{{ formatPrice(totalPrecio) }}</strong>
        </div>
        <div v-if="isMaxReached" class="alert alert-warning mt-2">
          <small v-if="totalEntradas >= 10">Máximo 10 entradas por compra.</small>
          <small v-else>Aforo completo.</small>
        </div>
      </section>

      <section class="card form">
        <h3>Datos del comprador</h3>
        <div class="form-grid">
          <label>Nombre *<input v-model="comprador.nombrePila" placeholder="Ej: Juan" required /></label>
          <label>Apellido 1 *<input v-model="comprador.apellido1" placeholder="Ej: Pérez" required /></label>
          <label>Apellido 2<input v-model="comprador.apellido2" placeholder="Ej: García" /></label>
          <label>Email *<input v-model="comprador.email" type="email" placeholder="ejemplo@correo.com" required /></label>
          <label>Teléfono *<input v-model="comprador.telefono" placeholder="+34 600 000 000" required /></label>
          <label>País<input v-model="comprador.pais" placeholder="España" /></label>
        </div>

        <div class="asistentes">
          <h4>Datos de asistentes</h4>
          <p class="muted">Introduce nombre y DNI para cada entrada.</p>
          <div v-if="totalEntradas === 0" class="empty">Añade entradas para completar los datos.</div>
          <div v-else class="assist-grid">
            <template v-for="tipo in tipos" :key="tipo.idTipoEntrada">
              <div v-for="(asistente, idx) in asistentes[tipo.idTipoEntrada]" :key="`${tipo.idTipoEntrada}-${idx}`" class="assist-card">
                <div class="muted">{{ tipo.nombre }} #{{ idx + 1 }}</div>
                <label>Nombre *<input v-model="asistente.nombrePila" placeholder="Nombre del asistente" required /></label>
                <label>Apellido 1 *<input v-model="asistente.apellido1" placeholder="Primer apellido" required /></label>
                <label>Apellido 2<input v-model="asistente.apellido2" placeholder="Segundo apellido" /></label>
                <label>DNI *<input v-model="asistente.dni" placeholder="12345678Z" required /></label>
              </div>
            </template>
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
      } catch (e) {
        this.error = 'No se pudo cargar la sesión o los tipos de entrada.';
      } finally {
        this.loading = false;
      }
    },
    formatFecha(value) {
      if (!value) return '';
      const d = new Date(value);
      return d.toLocaleDateString();
    },
    formatHora(value) {
      if (!value) return '';
      if (value.includes('T')) {
         return new Date(value).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
      }
      return value.substring(0, 5);
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
    async reservar() {
      if (!this.canSubmit) return;
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

<style scoped>
.page { max-width: 1100px; margin: 0 auto; padding: 28px 18px 48px; display: flex; flex-direction: column; gap: 18px; }
.header { background: #f6f8ff; border: 1px solid #e0e5f4; border-radius: 14px; padding: 18px; display: flex; justify-content: space-between; gap: 12px; align-items: center; }
.eyebrow { text-transform: uppercase; letter-spacing: .08em; font-size: 12px; color: #5b6472; margin: 0; }
h1 { margin: 4px 0 6px; }
.muted { color: #5b6472; margin: 0; }
.pill { background: #eef1f6; padding: 8px 12px; border-radius: 999px; font-weight: 700; }

.center { display: flex; justify-content: center; padding: 30px 0; }
.grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
@media (max-width: 900px) { .grid { grid-template-columns: 1fr; } }
.card { background: #fff; border: 1px solid #e9ecf5; border-radius: 14px; padding: 16px; box-shadow: 0 8px 20px rgba(0,0,0,0.05); display: flex; flex-direction: column; gap: 12px; }
.ticket-list { display: flex; flex-direction: column; gap: 10px; }
.ticket-row { display: grid; grid-template-columns: 1.4fr 0.6fr 0.6fr; align-items: center; gap: 10px; padding: 10px; border: 1px solid #eef1f6; border-radius: 12px; }
.price { font-weight: 800; text-align: right; }
.qty { display: flex; gap: 10px; align-items: center; justify-content: flex-end; }
.qty button { width: 32px; height: 32px; border-radius: 8px; border: 1px solid #d9deea; background: #fff; cursor: pointer; }
.summary { display: flex; justify-content: space-between; align-items: center; padding: 10px; border-top: 1px solid #eef1f6; font-weight: 700; }

.form { gap: 14px; }
.form-grid { display: grid; grid-template-columns: repeat(auto-fit,minmax(200px,1fr)); gap: 10px; }
label { display: flex; flex-direction: column; gap: 4px; font-weight: 600; color: #2f3540; }
input { border: 1px solid #d9deea; border-radius: 10px; padding: 10px; }
.asistentes { display: flex; flex-direction: column; gap: 10px; }
.assist-grid { display: grid; grid-template-columns: repeat(auto-fit,minmax(220px,1fr)); gap: 10px; }
.assist-card { border: 1px solid #eef1f6; border-radius: 12px; padding: 10px; background: #fafbff; display: flex; flex-direction: column; gap: 6px; }
.actions { display: flex; justify-content: flex-end; gap: 10px; align-items: center; }
.btn-primary { border: none; background: linear-gradient(135deg,#1f4b99,#153a7a); color: #fff; padding: 10px 14px; border-radius: 10px; font-weight: 700; cursor: pointer; }
.btn-primary:disabled { opacity: .6; cursor: not-allowed; }
.btn-ghost { border: 1px solid #d9deea; background: #fff; color: #1f4b99; padding: 10px 14px; border-radius: 10px; font-weight: 700; cursor: pointer; }
.note { color: #4a5460; margin: 0; font-size: 13px; }
.empty { padding: 12px; color: #5b6472; background: #f6f8ff; border-radius: 10px; }
</style>
