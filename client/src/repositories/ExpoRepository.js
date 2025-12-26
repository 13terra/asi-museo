import HTTP from '../common/http';

export default {
  /**
   * HU9 - Listar exposiciones para ADMIN
   * GET /api/exposiciones/admin? incluirArchivadas=true|false
   */
  async getAllForAdmin(incluirArchivadas = false) {
    return (await HTTP.get(`exposiciones/admin?incluirArchivadas=${incluirArchivadas}`)).data;
  },

  /**
   * HU10 - Listar exposiciones para GESTOR
   * GET /api/exposiciones/gestor? incluirArchivadas=true|false
   */
  async getAllForGestor(incluirArchivadas = false) {
    return (await HTTP.get(`exposiciones/gestor?incluirArchivadas=${incluirArchivadas}`)).data;
  },

  // Alias usado en vistas
  async listGestor(opts = {}) {
    const incluirArchivadas = typeof opts === 'object' ? !!opts.incluirArchivadas : !!opts;
    return this.getAllForGestor(incluirArchivadas);
  },

  /**
   * HU11 - Listar exposiciones públicas
   * GET /api/exposiciones/publico
   */
  async getAllPublic() {
    return (await HTTP.get('exposiciones/publico')).data;
  },

  // Alias usado en vistas
  async listPublic() {
    return this.getAllPublic();
  },

  /**
   * HU59 - Buscar exposiciones en catálogo público
   * GET /api/exposiciones/publico/buscar? termino=... 
   */
  async searchPublic(termino) {
    return (await HTTP.get(`exposiciones/publico/buscar?termino=${encodeURIComponent(termino)}`)).data;
  },

  /**
   * HU13 - Detalle de exposición (ADMIN/GESTOR)
   * GET /api/exposiciones/{id}/admin
   */
  async getDetalleForAdmin(idExposicion) {
    return (await HTTP.get(`exposiciones/${idExposicion}/admin`)).data;
  },

  // Alias usado en vistas
  async detailAdmin(idExposicion) {
    return this.getDetalleForAdmin(idExposicion);
  },

  /**
   * HU13 - Detalle de exposición (PÚBLICO)
   * GET /api/exposiciones/{id}/publico
   */
  async getDetallePublic(idExposicion) {
    return (await HTTP.get(`exposiciones/${idExposicion}/publico`)).data;
  },

  // Alias usado en vistas
  async detailPublic(idExposicion) {
    return this.getDetallePublic(idExposicion);
  },

  /**
   * HU12 - Crear exposición
   * POST /api/exposiciones? titulo=...&descripcion=... 
   */
  async create(exposicion) {
    const params = new URLSearchParams();
    params.append('titulo', exposicion.titulo);
    if (exposicion.descripcion) params.append('descripcion', exposicion.descripcion);
    
    return (await HTTP.post(`exposiciones?${params.toString()}`)).data;
  },

  /**
   * HU14 - Editar exposición
   * PUT /api/exposiciones/{id}? titulo=...&descripcion=... &estado=...
   */
  async update(idExposicion, exposicion) {
    const params = new URLSearchParams();
    if (exposicion.titulo) params.append('titulo', exposicion.titulo);
    if (exposicion. descripcion) params.append('descripcion', exposicion.descripcion);
    if (exposicion.estado) params.append('estado', exposicion.estado);
    
    const query = params.toString();
    const url = query ? `exposiciones/${idExposicion}?${query}` : `exposiciones/${idExposicion}`;
    return (await HTTP.put(url)).data;
  },

  /**
   * HU15 - Archivar exposición
   * PUT /api/exposiciones/{id}/archivar
   */
  async archivar(idExposicion) {
    return (await HTTP.put(`exposiciones/${idExposicion}/archivar`)).data;
  },

  /**
   * HU15 - Desarchivar exposición
   * PUT /api/exposiciones/{id}/desarchivar
   */
  async desarchivar(idExposicion) {
    return (await HTTP.put(`exposiciones/${idExposicion}/desarchivar`)).data;
  },

  /**
   * HU16 - Eliminar exposición
   * DELETE /api/exposiciones/{id}
   */
  async delete(idExposicion) {
    return (await HTTP.delete(`exposiciones/${idExposicion}`)).data;
  }
};