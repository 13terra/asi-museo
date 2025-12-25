import HTTP from '../common/http';

export default {
  /**
   * HU28 - Listar piezas expuestas de una edición
   * @param {Long} idEdicion
   * @param {Object} filters - { sala }
   * @returns {Promise<Array>}
   */
  async getByEdicion(idEdicion, filters = {}) {
    const params = new URLSearchParams();
    if (filters.sala) params.append('sala', filters.sala);

    return (await HTTP.get(`ediciones/${idEdicion}/piezas-expuestas?${params.toString()}`)).data;
  },

  /**
   * HU27 - Añadir pieza expuesta
   * @param {Long} idEdicion
   * @param {Object} pieza - { idObra, idSala, orden, textoCuratorial, portada }
   * @returns {Promise<Object>}
   */
  async create(idEdicion, pieza) {
    return (await HTTP.post(`ediciones/${idEdicion}/piezas-expuestas`, pieza)).data;
  },

  /**
   * HU29 - Editar pieza expuesta
   * @param {Long} idPiezaExpuesta
   * @param {Object} pieza - { idSala, orden, textoCuratorial, portada }
   * @returns {Promise<Object>}
   */
  async update(idPiezaExpuesta, pieza) {
    return (await HTTP.put(`piezas-expuestas/${idPiezaExpuesta}`, pieza)).data;
  },

  /**
   * HU30 - Eliminar pieza expuesta
   * @param {Long} idPiezaExpuesta
   * @returns {Promise<void>}
   */
  async delete(idPiezaExpuesta) {
    return (await HTTP.delete(`piezas-expuestas/${idPiezaExpuesta}`)).data;
  }
};