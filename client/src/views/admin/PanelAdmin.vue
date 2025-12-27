<template>
  <div class="container py-5 animate-slide-up">
    <div class="d-flex justify-content-between align-items-start mb-5">
      <div>
        <p class="text-uppercase text-gold ls-1 mb-2 fw-bold">Panel de administración</p>
        <h1 class="display-5 font-playfair mb-2">Resumen general</h1>
        <p class="text-muted lead mb-0">Visión rápida de usuarios, salas y exposiciones.</p>
      </div>
    </div>

    <!-- ESTADÍSTICAS -->
    <section class="row g-4 mb-5">
      <div class="col-md-4">
        <article class="card shadow-sm border-0 h-100">
          <div class="card-body p-4 d-flex align-items-start gap-3">
            <div class="rounded-3 bg-primary text-white p-3 d-flex align-items-center justify-content-center" style="width: 60px; height: 60px;">
              <i class="bi bi-people fs-4"></i>
            </div>
            <div>
              <p class="text-muted small text-uppercase fw-bold mb-1">Usuarios totales</p>
              <h2 class="display-6 fw-bold text-dark mb-1">{{ stats.users }}</h2>
              <p class="small text-muted mb-0">
                Admins: {{ stats.admins }} · Gestores: {{ stats.gestores }} · Visitantes: {{ stats.visitantes }}
              </p>
            </div>
          </div>
        </article>
      </div>

      <div class="col-md-4">
        <article class="card shadow-sm border-0 h-100">
          <div class="card-body p-4 d-flex align-items-start gap-3">
            <div class="rounded-3 bg-gold text-white p-3 d-flex align-items-center justify-content-center" style="width: 60px; height: 60px;">
              <i class="bi bi-building fs-4"></i>
            </div>
            <div>
              <p class="text-muted small text-uppercase fw-bold mb-1">Salas registradas</p>
              <h2 class="display-6 fw-bold text-dark mb-1">{{ stats.salas }}</h2>
              <p class="small text-muted mb-0">Todas las salas disponibles para planificar sesiones</p>
            </div>
          </div>
        </article>
      </div>

      <div class="col-md-4">
        <article class="card shadow-sm border-0 h-100">
          <div class="card-body p-4 d-flex align-items-start gap-3">
            <div class="rounded-3 bg-success text-white p-3 d-flex align-items-center justify-content-center" style="width: 60px; height: 60px;">
              <i class="bi bi-kanban fs-4"></i>
            </div>
            <div>
              <p class="text-muted small text-uppercase fw-bold mb-1">Exposiciones</p>
              <h2 class="display-6 fw-bold text-dark mb-1">{{ stats.expos }}</h2>
              <p class="small text-muted mb-0">{{ stats.archivadas }} archivadas</p>
            </div>
          </div>
        </article>
      </div>
    </section>

    <!-- ACCESOS DIRECTOS -->
    <section>
      <h3 class="h4 font-playfair mb-4">Accesos rápidos</h3>
      <div class="row g-4">
        <div class="col-md-6 col-lg-3">
          <div class="card shadow-sm border-0 h-100 cursor-pointer hover-lift" @click="go('GestionUsuarios')">
            <div class="card-body p-4 d-flex align-items-center gap-3">
              <div class="rounded-3 bg-primary bg-opacity-10 text-primary p-3 d-flex align-items-center justify-content-center" style="width: 50px; height: 50px;">
                <i class="bi bi-people-fill fs-5"></i>
              </div>
              <div class="flex-grow-1">
                <h4 class="h6 fw-bold mb-1">Gestionar usuarios</h4>
                <p class="small text-muted mb-0">Altas, bajas y roles</p>
              </div>
              <i class="bi bi-arrow-right text-muted"></i>
            </div>
          </div>
        </div>

        <div class="col-md-6 col-lg-3">
          <div class="card shadow-sm border-0 h-100 cursor-pointer hover-lift" @click="go('GestionSalasAdmin')">
            <div class="card-body p-4 d-flex align-items-center gap-3">
              <div class="rounded-3 bg-warning bg-opacity-10 text-warning p-3 d-flex align-items-center justify-content-center" style="width: 50px; height: 50px;">
                <i class="bi bi-building fs-5"></i>
              </div>
              <div class="flex-grow-1">
                <h4 class="h6 fw-bold mb-1">Gestionar salas</h4>
                <p class="small text-muted mb-0">Inventario de salas</p>
              </div>
              <i class="bi bi-arrow-right text-muted"></i>
            </div>
          </div>
        </div>

        <div class="col-md-6 col-lg-3">
          <div class="card shadow-sm border-0 h-100 cursor-pointer hover-lift" @click="go('AdminExposList')">
            <div class="card-body p-4 d-flex align-items-center gap-3">
              <div class="rounded-3 bg-success bg-opacity-10 text-success p-3 d-flex align-items-center justify-content-center" style="width: 50px; height: 50px;">
                <i class="bi bi-kanban-fill fs-5"></i>
              </div>
              <div class="flex-grow-1">
                <h4 class="h6 fw-bold mb-1">Ver exposiciones</h4>
                <p class="small text-muted mb-0">Consulta y crea expos</p>
              </div>
              <i class="bi bi-arrow-right text-muted"></i>
            </div>
          </div>
        </div>

        <div class="col-md-6 col-lg-3">
          <div class="card shadow-sm border-0 h-100 cursor-pointer hover-lift" @click="go('AdminCatalogoObras')">
            <div class="card-body p-4 d-flex align-items-center gap-3">
              <div class="rounded-3 bg-info bg-opacity-10 text-info p-3 d-flex align-items-center justify-content-center" style="width: 50px; height: 50px;">
                <i class="bi bi-collection fs-5"></i>
              </div>
              <div class="flex-grow-1">
                <h4 class="h6 fw-bold mb-1">Catálogo de obras</h4>
                <p class="small text-muted mb-0">Consulta e importa obras</p>
              </div>
              <i class="bi bi-arrow-right text-muted"></i>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- LOADING/ERROR -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-gold" role="status">
        <span class="visually-hidden">Cargando...</span>
      </div>
    </div>

    <div v-if="error" class="alert alert-danger mt-4 shadow-sm" role="alert">
      <i class="bi bi-exclamation-triangle-fill me-2"></i> {{ error }}
    </div>
  </div>
