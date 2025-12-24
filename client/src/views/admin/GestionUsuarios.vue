<template>
  <div class="page">
    <header class="head">
      <div>
        <p class="eyebrow">Administración</p>
        <h1>Gestión de usuarios</h1>
        <p class="muted">Altas, edición de roles y activación/desactivación.</p>
      </div>
      <button class="btn" @click="load" :disabled="loading">Recargar</button>
    </header>

    <section class="card">
      <h3>Filtrar</h3>
      <div class="form-grid">
        <label>Rol
          <select v-model="filters.autoridad" @change="load">
            <option value="">Todos</option>
            <option :value="ROLES.ADMIN">Admin</option>
            <option :value="ROLES.GESTOR">Gestor</option>
            <option :value="ROLES.VISITANTE">Visitante</option>
          </select>
        </label>
        <label>Estado
          <select v-model="filters.estado" @change="load">
            <option value="">Todos</option>
            <option value="ACTIVO">Activo</option>
            <option value="INACTIVO">Inactivo</option>
          </select>
        </label>
      </div>
    </section>

    <section class="card">
      <div class="section-head">
        <div>
          <p class="eyebrow">Formulario</p>
          <h3>{{ editId ? 'Editar usuario' : 'Crear usuario' }}</h3>
        </div>
        <button v-if="editId" class="btn ghost" @click="reset">Limpiar</button>
      </div>
      <div class="form-grid">
        <label>Login<input v-model="form.login" /></label>
        <label>Contraseña<input type="password" v-model="form.password" :placeholder="editId ? 'Dejar vacío para mantener' : ''" /></label>
        <label>Rol
          <select v-model="form.authority">
            <option :value="ROLES.ADMIN">Admin</option>
            <option :value="ROLES.GESTOR">Gestor</option>
            <option :value="ROLES.VISITANTE">Visitante</option>
          </select>
        </label>
        <label>Estado
          <select v-model="form.estado">
            <option value="ACTIVO">Activo</option>
            <option value="INACTIVO">Inactivo</option>
          </select>
        </label>
        <div class="actions">
          <button class="btn primary" :disabled="saving || !form.login" @click="save">{{ saving ? 'Guardando...' : editId ? 'Actualizar' : 'Crear' }}</button>
          <button class="btn ghost" v-if="editId" @click="reset">Cancelar</button>
        </div>
      </div>
      <p v-if="error" class="error">{{ error }}</p>
    </section>

    <section class="card">
      <div class="section-head">
        <div>
          <p class="eyebrow">Usuarios</p>
          <h3>Listado</h3>
        </div>
        <small class="muted">{{ users.length }} resultados</small>
      </div>
      <div v-if="loading" class="center"><div class="spinner-border" role="status"></div></div>
      <p v-else-if="error" class="error">{{ error }}</p>
      <div v-else-if="users.length === 0" class="empty">No hay usuarios con los filtros actuales.</div>
      <div v-else class="table">
        <div class="row header"><span>Login</span><span>Rol</span><span>Estado</span><span></span></div>
        <div class="row" v-for="user in users" :key="user.idUser || user.id">
          <span>{{ user.login }}</span>
          <span class="chip">{{ user.authority || user.autoridad }}</span>
          <span class="chip" :class="user.estado === 'INACTIVO' ? 'chip-warn' : ''">{{ user.estado || 'ACTIVO' }}</span>
          <div class="actions-row">
            <button class="btn" @click="startEdit(user)">Editar</button>
            <button class="btn" @click="toggleEstado(user)">{{ (user.estado || 'ACTIVO') === 'ACTIVO' ? 'Desactivar' : 'Activar' }}</button>
            <button class="btn danger" @click="remove(user)">Eliminar</button>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
import UserRepository from '@/repositories/UserRepository';
import { ROLES } from '@/constants';

