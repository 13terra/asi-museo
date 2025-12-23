import HTTP from '../common/http';

export default {
  /**
   * HU17 - Listar permisos de una exposición
   * @param {Long} idExposicion
   * @returns {Promise<Array>}
   */
  async getPermisos(idExposicion) {
    return (await HTTP.get(`exposiciones/${idExposicion}/permisos`)).data;
  },

  /**
   * HU18 - Asignar permiso
   * @param {Long} idExposicion
   * @param {Object} permiso - { idUser, permiso:  "CREADOR" | "EDITOR" }
   * @returns {Promise<void>}
   */
  async asignarPermiso(idExposicion, permiso) {
    return (await HTTP.post(`exposiciones/${idExposicion}/permisos`, permiso)).data;
  },

  /**
   * HU19 - Revocar permiso
   * @param {Long} idExposicion
   * @param {Long} idUser
   * @returns {Promise<void>}
   */
  async revocarPermiso(idExposicion, idUser) {
    return (await HTTP.delete(`exposiciones/${idExposicion}/permisos/${idUser}`)).data;
  }
};