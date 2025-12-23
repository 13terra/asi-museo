import HTTP from '../common/http';

export default {
  /**
   * HU52 - Listar mis reservas (visitante)
   * GET /reservas/mis-reservas?estado=...
   */
  async getMisReservas(filters = {}) {
    const params = new URLSearchParams();
    if (filters.estado) params.append('estado', filters.estado);

    return (await HTTP.get(`reservas/mis-reservas?${params.toString()}`)).data;
  },

  /**
   * HU57 - Listar reservas por edición (gestor/admin)
   * GET /ediciones/{idEdicion}/reservas? sesion=...&estado=...&fecha=...
   */
  async getByEdicion(idEdicion, filters = {}) {
    const params = new URLSearchParams();
    if (filters.sesion) params.append('sesion', filters.sesion);
    if (filters.estado) params.append('estado', filters.estado);
    if (filters.fecha) params.append('fecha', filters.fecha);

    return (await HTTP.get(`ediciones/${idEdicion}/reservas?${params.toString()}`)).data;
  },

  /**
   * HU53/58 - Detalle de reserva
   * GET /reservas/{id}
   */
  async getById(idReserva) {
    return (await HTTP.get(`reservas/${idReserva}`)).data;
  },

  /**
   * HU51 - Crear reserva
   * POST /sesiones/{idSesion}/reservas
   * Body: { comprador:  {}, entradas: [] }
   */
  async create(idSesion, reserva) {
    return (await HTTP.post(`sesiones/${idSesion}/reservas`, reserva)).data;
  },

  /**
   * HU54 - Cancelar reserva
   * PUT /reservas/{id}/cancelar
   */
  async cancelar(idReserva) {
    return (await HTTP.put(`reservas/${idReserva}/cancelar`)).data;
  }
};