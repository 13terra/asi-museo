<template>
  <div class="about-shell">
    <div class="card">
      <p class="eyebrow">Sobre el proyecto</p>
      <h1>Museo · Gestión de exposiciones</h1>
      <p class="muted">
        Frontend Vue 3 Options API + Router. Roles: ADMIN, GESTOR, VISITANTE. Enfoque en catálogo público, paneles de
        exposiciones, detalle, ediciones y permisos.
      </p>
      <ul>
        <li>Backend REST en /api con login/register/logout/me.</li>
        <li>Paneles por rol con listados, archivado y edición.</li>
        <li>Catálogo público con búsqueda y estados.</li>
      </ul>
    </div>
  </div>
</template>

<style scoped>
.about-shell {
  max-width: 900px;
  margin: 0 auto;
  padding: 48px 20px;
}

.card {
  background: #fff;
  border-radius: 14px;
  padding: 22px;
  box-shadow: 0 12px 26px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.eyebrow {
  text-transform: uppercase;
  letter-spacing: 0.08em;
  font-size: 12px;
  color: #6c7685;
  margin: 0;
}

h1 {
  margin: 0;
}

.muted {
  color: #4a5460;
  margin: 0;
}

ul {
  margin: 0;
  padding-left: 18px;
  color: #2a2f36;
  display: flex;
  flex-direction: column;
  gap: 6px;
}
</style>
