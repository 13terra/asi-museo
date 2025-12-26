import HTTP from '../common/http';

export default {
  /**
   * HU32 - Listar sesiones de una edición
   * GET /api/ediciones/{idEdicion}/sesiones? fecha=...&idSala=...&estado=...
   */
  async getByEdicion(idEdicion, filters = {}) {
    const params = new URLSearchParams();
    if (filters.fecha) params.append('fecha', filters.fecha);
    if (filters.idSala) params.append('idSala', filters.idSala);
    if (filters.estado) params.append('estado', filters.estado);

    return (await HTTP.get(`ediciones/${idEdicion}/sesiones?${params.toString()}`)).data;
  },

  /**
   * HU33 - Detalle de sesión (ADMIN/GESTOR)
   * GET /api/sesiones/{id}/admin
   */
  async getDetalleForAdmin(idSesion) {
    return (await HTTP.get(`sesiones/${idSesion}/admin`)).data;
  },

  /**
   * HU33 - Detalle de sesión (PÚBLICO)
   * GET /api/sesiones/{id}/publico
   */
  async getDetallePublic(idSesion) {
    return (await HTTP.get(`sesiones/${idSesion}/publico`)).data;
  },

  /**
   * Alias para obtener detalle público (usada en la UI de reserva)
   */
  async getById(idSesion) {
    return this.getDetallePublic(idSesion);
  },

  /**
   * HU31 - Crear sesión
   * POST /api/ediciones/{idEdicion}/sesiones?fecha=...&horaInicio=...&horaFin=...&aforo=...&idSalas=... 
   */
  async create(idEdicion, sesion) {
    const params = new URLSearchParams();
    params.append('fecha', sesion.fecha);
    // Normaliza hora a HH:mm:ss
    const hInicio = sesion.horaInicio && sesion.horaInicio.length === 5 ? `${sesion.horaInicio}:00` : sesion.horaInicio;
    const hFin = sesion.horaFin && sesion.horaFin.length === 5 ? `${sesion.horaFin}:00` : sesion.horaFin;
    params.append('horaInicio', hInicio);
    params.append('horaFin', hFin);
    params.append('aforo', sesion.aforo);
    // idSalas: enviar en distintos nombres
    const ids = sesion.idSalas || [];
    ids.forEach(id => {
      params.append('idSalas', id);
      params.append('idSala', id);
    });
    if (ids.length) {
      params.append('salas', ids.join(','));
    }
    return (await HTTP.post(`ediciones/${idEdicion}/sesiones?${params.toString()}`, { idSalas: ids, idSala: ids })).data;
  },

  /**
   * HU34 - Editar sesión
   * PUT /api/sesiones/{id}
   * Body: SesionUpdateDTO (JSON)
   */
  async update(idSesion, sesion) {
    const payload = { ...sesion };
    if (payload.horaInicio && payload.horaInicio.length === 5) payload.horaInicio = `${payload.horaInicio}:00`;
    if (payload.horaFin && payload.horaFin.length === 5) payload.horaFin = `${payload.horaFin}:00`;
    if (payload.idSalas) {
      payload.idSala = payload.idSalas;
    }
    return (await HTTP.put(`sesiones/${idSesion}`, payload)).data;
  },

  /**
   * HU35 - Cancelar sesión
   * PUT /api/sesiones/{id}/cancelar
   */
  async cancelar(idSesion) {
    return (await HTTP.put(`sesiones/${idSesion}/cancelar`)).data;
  },

  /**
   * HU36 - Eliminar sesión
   * DELETE /api/sesiones/{id}
   */
  async delete(idSesion) {
    return (await HTTP.delete(`sesiones/${idSesion}`)).data;
  },

  /**
   * Listar salas asignadas a sesión
   * GET /api/sesiones/{idSesion}/salas
   */
  async getSalasAsignadas(idSesion) {
    return (await HTTP.get(`sesiones/${idSesion}/salas`)).data;
  },

  /**
   * HU41 - Asignar salas a sesión
   * POST /api/sesiones/{idSesion}/salas?idSalas=...&idSalas=...
   */
  async asignarSalas(idSesion, idSalas) {
    const params = new URLSearchParams();
    idSalas.forEach(id => params.append('idSalas', id));
    
    return (await HTTP.post(`sesiones/${idSesion}/salas?${params.toString()}`)).data;
  },

  /**
   * HU42 - Desasignar sala de sesión
   * DELETE /api/sesiones/{idSesion}/salas/{idSala}
   */
  async desasignarSala(idSesion, idSala) {
    return (await HTTP.delete(`sesiones/${idSesion}/salas/${idSala}`)).data;
  }
};