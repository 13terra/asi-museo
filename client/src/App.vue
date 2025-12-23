<template>
  <div class="layout">
    <header class="topbar">
      <div class="brand">
        <router-link to="/catalogo">Museo · Exposiciones</router-link>
      </div>
      <nav class="links">
        <router-link to="/catalogo" active-class="active">Catálogo</router-link>
        <router-link v-if="store.state.user.logged && (isAdmin || isGestor)" to="/expos/gestor" active-class="active">
          Mis exposiciones
        </router-link>
        <router-link v-if="store.state.user.logged && isAdmin" to="/expos/admin" active-class="active">
          Panel admin
        </router-link>
      </nav>
      <div class="actions" v-if="store.state.user.logged">
        <div class="user-pill">{{ store.state.user.login }} · {{ store.state.user.authority }}</div>
        <button class="btn-ghost" @click="desautenticarme">Salir</button>
      </div>
      <div class="actions" v-else>
        <router-link to="/login" class="btn-ghost">Iniciar sesión</router-link>
        <router-link to="/register" class="btn-primary">Registrarse</router-link>
      </div>
    </header>

    <main class="page">
      <router-view />
    </main>
  </div>
</template>

<script>
import { getStore } from "./common/store";
import auth from "./common/auth";

export default {
  data() {
    return {
      store: getStore()
    };
  },
  computed: {
    isAdmin() {
      return auth.isAdmin();
    },
    isGestor() {
      return auth.isGestor();
    }
  },
  methods: {
    desautenticarme() {
      auth.logout();
      this.$router.push("/");
    }
  },
  watch: {}
};
</script>

<style scoped>
#app,
.layout {
  font-family: "Segoe UI", Arial, sans-serif;
  color: #1f2733;
}

.topbar {
  display: grid;
  grid-template-columns: auto 1fr auto;
  align-items: center;
  gap: 12px;
  padding: 14px 18px;
  border-bottom: 1px solid #e5e9f2;
  background: #ffffff;
  position: sticky;
  top: 0;
  z-index: 10;
}

.brand a {
  font-weight: 800;
  color: #1f4b99;
  text-decoration: none;
}

.links {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.links a {
  color: #4a5460;
  text-decoration: none;
  font-weight: 700;
  padding: 6px 10px;
  border-radius: 10px;
}

.links a.active {
  background: #eef2ff;
  color: #1f4b99;
}

.actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-pill {
  background: #eef1f6;
  padding: 6px 10px;
  border-radius: 999px;
  font-weight: 700;
  color: #2a2f36;
}

.btn-primary {
  background: #1f4b99;
  color: #fff;
  border: none;
  border-radius: 10px;
  padding: 8px 12px;
  font-weight: 700;
  text-decoration: none;
}

.btn-ghost {
  border: 1px solid #d9deea;
  color: #1f4b99;
  background: #fff;
  border-radius: 10px;
  padding: 8px 12px;
  font-weight: 700;
  text-decoration: none;
}

.page {
  min-height: calc(100vh - 64px);
  background: #f5f7fb;
}

@media (max-width: 800px) {
  .topbar {
    grid-template-columns: 1fr;
    align-items: flex-start;
  }
  .links {
    width: 100%;
  }
  .actions {
    width: 100%;
    justify-content: flex-start;
  }
}
</style>
