import HTTP from '../common/http';

export default {
  /**
   * HU50 - Listar tipos de entrada
   * @returns {Promise<Array>}
   */
  async getAll() {
    return (await HTTP.get('tipos-entrada')).data;
  },

  /**
   * Obtener tipo de entrada por ID
   * @param {Long} idTipoEntrada
   * @returns {Promise<Object>}
   */
  async getById(idTipoEntrada) {
    return (await HTTP.get(`tipos-entrada/${idTipoEntrada}`)).data;
  }
};