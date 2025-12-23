<template>
  <div class="notfound-shell">
    <div class="card">
      <p class="eyebrow">Error 404</p>
      <h1>La página no existe</h1>
      <p class="muted">Intentaste entrar en {{ path }}</p>
      <div class="actions">
        <router-link to="/" class="btn-primary">Volver al inicio</router-link>
        <router-link to="/catalogo" class="btn-ghost">Ir al catálogo</router-link>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      path: this.$route.fullPath
    };
  }
};
</script>

<style scoped>
.notfound-shell {
  min-height: 70vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 16px;
}

.card {
  background: #fff;
  border-radius: 14px;
  padding: 24px;
  box-shadow: 0 12px 26px rgba(0, 0, 0, 0.08);
  max-width: 520px;
  text-align: center;
}

.eyebrow {
  text-transform: uppercase;
  letter-spacing: 0.08em;
  font-size: 12px;
  color: #6c7685;
  margin: 0 0 6px;
}

h1 {
  margin: 0 0 8px;
}

.muted {
  color: #6c7685;
  margin: 0 0 16px;
}

.actions {
  display: flex;
  gap: 10px;
  justify-content: center;
  flex-wrap: wrap;
}

.btn-primary {
  background: #1f4b99;
  color: #fff;
  border: none;
  border-radius: 10px;
  padding: 10px 14px;
  font-weight: 700;
  text-decoration: none;
}

.btn-ghost {
  border: 1px solid #d9deea;
  color: #1f4b99;
  background: #fff;
  border-radius: 10px;
  padding: 10px 14px;
  font-weight: 700;
  text-decoration: none;
}
</style>
