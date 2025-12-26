<template>
  <div class="detail-shell" v-if="!loading && expo">
    <div class="header">
      <router-link class="back" to="/catalogo">Volver al catálogo</router-link>
      <span class="badge" :class="badgeClass(expo.estadoExpo)">{{ expo.estadoExpo }}</span>
    </div>
    <h1>{{ expo.titulo }}</h1>
    <p class="description">{{ expo.descripcion || "Sin descripción" }}</p>

    <section class="card-box">
      <h3>Ediciones publicadas</h3>
      <div v-if="ediciones.length === 0" class="muted">Aún no hay ediciones publicadas.</div>
      <div v-for="ed in ediciones" :key="ed.idEdicion" class="edition">
        <div>
          <strong>{{ ed.nombre || ed.fechaInicio }}</strong>
          <p class="muted">{{ ed.fechaInicio }} → {{ ed.fechaFin }}</p>
        </div>
        <span class="chip" :class="edBadge(ed.estadoEdicion)">{{ ed.estadoEdicion }}</span>
      </div>
    </section>
  </div>
  <div v-else-if="error" class="loading">
    <div class="alert alert-danger" role="alert">{{ error }}</div>
  </div>
  <div v-else class="loading">
    <div class="spinner-border" role="status"></div>
  </div>
</template>

<script>
import ExpoRepository from "@/repositories/ExpoRepository";
import EdicionRepository from "@/repositories/EdicionRepository";
import { ESTADOS_EDICION } from "@/constants";

export default {
  name: "PublicExpoDetailView",
  data() {
    return {
      expo: null,
      ediciones: [],
      loading: true,
      error: ""
    };
  },
  async created() {
    await this.load();
  },
  methods: {
    badgeClass(estado) {
      const map = {
        ACTIVA: "badge-green",
        EN_PREPARACION: "badge-gray",
        BORRADOR: "badge-gray",
        ARCHIVADA: "badge-dark"
      };
      return map[estado] || "badge-gray";
    },
    edBadge(estado) {
      const map = {
        PUBLICADA: "chip chip-green",
        BORRADOR: "chip chip-gray",
        FINALIZADA: "chip chip-dark",
        CANCELADA: "chip chip-red"
      };
      return map[estado] || "chip";
    },
    async load() {
      this.loading = true;
      this.error = "";
      try {
        const id = this.$route.params.id;
        this.expo = await ExpoRepository.detailPublic(id);
        const ediciones = await EdicionRepository.getByExposicionPublic(id);
        this.ediciones = (ediciones || []).filter(ed => ed.estadoEdicion === ESTADOS_EDICION.PUBLICADA);
      } catch (e) {
        this.error = "No se pudo cargar la exposición";
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>

<style scoped>
.detail-shell {
  max-width: 900px;
  margin: 0 auto;
  padding: 32px 20px 48px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.back {
  color: #1f4b99;
  text-decoration: none;
  font-weight: 700;
}

.badge {
  align-self: flex-start;
  padding: 6px 10px;
  border-radius: 10px;
  font-weight: 700;
  font-size: 12px;
}

.badge-green {
  background: #e3f7e9;
  color: #1f7a3d;
}

.badge-gray {
  background: #eef1f6;
  color: #5b6472;
}

.badge-dark {
  background: #dfe2e7;
  color: #2a2f36;
}

.description {
  color: #4a5460;
}

.card-box {
  background: #fff;
  border-radius: 14px;
  padding: 16px 18px;
  box-shadow: 0 10px 24px rgba(0, 0, 0, 0.06);
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.edition {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #eceff5;
  padding: 8px 0;
}

.chip {
  background: #e8f0ff;
  color: #1f4b99;
  padding: 6px 10px;
  border-radius: 999px;
  font-weight: 700;
}

.chip-green {
  background: #e3f7e9;
  color: #1f7a3d;
}

.chip-gray {
  background: #eef1f6;
  color: #4a5460;
}

.chip-dark {
  background: #dfe2e7;
  color: #2a2f36;
}

.chip-red {
  background: #fff0f0;
  color: #d23b3b;
}

.muted {
  color: #6c7685;
  margin: 0;
}

.loading {
  display: flex;
  justify-content: center;
  padding: 48px 0;
}
</style>
