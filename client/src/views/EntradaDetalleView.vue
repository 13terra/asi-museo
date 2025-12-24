<template>
  <div class="page">
    <header class="head" v-if="entrada">
      <div>
        <p class="eyebrow">Entrada #{{ entrada.idEntrada }}</p>
        <h1>{{ entrada.exposicion?.titulo || 'Exposición' }}</h1>
        <p class="muted">{{ entrada.edicion?.nombre || entrada.edicion?.idEdicion }} · {{ formatFecha(entrada.sesion?.fecha) }} · {{ formatHora(entrada.sesion?.horaInicio) }}</p>
      </div>
      <span class="chip chip-green">{{ entrada.estado || 'VÁLIDA' }}</span>
    </header>

    <div v-if="loading" class="center"><div class="spinner-border" role="status"></div></div>
    <div v-else-if="error" class="alert alert-danger">{{ error }}</div>
    <div v-else-if="!entrada" class="empty">Entrada no encontrada.</div>
    <div v-else class="card">
      <div class="row">
        <div>
          <p class="label">Asistente</p>
          <h3>{{ entrada.nombrePila }} {{ entrada.apellido1 }} {{ entrada.apellido2 }}</h3>
          <p class="muted">DNI: {{ entrada.dni }}</p>
        </div>
        <div>
          <p class="label">Tipo</p>
          <p class="body">{{ entrada.tipoEntrada?.nombre }} · {{ formatPrice(entrada.precio || entrada.tipoEntrada?.precio) }}</p>
        </div>
        <div>
          <p class="label">Sala(s)</p>
          <p class="body">{{ entrada.sesion?.salas?.map(s=>s.nombre).join(', ') || '—' }}</p>
        </div>
      </div>
      <div class="qr">Código: {{ entrada.idEntrada }}</div>
    </div>
  </div>
</template>

<script>
import EntradaRepository from '@/repositories/EntradaRepository';

export default {
  name: 'EntradaDetalleView',
  data() {
    return {
      entrada: null,
      loading: true,
      error: ''
    };
  },
  async created() {
    await this.load();
  },
  methods: {
    async load() {
      this.loading = true;
      this.error = '';
      try {
        this.entrada = await EntradaRepository.getById(this.$route.params.id);
      } catch (e) {
        this.error = 'No se pudo cargar la entrada.';
      } finally {
        this.loading = false;
      }
    },
    formatFecha(v) { return v ? new Date(v).toLocaleDateString() : ''; },
    formatHora(v) { return v ? new Date(v).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : ''; },
    formatPrice(v) { return `${Number(v || 0).toFixed(2)} €`; }
  }
};
</script>

<style scoped>
.page { max-width: 900px; margin: 0 auto; padding: 28px 18px 48px; display: flex; flex-direction: column; gap: 16px; }
.head { display: flex; justify-content: space-between; align-items: center; gap: 12px; }
.eyebrow { text-transform: uppercase; letter-spacing: .08em; font-size: 12px; color: #5b6472; margin: 0; }
h1 { margin: 4px 0 6px; }
.muted { color: #5b6472; margin: 0; }
.chip { padding: 6px 10px; border-radius: 999px; font-weight: 700; font-size: 12px; background: #eef1f6; color: #2a2f36; }
.chip-green { background: #e3f7e9; color: #1f7a3d; }

.center { display: flex; justify-content: center; padding: 30px 0; }
.empty { padding: 14px; background: #f6f8ff; border-radius: 10px; color: #4a5460; }
.card { background: #fff; border: 1px solid #e9ecf5; border-radius: 14px; padding: 16px; box-shadow: 0 8px 18px rgba(0,0,0,0.05); display: flex; flex-direction: column; gap: 12px; }
.row { display: grid; grid-template-columns: repeat(auto-fit,minmax(220px,1fr)); gap: 12px; }
.label { text-transform: uppercase; letter-spacing: .06em; font-size: 11px; color: #5b6472; margin: 0; }
.body { color: #2f3540; margin: 0; }
.qr { margin-top: 6px; padding: 12px; border: 1px dashed #d9deea; border-radius: 12px; text-align: center; font-weight: 700; }
</style>
