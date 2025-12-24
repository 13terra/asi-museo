import HTTP from '../common/http';

export default {
  /**
   * HU4 - Listar usuarios (ADMIN)
   * GET /users?autoridad=...&estado=...
   */
  async getAll(filters = {}) {
    const params = new URLSearchParams();
    if (filters.autoridad) params.append('autoridad', filters.autoridad);
    if (filters.estado) params.append('estado', filters.estado);

    return (await HTTP.get(`users?${params.toString()}`)).data;
  },

  /**
   * Obtener usuario por ID
   * GET /users/{id}
   */
  async getById(idUser) {
    return (await HTTP.get(`users/${idUser}`)).data;
  },

  /**
   * HU5 - Crear usuario (ADMIN)
   * POST /users
   */
  async create(user) {
    return (await HTTP.post('users', user)).data;
  },

  /**
   * HU6 - Editar usuario (ADMIN)
   * PUT /users/{id}
   */
  async update(idUser, user) {
    return (await HTTP.put(`users/${idUser}`, user)).data;
  },

  /**
   * HU7 - Eliminar usuario (ADMIN)
   * DELETE /users/{id}
   */
  async delete(idUser) {
    return (await HTTP.delete(`users/${idUser}`)).data;
  },

  /**
   * HU8 - Desactivar usuario (ADMIN)
   * PUT /users/{id}/desactivar
   */
  async desactivar(idUser) {
    return (await HTTP.put(`users/${idUser}/desactivar`)).data;
  },

  /**
   * Activar usuario (ADMIN)
   * PUT /users/{id}/activar
   */
  async activar(idUser) {
    return (await HTTP.put(`users/${idUser}/activar`)).data;
  }
};