import HTTP from '../common/http';

export default {
  /**
   * HU44 - Listar obras con filtros
   * @param {Object} filters - { autor, año, estado, tecnica }
   * @returns {Promise<Array>}
   */
  async getAll(filters = {}) {
    const params = new URLSearchParams();
    if (filters.autor) params.append('autor', filters. autor);
    if (filters.año) params.append('año', filters.año);
    if (filters.estado) params.append('estado', filters.estado);
    if (filters.tecnica) params.append('tecnica', filters.tecnica);

    return (await HTTP.get(`obras?${params.toString()}`)).data;
  },

  /**
   * HU45 - Detalle de obra
   * @param {Long} idObra
   * @returns {Promise<Object>}
   */
  async getById(idObra) {
    return (await HTTP.get(`obras/${idObra}`)).data;
  },

  /**
   * HU43 - Crear obra
   * @param {Object} obra - { titulo, autor, añoCreacion, tecnica, dimensiones, imagen, estado, idExterno }
   * @returns {Promise<Object>}
   */
  async create(obra) {
    return (await HTTP.post('obras', obra)).data;
  },

  /**
   * HU46 - Editar obra
   * @param {Long} idObra
   * @param {Object} obra
   * @returns {Promise<Object>}
   */
  async update(idObra, obra) {
    return (await HTTP.put(`obras/${idObra}`, obra)).data;
  },

  /**
   * HU47 - Eliminar obra (solo si no está en ediciones)
   * @param {Long} idObra
   * @returns {Promise<void>}
   */
  async delete(idObra) {
    return (await HTTP.delete(`obras/${idObra}`)).data;
  },

  /**
   * Subir imagen de obra
   * @param {FormData} formData - Contiene el archivo
   * @returns {Promise<String>} URL de la imagen
   */
  async uploadImage(formData) {
    return (await HTTP.post('obras/upload-image', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })).data;
  }
};