import HTTP from '../common/http';

export default {
  /**
   * HU52 - Listar mis reservas
   * GET /api/mis-reservas? estado=... 
   */
  async getMisReservas(filters = {}) {
    const params = new URLSearchParams();
    if (filters.estado) params.append('estado', filters.estado);

    return (await HTTP.get(`mis-reservas?${params.toString()}`)).data;
  },

  /**
   * HU57 - Listar reservas por edición (ADMIN/GESTOR)
   * GET /api/ediciones/{idEdicion}/reservas? estado=...&fecha=...
   */
  async getByEdicion(idEdicion, filters = {}) {
    const params = new URLSearchParams();
    if (filters.estado) params.append('estado', filters.estado);
    if (filters.fecha) params.append('fecha', filters. fecha);

    return (await HTTP.get(`ediciones/${idEdicion}/reservas?${params. toString()}`)).data;
  },

  /**
   * HU53/58 - Detalle de MI reserva
   * GET /api/mis-reservas/{id}
   */
  async getById(idReserva) {
    return (await HTTP.get(`mis-reservas/${idReserva}`)).data;
  },

  /**
   * HU51 - Crear reserva
   * POST /api/reservas
   * Body: { idSesion, comprador:  {... }, entradas: [...] }
   */
  async create(idSesion, reserva) {
    return (await HTTP.post('reservas', {
      idSesion,
      ... reserva
    })).data;
  },

  /**
   * HU54 - Cancelar MI reserva
   * PUT /api/mis-reservas/{id}/cancelar
   */
  async cancelar(idReserva) {
    return (await HTTP.put(`mis-reservas/${idReserva}/cancelar`)).data;
  }
};