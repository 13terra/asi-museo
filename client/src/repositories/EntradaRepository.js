import HTTP from '../common/http';

export default {
  /**
   * HU55 - Listar mis entradas
   * @returns {Promise<Array>}
   */
  async getMisEntradas() {
    return (await HTTP.get('mis-entradas')).data;
  },

  /**
   * HU56 - Detalle de entrada
   * @param {Long} idEntrada
   * @returns {Promise<Object>}
   */
  async getById(idEntrada) {
    return (await HTTP.get(`mis-entradas/${idEntrada}`)).data;
  },

  /**
   * Listar entradas por reserva
   * @param {Long} idReserva
   * @returns {Promise<Array>}
   */
  async getByReserva(idReserva) {
    return (await HTTP.get(`reservas/${idReserva}/entradas`)).data;
  }
};