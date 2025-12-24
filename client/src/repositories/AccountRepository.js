import HTTP from '../common/http';

export default {
  /**
   * HU2 - Autenticar usuario
   * POST /api/auth/login
   */
  async authenticate(credentials) {
    return (await HTTP.post('auth/login', credentials)).data;
  },

  /**
   * Obtener cuenta del usuario autenticado
   * GET /api/auth/me
   */
  async getAccount() {
    return (await HTTP.get('auth/me')).data;
  },

  /**
   * Cerrar sesión (opcional en backend, limpia estado servidor)
   * POST /api/auth/logout
   */
  async logout() {
    return (await HTTP.post('auth/logout')).data;
  },

  /**
   * HU1 - Registrar nuevo usuario VISITANTE
   * POST /api/auth/register
   */
  async registerAccount(user) {
    return (await HTTP.post('auth/register', {
      login: user.login,
      password: user.password,
      passwordConfirm: user.password
    })).data;
  }
};