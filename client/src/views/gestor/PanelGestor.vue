<template>
  <div class="container py-5 animate-slide-up">
    <div class="d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center mb-5 gap-3">
      <div>
        <h2 class="display-6 font-playfair mb-2">Mis exposiciones</h2>
        <p class="text-muted lead mb-0">Listado de exposiciones que gestionas y alta de nuevas.</p>
      </div>
      <div class="d-flex align-items-center gap-3">
        <div class="form-check form-switch">
          <input class="form-check-input" type="checkbox" id="showArchived" v-model="incluirArchivadas" @change="load">
          <label class="form-check-label small text-muted fw-bold" for="showArchived">Mostrar archivadas</label>
        </div>
        <button v-if="puedeCrear" class="btn btn-primary shadow-sm" @click="toggleCreate" :disabled="creating">
          <i class="bi bi-plus-lg me-2"></i>Nueva Exposición
        </button>
      </div>
    </div>

    <!-- Formulario de Creación -->
    <div v-if="creating" class="card shadow-sm border-0 mb-5 animate-slide-up">
      <div class="card-header bg-white border-bottom-0 pt-4 px-4">
        <h5 class="font-playfair mb-0">Crear nueva exposición</h5>
      </div>
      <div class="card-body px-4 pb-4">
        <div class="row g-3">
          <div class="col-md-12">
            <label class="form-label small fw-bold text-uppercase text-muted">Título *</label>
            <input v-model="form.titulo" type="text" class="form-control" placeholder="Título de la exposición" required />
          </div>
          <div class="col-md-12">
            <label class="form-label small fw-bold text-uppercase text-muted">Descripción</label>
            <textarea v-model="form.descripcion" class="form-control" rows="3" placeholder="Breve descripción..."></textarea>
          </div>
        </div>
        <div class="d-flex justify-content-end gap-2 mt-4">
          <button class="btn btn-outline-secondary" @click="toggleCreate">Cancelar</button>
          <button class="btn btn-primary px-4" @click="create" :disabled="loading || !form.titulo">
            <span v-if="loading" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
            Crear Exposición
          </button>
        </div>
      </div>
    </div>

    <!-- Loading / Error -->
    <div v-if="loading && !creating" class="text-center py-5">
      <div class="spinner-border text-gold" role="status">
        <span class="visually-hidden">Cargando...</span>
      </div>
    </div>

    <div v-else-if="error" class="alert alert-danger shadow-sm" role="alert">
      <i class="bi bi-exclamation-triangle-fill me-2"></i>{{ error }}
    </div>

    <!-- Grid de Exposiciones -->
    <div v-else class="row g-4">
      <div v-if="expos.length === 0" class="col-12">
        <div class="alert alert-light text-center border border-dashed py-5 text-muted">
          <i class="bi bi-inbox fs-1 d-block mb-3"></i>
          <p class="mb-0">No hay exposiciones asignadas.</p>
        </div>
      </div>

      <div v-for="expo in expos" :key="expo.idExposicion" class="col-md-6 col-lg-4">
        <article class="card shadow-sm border-0 h-100 hover-lift transition-all">
          <div class="card-body p-4 d-flex flex-column">
            <div class="d-flex justify-content-between align-items-start mb-3">
              <div>
                <h4 class="h5 font-playfair fw-bold mb-1 text-truncate" :title="expo.titulo">{{ expo.titulo }}</h4>
                <p class="small text-muted mb-0">Ediciones: {{ expo.numEdiciones }}</p>
              </div>
              <span class="badge rounded-pill" :class="badgeClass(expo.estadoExpo)">{{ expo.estadoExpo }}</span>
            </div>

            <p class="text-muted small flex-grow-1 mb-3 line-clamp-3">
              {{ expo.descripcion || 'Sin descripción disponible.' }}
            </p>

            <div class="d-flex align-items-center justify-content-between mt-auto pt-3 border-top">
              <span class="badge bg-light text-dark border">
                <i class="bi bi-shield-lock me-1"></i>{{ expo.miPermiso || '-' }}
              </span>

              <div class="dropdown">
                <button class="btn btn-sm btn-outline-secondary dropdown-toggle" type="button" data-bs-toggle="dropdown" aria-expanded="false">
                  Acciones
                </button>
                <ul class="dropdown-menu dropdown-menu-end shadow-sm border-0">
                  <li>
                    <router-link :to="`/gestor/exposiciones/${expo.idExposicion}`" class="dropdown-item">
                      <i class="bi bi-eye me-2"></i>Ver Detalle
                    </router-link>
                  </li>
                  <li v-if="esCreador(expo)">
                    <button class="dropdown-item" @click="toggleArchive(expo)" :disabled="actionLoading">
                      <i class="bi bi-archive me-2"></i>{{ expo.estadoExpo === 'ARCHIVADA' ? 'Desarchivar' : 'Archivar' }}
                    </button>
                  </li>
                  <li v-if="esCreador(expo)">
                    <hr class="dropdown-divider">
                  </li>
                  <li v-if="esCreador(expo)">
                    <button class="dropdown-item text-danger" @click="eliminar(expo)" :disabled="actionLoading">
                      <i class="bi bi-trash me-2"></i>Eliminar
                    </button>
                  </li>
                </ul>
              </div>
            </div>
          </div>
        </article>
      </div>
    </div>
  </div>
