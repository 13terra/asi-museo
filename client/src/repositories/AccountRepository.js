import HTTP from "../common/http";

export default {
  async authenticate(credentials) {
    return (await HTTP.post(`auth/login`, credentials)).data;
  },

  async getAccount() {
    return (await HTTP.get(`auth/me`)).data;
  },

  async registerAccount(user) {
    return (await HTTP.post(`auth/register`, user)).data;
  },

  async logout() {
    return (await HTTP.post(`auth/logout`)).data;
  }
};
