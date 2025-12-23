import HTTP from '../common/http';

export default {
  /**
   * HU48 - Buscar obras en The MET
   * @param {String} query - Término de búsqueda
   * @returns {Promise<Array>}
   */
  async buscarObras(query) {
    return (await HTTP.get(`met/obras?query=${encodeURIComponent(query)}`)).data;
  },

  /**
   * HU49 - Obtener detalle de obra desde The MET
   * @param {Integer} metId - ID externo de The MET
   * @returns {Promise<Object>}
   */
  async getObraDetalle(metId) {
    return (await HTTP.get(`met/obras/${metId}`)).data;
  }
};