</template>

<script>
import ExpoRepository from '@/repositories/ExpoRepository';
import { getStore } from '@/common/store';

export default {
  name: 'PanelGestor',
  data() {
    return { expos: [], loading: false, actionLoading: false, error: '', incluirArchivadas: false, creating: false, form: { titulo: '', descripcion: '' }, puedeCrear: false };
  },
  created() { this.load(); },
  methods: {
    canCreateByRole() {
      const user = getStore().state.user || {};
      const role = (user.authority || user.autoridad || '').toUpperCase();
      const permiso = (user.gestorRol || user.permisoGestor || user.tipoGestor || '').toUpperCase();
      if (role === 'ADMIN') return true;
      if (role === 'GESTOR') return permiso === 'CREADOR';
      return false;
    },
    badgeClass(estado) {
      const map = {
        ACTIVA: 'bg-success',
        EN_PREPARACION: 'bg-warning text-dark',
        BORRADOR: 'bg-secondary',
        ARCHIVADA: 'bg-dark'
      };
      return map[estado] || 'bg-secondary';
    },
    toggleCreate() {
      this.creating = !this.creating;
      if (!this.creating) { this.form.titulo = ''; this.form.descripcion = ''; }
    },
    async load() {
      this.loading = true; this.error = '';
      try {
        const raw = await ExpoRepository.listGestor({ incluirArchivadas: this.incluirArchivadas });
        this.expos = (raw || []).map(expo => {
          const permiso = (expo.miPermiso || expo.permiso || expo.tipoPermiso || '').toString().toUpperCase();
          return { ...expo, miPermiso: permiso };
        });
        // Un gestor CREATOR puede crear aunque todavía no tenga expos asignadas
        this.puedeCrear = this.canCreateByRole() || this.expos.some(this.esCreador);
      } catch (e) { this.error = 'No se pudieron cargar las exposiciones'; }
      finally { this.loading = false; }
    },
    async create() {
      this.loading = true; this.error = '';
      try {
        await ExpoRepository.create({ ...this.form });
        this.toggleCreate();
        await this.load();
      } catch (e) { this.error = 'Error al crear la exposición'; }
      finally { this.loading = false; }
    },
    goEdit(expo) { this.$router.push({ name: 'ExposicionDetalle', params: { idExposicion: expo.idExposicion } }); },
    esCreador(expo) { return (expo.miPermiso || '').toUpperCase() === 'CREADOR'; },
    async toggleArchive(expo) {
      if (this.actionLoading) return;
      this.actionLoading = true; this.error = '';
      try {
        if (expo.estadoExpo === 'ARCHIVADA') await ExpoRepository.desarchivar(expo.idExposicion);
        else await ExpoRepository.archivar(expo.idExposicion);
        await this.load();
      } catch (e) {
        this.error = e.response?.data?.message || 'No se pudo cambiar el estado';
      } finally {
        this.actionLoading = false;
      }
    },
    async eliminar(expo) {
      if (!confirm('¿Eliminar esta exposición?')) return;
      this.actionLoading = true; this.error = '';
      try {
        await ExpoRepository.delete(expo.idExposicion);
        await this.load();
      } catch (e) {
        this.error = e.response?.data?.message || 'No se pudo eliminar';
      } finally {
        this.actionLoading = false;
      }
    }
  }
};
</script>

<style scoped>
.hover-lift {
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
.hover-lift:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0,0,0,0.1) !important;
}
.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
