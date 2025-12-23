<<<<<<< HEAD
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
=======
import HTTP from '../common/http';

export default {
  /**
   * HU9/10/11 - Listar exposiciones
   * Admin: GET /exposiciones/admin? incluirArchivadas=true
   * Gestor: GET /exposiciones/gestor?incluirArchivadas=true
   * Público: GET /exposiciones/publico
   */
  async getAllForAdmin(incluirArchivadas = false) {
    return (await HTTP.get(`exposiciones/admin?incluirArchivadas=${incluirArchivadas}`)).data;
  },

  async getAllForGestor(incluirArchivadas = false) {
    return (await HTTP.get(`exposiciones/gestor?incluirArchivadas=${incluirArchivadas}`)).data;
  },

  async getAllPublic() {
    return (await HTTP.get('exposiciones/publico')).data;
  },

  /**
   * HU13 - Detalle de exposición
   * Admin/Gestor: GET /exposiciones/{id}/admin
   * Público: GET /exposiciones/{id}/publico
   */
  async getDetalleForAdmin(idExposicion) {
    return (await HTTP.get(`exposiciones/${idExposicion}/admin`)).data;
  },

  async getDetallePublic(idExposicion) {
    return (await HTTP.get(`exposiciones/${idExposicion}/publico`)).data;
  },

  /**
   * HU12 - Crear exposición
   * POST /exposiciones? titulo=... &descripcion=... 
   */
  async create(exposicion) {
    const params = new URLSearchParams();
    params.append('titulo', exposicion.titulo);
    if (exposicion.descripcion) params.append('descripcion', exposicion.descripcion);
    
    return (await HTTP.post(`exposiciones?${params.toString()}`)).data;
  },

  /**
   * HU14 - Editar exposición
   * PUT /exposiciones/{id}? titulo=...&descripcion=... &estado=...
   */
  async update(idExposicion, exposicion) {
    const params = new URLSearchParams();
    if (exposicion.titulo) params.append('titulo', exposicion.titulo);
    if (exposicion. descripcion) params.append('descripcion', exposicion.descripcion);
    if (exposicion.estado) params.append('estado', exposicion.estado);
    
    return (await HTTP.put(`exposiciones/${idExposicion}?${params.toString()}`)).data;
  },

  /**
   * HU15 - Archivar exposición
   * PUT /exposiciones/{id}/archivar
   */
  async archivar(idExposicion) {
    return (await HTTP.put(`exposiciones/${idExposicion}/archivar`)).data;
  },

  /**
   * HU15 - Desarchivar exposición
   * PUT /exposiciones/{id}/desarchivar
   */
  async desarchivar(idExposicion) {
    return (await HTTP.put(`exposiciones/${idExposicion}/desarchivar`)).data;
  },

  /**
   * HU16 - Eliminar exposición
   * DELETE /exposiciones/{id}
   */
  async delete(idExposicion) {
    return (await HTTP.delete(`exposiciones/${idExposicion}`)).data;
  },

  /**
   * HU59 - Buscar en catálogo público
   * GET /exposiciones/publico/buscar? termino=... 
   */
  async searchPublic(termino) {
    return (await HTTP.get(`exposiciones/publico/buscar?termino=${encodeURIComponent(termino)}`)).data;
  }
};
>>>>>>> 8044a9b ([Repositories] Completos + comprobados)
