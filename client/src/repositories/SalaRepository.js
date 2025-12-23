import HTTP from '../common/http';

export default {
  /**
   * HU38 - Listar salas
   * @returns {Promise<Array>}
   */
  async getAll() {
    return (await HTTP.get('salas')).data;
  },

  /**
   * Obtener sala por ID
   * @param {Long} idSala
   * @returns {Promise<Object>}
   */
  async getById(idSala) {
    return (await HTTP.get(`salas/${idSala}`)).data;
  },

  /**
   * HU37 - Crear sala
   * @param {Object} sala - { nombre, planta }
   * @returns {Promise<Object>}
   */
  async create(sala) {
    return (await HTTP.post('salas', sala)).data;
  },

  /**
   * HU39 - Editar sala
   * @param {Long} idSala
   * @param {Object} sala - { nombre, planta }
   * @returns {Promise<Object>}
   */
  async update(idSala, sala) {
    return (await HTTP.put(`salas/${idSala}`, sala)).data;
  },

  /**
   * HU40 - Eliminar sala (sin sesiones programadas)
   * @param {Long} idSala
   * @returns {Promise<void>}
   */
  async delete(idSala) {
    return (await HTTP.delete(`salas/${idSala}`)).data;
  }
};