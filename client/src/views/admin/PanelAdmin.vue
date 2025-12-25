<template>
  <div class="panel-container">
    <div class="panel-header">
      <div>
        <p class="eyebrow">Panel de administración</p>
        <h1>Resumen general</h1>
        <p class="subtitle">Visión rápida de usuarios, salas y exposiciones. </p>
      </div>
      <button class="btn-secondary" @click="load" :disabled="loading">
        <i class="bi bi-arrow-clockwise"></i> Recargar
      </button>
    </div>

    <!-- ESTADÍSTICAS -->
    <section class="stats-grid">
      <article class="stat-card">
        <div class="stat-icon bg-primary">
          <i class="bi bi-people"></i>
        </div>
        <div class="stat-content">
          <p class="stat-label">Usuarios totales</p>
          <h2 class="stat-value">{{ stats.users }}</h2>
          <p class="stat-detail">
            Admins: {{ stats.admins }} · Gestores: {{ stats.gestores }} · Visitantes: {{ stats.visitantes }}
          </p>
        </div>
      </article>

      <article class="stat-card">
        <div class="stat-icon bg-warning">
          <i class="bi bi-building"></i>
        </div>
        <div class="stat-content">
          <p class="stat-label">Salas registradas</p>
          <h2 class="stat-value">{{ stats.salas }}</h2>
          <p class="stat-detail">Todas las salas disponibles para planificar sesiones</p>
        </div>
      </article>

      <article class="stat-card">
        <div class="stat-icon bg-success">
          <i class="bi bi-kanban"></i>
        </div>
        <div class="stat-content">
          <p class="stat-label">Exposiciones</p>
          <h2 class="stat-value">{{ stats.expos }}</h2>
          <p class="stat-detail">{{ stats.archivadas }} archivadas</p>
        </div>
      </article>
    </section>

    <!-- ACCESOS DIRECTOS -->
    <section class="shortcuts-section">
      <h3 class="section-title">Accesos rápidos</h3>
      <div class="shortcuts-grid">
        <div class="shortcut-card" @click="go('GestionUsuarios')">
          <div class="shortcut-icon bg-primary">
            <i class="bi bi-people-fill"></i>
          </div>
          <div class="shortcut-content">
            <h4>Gestionar usuarios</h4>
            <p>Altas, bajas y roles</p>
          </div>
          <i class="bi bi-arrow-right shortcut-arrow"></i>
        </div>

        <div class="shortcut-card" @click="go('GestionSalasAdmin')">
          <div class="shortcut-icon bg-warning">
            <i class="bi bi-building"></i>
          </div>
          <div class="shortcut-content">
            <h4>Gestionar salas</h4>
            <p>Mantén el inventario de salas actualizado</p>
          </div>
          <i class="bi bi-arrow-right shortcut-arrow"></i>
        </div>

        <div class="shortcut-card" @click="go('AdminExposList')">
          <div class="shortcut-icon bg-success">
            <i class="bi bi-kanban-fill"></i>
          </div>
          <div class="shortcut-content">
            <h4>Ver exposiciones</h4>
            <p>Consulta y crea exposiciones</p>
          </div>
          <i class="bi bi-arrow-right shortcut-arrow"></i>
        </div>
      </div>
    </section>

    <!-- LOADING/ERROR -->
    <div v-if="loading" class="loading-container">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Cargando...</span>
      </div>
    </div>

    <div v-if="error" class="alert alert-danger" role="alert">
      <i class="bi bi-exclamation-triangle"></i> {{ error }}
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
          UserRepository. findAll(),
          SalaRepository.findAll(),
          ExpoRepository.getAllForAdmin(false)
        ]);

        // Procesar usuarios
        if (usersResult.status === 'fulfilled') {
          const users = usersResult.value;
          this.stats.users = users.length;
          this. stats.admins = users.filter(u => u.authority === 'ADMIN').length;
          this.stats.gestores = users.filter(u => u.authority === 'GESTOR').length;
          this.stats.visitantes = users.filter(u => u.authority === 'VISITANTE').length;
        }

        // Procesar salas
        if (salasResult.status === 'fulfilled') {
          this.stats.salas = salasResult.value.length;
        }

        // Procesar exposiciones
        if (exposResult. status === 'fulfilled') {
          const expos = exposResult.value;
          this.stats.expos = expos.length;
          this.stats.archivadas = expos.filter(e => e. estadoExpo === 'ARCHIVADA').length;
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
.panel-container {
  max-width:  1400px;
  margin: 0 auto;
  padding: 24px;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items:  flex-start;
  margin-bottom: 32px;
}

.eyebrow {
  text-transform: uppercase;
  letter-spacing: 0.08em;
  font-size: 12px;
  color: #6c7685;
  margin:  0 0 8px;
  font-weight: 700;
}

h1 {
  margin: 0 0 8px;
  font-size: 32px;
  font-weight:  800;
}

.subtitle {
  color: #4a5460;
  margin: 0;
}

.btn-secondary {
  background: #f8f9fa;
  border: 1px solid #d9deea;
  border-radius: 10px;
  padding: 10px 16px;
  font-weight: 700;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.2s ease;
}

.btn-secondary:hover:not(:disabled) {
  background: #e9ecef;
  transform: translateY(-1px);
}

.btn-secondary:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom:  40px;
}

.stat-card {
  background: #fff;
  border:  1px solid #e4e8f2;
  border-radius: 16px;
  padding: 20px;
  display: flex;
  gap: 16px;
  align-items:  flex-start;
  transition: all 0.2s ease;
}

.stat-card:hover {
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  flex-shrink: 0;
}

.bg-primary { background: linear-gradient(135deg, #1f4b99, #153a7a); }
.bg-warning { background: linear-gradient(135deg, #ffc107, #ff9800); }
.bg-success { background: linear-gradient(135deg, #28a745, #20c997); }

.stat-content {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #6c7685;
  margin:  0 0 8px;
  font-weight: 600;
}

.stat-value {
  font-size: 36px;
  font-weight:  800;
  margin:  0 0 8px;
  color: #1f2a44;
}

.stat-detail {
  font-size: 14px;
  color: #6c7685;
  margin: 0;
}

.section-title {
  font-size: 20px;
  font-weight: 700;
  margin:  0 0 16px;
}

.shortcuts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 16px;
}

.shortcut-card {
  background: #fff;
  border: 1px solid #e4e8f2;
  border-radius: 14px;
  padding: 18px;
  display: flex;
  gap: 16px;
  align-items: center;
  cursor: pointer;
  transition: all 0.2s ease;
}

.shortcut-card:hover {
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
  transform: translateX(4px);
  border-color: #1f4b99;
}

.shortcut-icon {
  width:  48px;
  height: 48px;
  border-radius:  10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: white;
  flex-shrink: 0;
}

.shortcut-content {
  flex: 1;
}

.shortcut-content h4 {
  margin: 0 0 4px;
  font-size:  16px;
  font-weight: 700;
}

.shortcut-content p {
  margin: 0;
  font-size: 14px;
  color: #6c7685;
}

.shortcut-arrow {
  color: #d9deea;
  font-size: 20px;
  transition: all 0.2s ease;
}

.shortcut-card:hover .shortcut-arrow {
  color: #1f4b99;
  transform: translateX(4px);
}

.loading-container {
  display: flex;
  justify-content: center;
  padding: 48px;
}

@media (max-width: 768px) {
  .panel-header {
    flex-direction:  column;
    gap: 16px;
  }
}
</style>