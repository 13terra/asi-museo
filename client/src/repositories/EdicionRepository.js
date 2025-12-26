import HTTP from '../common/http';

export default {
  /**
   * HU20 - Crear edición
   * POST /exposiciones/{idExposicion}/ediciones? fechaInicio=...&fechaFin=... 
   */
  async create(idExposicion, edicion) {
    const params = new URLSearchParams();
    params.append('fechaInicio', edicion.fechaInicio);
    params.append('fechaFin', edicion.fechaFin);
    
    return (await HTTP.post(`exposiciones/${idExposicion}/ediciones?${params.toString()}`)).data;
  },

  /**
   * Listar ediciones de una exposición
   * GET /exposiciones/{idExposicion}/ediciones
   */
  async getByExposicion(idExposicion) {
    return (await HTTP.get(`exposiciones/${idExposicion}/ediciones`)).data;
  },

  // Alias usado en vistas (gestor/admin y público)
  async listByExpo(idExposicion) {
    return this.getByExposicion(idExposicion);
  },

  /**
   * HU21 - Detalle de edición
   * Admin/Gestor: GET /ediciones/{id}/admin
   * Público: GET /ediciones/{id}/publico
   */
  async getDetalleForAdmin(idEdicion) {
    return (await HTTP.get(`ediciones/${idEdicion}/admin`)).data;
  },

  async getDetallePublic(idEdicion) {
    return (await HTTP.get(`ediciones/${idEdicion}/publico`)).data;
  },

  /**
   * HU22 - Editar edición
   * PUT /ediciones/{id}?fechaInicio=...&fechaFin=...&estado=...
   */
  async update(idEdicion, edicion) {
    const params = new URLSearchParams();
    if (edicion.fechaInicio) params.append('fechaInicio', edicion.fechaInicio);
    if (edicion.fechaFin) params.append('fechaFin', edicion.fechaFin);
    if (edicion.estado) params.append('estado', edicion.estado);
    if (edicion.nombre) params.append('nombre', edicion.nombre);
    
    return (await HTTP.put(`ediciones/${idEdicion}?${params.toString()}`)).data;
  },

  /**
   * HU23 - Publicar edición
   * PUT /ediciones/{id}/publicar
   */
  async publicar(idEdicion) {
    return (await HTTP.put(`ediciones/${idEdicion}/publicar`)).data;
  },

  /**
   * HU25 - Cancelar edición
   * PUT /ediciones/{id}/cancelar
   */
  async cancelar(idEdicion) {
    return (await HTTP.put(`ediciones/${idEdicion}/cancelar`)).data;
  },

  /**
   * HU26 - Eliminar edición (solo BORRADOR)
   * DELETE /ediciones/{id}
   */
  async delete(idEdicion) {
    return (await HTTP.delete(`ediciones/${idEdicion}`)).data;
  }
};