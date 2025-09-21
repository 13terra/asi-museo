import HTTP from "../common/http";

export default {
  async authenticate(credentials) {
    return (await HTTP.post(`authenticate`, credentials)).data;
  },

  async getAccount() {
    /* Lo del .data era como cuando en ISD haciamos el .json 
    lo que pasa es que la petición devuelve muchas más cosas de las que necesitamos  CREOOOO */
    return (await HTTP.get(`account`)).data;
  },

  async registerAccount(user) {
    return (await HTTP.post(`register`, user)).data;
  }
};
