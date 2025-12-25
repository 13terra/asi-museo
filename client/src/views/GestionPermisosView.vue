<template>
  <div class="page">
    <header class="head">
      <div>
        <p class="eyebrow">Permisos de la exposición</p>
        <h1>{{ expoTitle || `Exposición #${idExposicion}` }}</h1>
        <p class="muted">Asigna o revoca permisos de gestores sobre esta exposición.</p>
      </div>
      <button class="btn" @click="load" :disabled="loading">Recargar</button>
    </header>

    <section class="card">
      <h3>Asignar permiso</h3>
      <div class="form-grid">
        <label>
          Gestor
          <select v-model="form.idUser">
            <option value="">Selecciona un gestor</option>
            <option v-for="u in gestores" :key="u.idUser || u.id" :value="u.idUser || u.id">{{ u.login }} ({{ u.idUser || u.id }})</option>
          </select>
        </label>
        <label>
          Permiso
          <select v-model="form.permiso">
            <option value="CREADOR">CREADOR</option>
            <option value="EDITOR">EDITOR</option>
          </select>
        </label>
        <button class="btn primary" :disabled="!canAssign || saving" @click="asignar">
          {{ saving ? 'Guardando...' : 'Asignar' }}
        </button>
      </div>
      <p v-if="error" class="error">{{ error }}</p>
    </section>

    <section class="card">
      <h3>Permisos actuales</h3>
      <div v-if="loading" class="center"><div class="spinner-border" role="status"></div></div>
      <div v-else-if="permisos.length === 0" class="empty">No hay permisos asignados.</div>
      <div v-else class="table">
        <div class="row header">
          <span>ID usuario</span>
          <span>Login</span>
          <span>Permiso</span>
          <span></span>
        </div>
        <div v-for="perm in permisos" :key="perm.idUser" class="row">
          <span>{{ perm.idUser }}</span>
          <span>{{ perm.login || perm.user?.login }}</span>
          <span class="chip">{{ perm.permiso }}</span>
          <button class="btn ghost" @click="revocar(perm.idUser)">Revocar</button>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
import GestionRepository from '@/repositories/GestionRepository';
import UserRepository from '@/repositories/UserRepository';
import ExpoRepository from '@/repositories/ExpoRepository';

export default {
  name: 'GestionPermisosView',
  data() {
    return {
      idExposicion: this.$route.params.id,
      permisos: [],
      gestores: [],
      expoTitle: '',
      loading: true,
      saving: false,
      error: '',
      form: {
        idUser: '',
        permiso: 'EDITOR'
      }
    };
  },
  computed: {
    canAssign() {
      return this.form.idUser && this.form.permiso;
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
        const [permisos, gestores, expo] = await Promise.all([
          GestionRepository.getPermisos(this.idExposicion),
          UserRepository.getAll({ autoridad: 'GESTOR' }),
          ExpoRepository.detailAdmin(this.idExposicion).catch(() => null)
        ]);
        this.permisos = permisos || [];
        this.gestores = gestores || [];
        this.expoTitle = expo?.titulo || '';
      } catch (e) {
        this.error = 'No se pudieron cargar los permisos.';
      } finally {
        this.loading = false;
      }
    },
    async asignar() {
      if (!this.canAssign) return;
      this.saving = true;
      this.error = '';
      try {
        await GestionRepository.asignarPermiso(this.idExposicion, { ...this.form });
        await this.load();
      } catch (e) {
        this.error = 'No se pudo asignar el permiso.';
      } finally {
        this.saving = false;
      }
    },
    async revocar(idUser) {
      if (!confirm('¿Revocar permiso de este gestor?')) return;
      try {
        await GestionRepository.revocarPermiso(this.idExposicion, idUser);
        await this.load();
      } catch (e) {
        alert('No se pudo revocar el permiso.');
      }
    }
  }
};
</script>

<style scoped>
.page { max-width: 1100px; margin: 0 auto; padding: 28px 18px 48px; display: flex; flex-direction: column; gap: 16px; }
.head { display: flex; justify-content: space-between; gap: 12px; align-items: center; }
.eyebrow { text-transform: uppercase; letter-spacing: .08em; font-size: 12px; color: #5b6472; margin: 0; }
.muted { color: #5b6472; margin: 0; }
.btn { padding: 10px 14px; border-radius: 10px; border: 1px solid #d9deea; background: #fff; cursor: pointer; font-weight: 700; }
.btn.primary { background: linear-gradient(135deg,#1f4b99,#153a7a); color: #fff; border: none; }
.btn.ghost { background: #fff; }
.card { background: #fff; border: 1px solid #e9ecf5; border-radius: 14px; padding: 16px; box-shadow: 0 8px 18px rgba(0,0,0,0.05); display: flex; flex-direction: column; gap: 12px; }
.form-grid { display: grid; grid-template-columns: repeat(auto-fit,minmax(240px,1fr)); gap: 12px; align-items: end; }
select { width: 100%; padding: 10px; border-radius: 10px; border: 1px solid #d9deea; }
.table { display: flex; flex-direction: column; gap: 8px; }
.row { display: grid; grid-template-columns: 1fr 2fr 1fr auto; gap: 8px; align-items: center; padding: 10px; border: 1px solid #eef1f6; border-radius: 10px; }
.row.header { background: #f6f8ff; font-weight: 700; }
.chip { padding: 6px 10px; border-radius: 999px; background: #eef1f6; font-weight: 700; width: fit-content; }
.error { color: #d23b3b; margin: 0; }
.empty { padding: 12px; background: #f6f8ff; border-radius: 10px; color: #4a5460; }
.center { display: flex; justify-content: center; padding: 20px 0; }
</style>
