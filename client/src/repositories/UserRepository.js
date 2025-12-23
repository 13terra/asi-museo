import HTTP from "../common/http";

const resource = "users"; // endpoint base

export default {
  async findAll({ autoridad, estado } = {}) {
    const params = {};
    if (autoridad) params.autoridad = autoridad;
    if (estado) params.estado = estado;
    return (await HTTP.get(`${resource}`, { params })).data; //lista básica
  },
  async findOne(id) {
    return (await HTTP.get(`${resource}/${id}`)).data; //user individual para saber numNotas
  },
  async delete(id) {
    return (await HTTP.delete(`${resource}/${id}`)).data; //eliminamos un user
  },
  async deactivate(id) {
    return (await HTTP.put(`${resource}/${id}/deactivate`)).data;
  },
  async activate(id) {
    return (await HTTP.put(`${resource}/${id}/activate`)).data;
  }
};
