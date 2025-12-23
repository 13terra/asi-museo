import HTTP from "../common/http";

export default {
  async listByExpo(idExposicion) {
    return (await HTTP.get(`exposiciones/${idExposicion}/ediciones`)).data;
  },

  async create(idExposicion, { fechaInicio, fechaFin }) {
    const params = new URLSearchParams();
    params.append("fechaInicio", fechaInicio);
    params.append("fechaFin", fechaFin);
    return (await HTTP.post(`exposiciones/${idExposicion}/ediciones`, params)).data;
  },

  async detailAdmin(idEdicion) {
    return (await HTTP.get(`ediciones/${idEdicion}/admin`)).data;
  },

  async update(idEdicion, { fechaInicio, fechaFin, estado }) {
    const params = new URLSearchParams();
    if (fechaInicio) params.append("fechaInicio", fechaInicio);
    if (fechaFin) params.append("fechaFin", fechaFin);
    if (estado) params.append("estado", estado);
    return (await HTTP.put(`ediciones/${idEdicion}`, params)).data;
  }
};