export default {
  name: 'GestionUsuarios',
  data() {
    return {
      ROLES,
      users: [],
      loading: true,
      saving: false,
      error: '',
      editId: null,
      filters: { autoridad: '', estado: '' },
      form: { login: '', password: '', authority: ROLES.VISITANTE, estado: 'ACTIVO' }
    };
  },
  async created() { await this.load(); },
  methods: {
    reset() { this.editId = null; this.form = { login: '', password: '', authority: ROLES.VISITANTE, estado: 'ACTIVO' }; this.error = ''; },
    async load() {
      this.loading = true; this.error = '';
      try {
        const filters = {};
        if (this.filters.autoridad) filters.autoridad = this.filters.autoridad;
        if (this.filters.estado) filters.estado = this.filters.estado;
        this.users = await UserRepository.getAll(filters);
      } catch (e) { this.error = 'No se pudo cargar la lista de usuarios.'; }
      finally { this.loading = false; }
    },
    startEdit(user) {
      this.editId = user.idUser || user.id;
      this.form = {
        login: user.login,
        password: '',
        authority: user.authority || user.autoridad || ROLES.VISITANTE,
        estado: user.estado || 'ACTIVO'
      };
    },
    async save() {
      if (!this.form.login) return;
      this.saving = true; this.error = '';
      const payload = { login: this.form.login, authority: this.form.authority, estado: this.form.estado };
      if (this.form.password) payload.password = this.form.password;
      try {
        if (this.editId) await UserRepository.update(this.editId, payload);
        else await UserRepository.create(payload);
        this.reset();
        await this.load();
      } catch (e) { this.error = 'No se pudo guardar el usuario.'; }
      finally { this.saving = false; }
    },
    async toggleEstado(user) {
      const id = user.idUser || user.id;
      const isActive = (user.estado || 'ACTIVO') === 'ACTIVO';
      try {
        if (isActive) await UserRepository.desactivar(id);
        else await UserRepository.activar(id);
        await this.load();
      } catch (e) { alert('No se pudo cambiar el estado del usuario.'); }
    },
    async remove(user) {
      if (!confirm('¿Eliminar este usuario?')) return;
      const id = user.idUser || user.id;
      try { await UserRepository.delete(id); await this.load(); }
      catch (e) { alert('No se pudo eliminar el usuario.'); }
    }
  }
};
</script>

<style scoped>
.page { max-width: 1100px; margin: 0 auto; padding: 28px 18px 48px; display: flex; flex-direction: column; gap: 16px; }
.head { display: flex; justify-content: space-between; align-items: center; gap: 12px; }
.eyebrow { text-transform: uppercase; letter-spacing: .08em; font-size: 12px; color: #5b6472; margin: 0; }
.muted { color: #5b6472; margin: 0; }
.btn { padding: 10px 14px; border-radius: 10px; border: 1px solid #d9deea; background: #fff; cursor: pointer; font-weight: 700; }
.btn.primary { background: linear-gradient(135deg,#1f4b99,#153a7a); color: #fff; border: none; }
.btn.ghost { background: #fff; }
.btn.danger { border-color: #e84a4a; color: #e84a4a; background: #fff3f3; }
.card { background: #fff; border: 1px solid #e9ecf5; border-radius: 14px; padding: 16px; box-shadow: 0 8px 18px rgba(0,0,0,0.05); display: flex; flex-direction: column; gap: 12px; }
.form-grid { display: grid; grid-template-columns: repeat(auto-fit,minmax(240px,1fr)); gap: 12px; align-items: end; }
input, select { width: 100%; padding: 10px; border-radius: 10px; border: 1px solid #d9deea; }
.actions { display: flex; gap: 8px; align-items: center; }
.section-head { display: flex; justify-content: space-between; align-items: center; gap: 10px; }
.table { display: flex; flex-direction: column; gap: 8px; }
.row { display: grid; grid-template-columns: 1.5fr 1fr 1fr auto; gap: 8px; align-items: center; padding: 10px; border: 1px solid #eef1f6; border-radius: 10px; }
.row.header { background: #f6f8ff; font-weight: 700; }
.actions-row { display: flex; gap: 6px; justify-content: flex-end; flex-wrap: wrap; }
.error { color: #d23b3b; margin: 0; }
.empty { padding: 12px; background: #f6f8ff; border-radius: 10px; color: #4a5460; }
.center { display: flex; justify-content: center; padding: 20px 0; }
.chip { padding: 6px 10px; border-radius: 999px; background: #eef1f6; font-weight: 700; width: fit-content; }
.chip-warn { background: #fff3d9; color: #aa5b00; }
</style>