</template>

<script>
import { getStore } from '@/common/store';
import UserRepository from '@/repositories/UserRepository';
import SalaRepository from '@/repositories/SalaRepository';
import ExpoRepository from '@/repositories/ExpoRepository';

export default {
  name: 'PanelAdmin',
  data() {
    return {
      stats: {
        users:  0,
        admins: 0,
        gestores: 0,
        visitantes: 0,
        salas: 0,
        expos: 0,
        archivadas: 0
      },
      loading: false,
      error: ''
    };
  },
  computed: {
    userName() {
      return getStore().state.user.login || 'Admin';
    }
  },
  async mounted() {
    await this.load();
  },
  methods: {
    async load() {
      this.loading = true;
      this.error = '';

      try {
        const [usersResult, salasResult, exposResult] = await Promise.allSettled([
          UserRepository.getAll(),
          SalaRepository.getAll(),
          ExpoRepository.getAllForAdmin(false)
        ]);

        // Procesar usuarios
        if (usersResult.status === 'fulfilled') {
          const users = usersResult.value || [];
          this.stats.users = users.length;

          // Normaliza el rol para soportar distintas claves/formatos devueltos por backend
          const roleOf = (u) => (u.authority || u.autoridad || u.role || '').toString().toUpperCase();

          this.stats.admins = users.filter(u => roleOf(u) === 'ADMIN').length;
          this.stats.gestores = users.filter(u => roleOf(u) === 'GESTOR').length;
          this.stats.visitantes = users.filter(u => roleOf(u) === 'VISITANTE').length;
        }

        // Procesar salas
        if (salasResult.status === 'fulfilled') {
          this.stats.salas = salasResult.value.length;
        }

        // Procesar exposiciones
        if (exposResult. status === 'fulfilled') {
          const expos = exposResult.value || [];
          this.stats.expos = expos.length;
          const estadoOf = (e) => (e.estadoExpo || e.estadoExposicion || e.estado || '').toString().toUpperCase();
          this.stats.archivadas = expos.filter(e => estadoOf(e) === 'ARCHIVADA').length;
        }
      } catch (e) {
        console.error('Error loading admin panel:', e);
        this.error = 'No se pudieron cargar las estadísticas';
      } finally {
        this.loading = false;
      }
    },
    go(routeName) {
      this.$router.push({ name: routeName });
    }
  }
};
</script>

<style scoped>
.hover-lift {
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
.hover-lift:hover {
  transform: translateY(-3px);
  box-shadow: 0 .5rem 1rem rgba(0,0,0,.15)!important;
}
.cursor-pointer {
  cursor: pointer;
}
</style>