<template>
  <div class="error-shell">
    <div class="error-card">
      <p class="eyebrow">Error 404</p>
      <h1>Página no encontrada</h1>
      <p class="muted">La ruta que buscas no existe o ya no está disponible.</p>
      <div class="actions">
        <router-link class="btn primary" to="/">Ir al inicio</router-link>
        <button class="btn" @click="$router.back()">Volver atrás</button>
      </div>
    </div>
  </div>
</template>

<script>
export default { name: 'ErrorNotFound' };
</script>

<style scoped>
.error-shell { min-height: 80vh; display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg,#f7f9fc,#eef2f7); padding: 24px; }
.error-card { background: #fff; padding: 24px; border-radius: 16px; box-shadow: 0 12px 30px rgba(0,0,0,0.08); max-width: 520px; width: 100%; display: flex; flex-direction: column; gap: 10px; }
.eyebrow { text-transform: uppercase; letter-spacing: .08em; font-size: 12px; color: #5b6472; margin: 0; }
.muted { color: #5b6472; margin: 0; }
.btn { padding: 12px 16px; border-radius: 10px; border: 1px solid #d9deea; background: #fff; cursor: pointer; font-weight: 700; text-decoration: none; text-align: center; }
.btn.primary { background: linear-gradient(135deg,#1f4b99,#153a7a); color: #fff; border: none; }
.actions { display: flex; gap: 10px; flex-wrap: wrap; }
</style>
