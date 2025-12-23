import HTTP from "../common/http";

const resource = "exposiciones";

export default {
  async listAdmin({ incluirArchivadas = false } = {}) {
    return (await HTTP.get(`${resource}/admin`, { params: { incluirArchivadas } })).data;
  },

  async listGestor({ incluirArchivadas = false } = {}) {
    return (await HTTP.get(`${resource}/gestor`, { params: { incluirArchivadas } })).data;
  },

  async listPublic() {
    return (await HTTP.get(`${resource}/publico`)).data;
  },

  async searchPublic(term) {
    return (await HTTP.get(`${resource}/publico/buscar`, { params: { termino: term } })).data;
  },

  async create({ titulo, descripcion }) {
    const params = new URLSearchParams();
    params.append("titulo", titulo);
    if (descripcion) params.append("descripcion", descripcion);
    return (await HTTP.post(resource, params)).data;
  },

  async detailAdmin(idExposicion) {
    return (await HTTP.get(`${resource}/${idExposicion}/admin`)).data;
  },

  async detailPublic(idExposicion) {
    return (await HTTP.get(`${resource}/${idExposicion}/publico`)).data;
  },

  async update(idExposicion, payload) {
    const params = new URLSearchParams();
    if (payload.titulo) params.append("titulo", payload.titulo);
    if (payload.descripcion) params.append("descripcion", payload.descripcion);
    if (payload.estado) params.append("estado", payload.estado);
    return (await HTTP.put(`${resource}/${idExposicion}`, params)).data;
  },

  async archivar(idExposicion) {
    return (await HTTP.put(`${resource}/${idExposicion}/archivar`)).data;
  },

  async desarchivar(idExposicion) {
    return (await HTTP.put(`${resource}/${idExposicion}/desarchivar`)).data;
  },

  async remove(idExposicion) {
    return (await HTTP.delete(`${resource}/${idExposicion}`)).data;
  }
};
