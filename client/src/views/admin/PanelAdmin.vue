<template>
  <div class="page">
    <header class="head">
      <div>
        <p class="eyebrow">Panel de administración</p>
        <h1>Resumen general</h1>
        <p class="muted">Visión rápida de usuarios, salas y exposiciones.</p>
      </div>
      <button class="btn" @click="load" :disabled="loading">Recargar</button>
    </header>

    <section class="grid stats">
      <article class="card stat">
        <p class="eyebrow">Usuarios totales</p>
        <h2>{{ stats.users }}</h2>
        <p class="muted">Admins {{ stats.admins }} · Gestores {{ stats.gestores }} · Visitantes {{ stats.visitantes }}</p>
      </article>
      <article class="card stat">
        <p class="eyebrow">Salas registradas</p>
        <h2>{{ stats.salas }}</h2>
        <p class="muted">Todas las salas disponibles para planificar sesiones.</p>
      </article>
      <article class="card stat">
        <p class="eyebrow">Exposiciones</p>
        <h2>{{ stats.expos }}</h2>
        <p class="muted">{{ stats.archivadas }} archivadas</p>
      </article>
    </section>

    <section class="card shortcuts">
      <div class="shortcut">
        <div>
          <p class="eyebrow">Personas</p>
          <h3>Gestionar usuarios</h3>
          <p class="muted">Altas, bajas y roles.</p>
        </div>
        <button class="btn primary" @click="go('GestionUsuarios')">Ir a usuarios</button>
      </div>
      <div class="shortcut">
        <div>
          <p class="eyebrow">Infraestructura</p>
          <h3>Gestionar salas</h3>
          <p class="muted">Mantén el inventario de salas actualizado.</p>
        </div>
        <button class="btn primary" @click="go('GestionSalasAdmin')">Ir a salas</button>
      </div>
      <div class="shortcut">
        <div>
          <p class="eyebrow">Exposiciones</p>
          <h3>Ver exposiciones</h3>
          <p class="muted">Consulta y crea exposiciones.</p>
        </div>
        <button class="btn" @click="go('AdminExposList')">Abrir exposiciones</button>
      </div>
    </section>

    <section class="card">
      <div class="section-head">
        <div>
          <p class="eyebrow">Exposiciones recientes</p>
          <h3>Últimas que se han consultado</h3>
        </div>
        <router-link class="btn ghost" :to="{ name: 'PanelGestor' }">Ir al listado</router-link>
      </div>
      <div v-if="loading" class="center"><div class="spinner-border" role="status"></div></div>
      <p v-else-if="error" class="error">{{ error }}</p>
      <div v-else-if="expos.length === 0" class="empty">No hay exposiciones registradas.</div>
      <div v-else class="list">
        <article v-for="expo in expos" :key="expo.idExposicion" class="item">
          <div>
            <h4>{{ expo.titulo }}</h4>
            <p class="muted">Estado: {{ expo.estadoExpo }} · Ediciones: {{ expo.numEdiciones || expo.ediciones?.length || 0 }}</p>
          </div>
          <router-link class="btn" :to="{ name: 'ExposicionDetalle', params: { idExposicion: expo.idExposicion } }">Abrir</router-link>
        </article>
      </div>
    </section>
  </div>
</template>

<script>
import UserRepository from '@/repositories/UserRepository';
import SalaRepository from '@/repositories/SalaRepository';
import ExpoRepository from '@/repositories/ExpoRepository';
import { ROLES } from '@/constants';

export default {
  name: 'PanelAdmin',
  data() {
    return {
      loading: true,
      error: '',
      stats: { users: 0, admins: 0, gestores: 0, visitantes: 0, salas: 0, expos: 0, archivadas: 0 },
      expos: []
    };
  },
  async created() { await this.load(); },
  methods: {
    async load() {
      this.loading = true; this.error = '';
      try {
        const [users, salas, expos] = await Promise.all([
          UserRepository.getAll(),
          SalaRepository.getAll(),
          ExpoRepository.getAllForAdmin(true)
        ]);

        const byRole = { [ROLES.ADMIN]: 0, [ROLES.GESTOR]: 0, [ROLES.VISITANTE]: 0 };
        (users || []).forEach(u => { if (byRole[u.authority]) byRole[u.authority] += 1; });

        this.stats = {
          users: (users || []).length,
          admins: byRole[ROLES.ADMIN],
          gestores: byRole[ROLES.GESTOR],
          visitantes: byRole[ROLES.VISITANTE],
          salas: (salas || []).length,
          expos: (expos || []).filter(e => e.estadoExpo !== 'ARCHIVADA').length,
          archivadas: (expos || []).filter(e => e.estadoExpo === 'ARCHIVADA').length
        };
        this.expos = (expos || []).slice(0, 6);
      } catch (e) { this.error = 'No se pudo cargar el resumen.'; }
      finally { this.loading = false; }
    },
    go(name) { this.$router.push({ name }); }
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
.grid.stats { display: grid; grid-template-columns: repeat(auto-fit,minmax(240px,1fr)); gap: 12px; }
.card { background: #fff; border: 1px solid #e9ecf5; border-radius: 14px; padding: 16px; box-shadow: 0 8px 18px rgba(0,0,0,0.05); display: flex; flex-direction: column; gap: 8px; }
.card.stat h2 { margin: 2px 0; font-size: 32px; }
.shortcuts { gap: 14px; }
.shortcut { display: flex; justify-content: space-between; align-items: center; gap: 10px; padding: 10px 0; border-bottom: 1px dashed #e3e7f0; }
.shortcut:last-child { border-bottom: none; }
.section-head { display: flex; justify-content: space-between; align-items: center; gap: 10px; }
.list { display: flex; flex-direction: column; gap: 10px; }
.item { display: flex; justify-content: space-between; gap: 12px; align-items: center; padding: 12px; border: 1px solid #eef1f6; border-radius: 12px; }
.error { color: #d23b3b; margin: 0; }
.empty { padding: 12px; background: #f6f8ff; border-radius: 10px; color: #4a5460; }
.center { display: flex; justify-content: center; padding: 20px 0; }
</style>
