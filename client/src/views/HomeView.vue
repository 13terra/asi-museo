<template>
  <div class="home-shell">
    <div class="hero">
      <div>
        <p class="eyebrow">Museo · Gestión integral</p>
        <h1>Controla las exposiciones, ediciones y reservas en un solo lugar</h1>
        <p class="lead">
          Catálogo público para visitantes, paneles para gestores y administración centralizada.
        </p>
        <div class="cta">
          <router-link to="/catalogo" class="btn-primary">Ver catálogo público</router-link>
          <router-link to="/login" class="btn-ghost">Iniciar sesión</router-link>
          <router-link to="/register" class="btn-ghost">Registrarse</router-link>
        </div>
      </div>
      <div class="hero-card">
        <h3>Accesos rápidos</h3>
        <ul>
          <li>Paneles de exposiciones por rol</li>
          <li>Detalle de exposiciones y ediciones</li>
          <li>Permisos y gestión de equipo</li>
        </ul>
        <router-link to="/expos/admin" class="btn-link">Ir a panel admin</router-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
.home-shell {
  max-width: 1200px;
  margin: 0 auto;
  padding: 48px 20px;
}

.hero {
  background: #f7f9fd;
  border: 1px solid #e4e8f2;
  border-radius: 18px;
  padding: 28px;
  display: grid;
  grid-template-columns: 1.5fr 1fr;
  gap: 20px;
  align-items: center;
}

@media (max-width: 900px) {
  .hero {
    grid-template-columns: 1fr;
  }
}

.eyebrow {
  text-transform: uppercase;
  letter-spacing: 0.08em;
  font-size: 12px;
  color: #6c7685;
  margin: 0 0 8px;
}

h1 {
  font-size: 32px;
  margin: 0 0 12px;
}

.lead {
  color: #4a5460;
  margin: 0 0 18px;
}

.cta {
  display: flex;
  gap: 10px;
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

.hero-card {
  background: #fff;
  border-radius: 14px;
  padding: 18px;
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.06);
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.hero-card h3 {
  margin: 0;
}

.hero-card ul {
  margin: 0;
  padding-left: 18px;
  color: #4a5460;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.btn-link {
  color: #1f4b99;
  font-weight: 700;
  text-decoration: none;
}
</style>
