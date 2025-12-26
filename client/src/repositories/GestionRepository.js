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
    const params = new URLSearchParams();
    params.append('idGestor', permiso.idGestor);
    params.append('permiso', permiso.permiso);
    // Se envía doble: query + body por compatibilidad
    return (await HTTP.post(`exposiciones/${idExposicion}/permisos?${params.toString()}`, {
      idGestor: permiso.idGestor,
      permiso: permiso.permiso
    })).data;
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