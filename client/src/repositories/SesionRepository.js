import HTTP from '../common/http';

export default {
  /**
   * HU32 - Listar sesiones de una edición
   * GET /ediciones/{idEdicion}/sesiones? fecha=...&sala=...&estado=...
   */
  async getByEdicion(idEdicion, filters = {}) {
    const params = new URLSearchParams();
    if (filters.fecha) params.append('fecha', filters.fecha);
    if (filters.sala) params.append('sala', filters. sala);
    if (filters.estado) params.append('estado', filters.estado);

    return (await HTTP.get(`ediciones/${idEdicion}/sesiones?${params.toString()}`)).data;
  },

  /**
   * HU33 - Detalle de sesión
   * GET /sesiones/{id}
   */
  async getById(idSesion) {
    return (await HTTP.get(`sesiones/${idSesion}`)).data;
  },

  /**
   * HU31 - Crear sesión
   * POST /ediciones/{idEdicion}/sesiones
   * Body: { fecha, horaInicio, horaFin, aforo, salas:  [{ idSala, orden }] }
   */
  async create(idEdicion, sesion) {
    return (await HTTP.post(`ediciones/${idEdicion}/sesiones`, sesion)).data;
  },

  /**
   * HU34 - Editar sesión
   * PUT /sesiones/{id}
   */
  async update(idSesion, sesion) {
    return (await HTTP.put(`sesiones/${idSesion}`, sesion)).data;
  },

  /**
   * HU35 - Cancelar sesión
   * PUT /sesiones/{id}/cancelar
   */
  async cancelar(idSesion) {
    return (await HTTP.put(`sesiones/${idSesion}/cancelar`)).data;
  },

  /**
   * HU36 - Eliminar sesión (sin reservas)
   * DELETE /sesiones/{id}
   */
  async delete(idSesion) {
    return (await HTTP.delete(`sesiones/${idSesion}`)).data;
  },

  /**
   * HU41 - Asignar sala a sesión
   * POST /sesiones/{idSesion}/salas
   */
  async asignarSala(idSesion, sala) {
    return (await HTTP.post(`sesiones/${idSesion}/salas`, sala)).data;
  },

  /**
   * HU42 - Desasignar sala de sesión
   * DELETE /sesiones/{idSesion}/salas/{idSala}
   */
  async desasignarSala(idSesion, idSala) {
    return (await HTTP.delete(`sesiones/${idSesion}/salas/${idSala}`)).data;
  }
};