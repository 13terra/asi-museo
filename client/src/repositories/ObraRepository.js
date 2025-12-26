import HTTP from "../common/http";

export default {
  /**
   * HU44 - Listar obras con filtros
   */
  async getAll(filters = {}) {
    const params = new URLSearchParams();
    if (filters.autor) params.append("autor", filters.autor);
    if (filters.año) params.append("año", filters.año);
    if (filters.estado) params.append("estado", filters.estado);
    if (filters.tecnica) params.append("tecnica", filters.tecnica);

    return (await HTTP.get(`obras?${params.toString()}`)).data;
  },

  async getById(idObra) {
    return (await HTTP.get(`obras/${idObra}`)).data;
  },

  /**
   * Helper interno para construir el FormData
   */
  _buildFormData(obra) {
    const formData = new FormData();

    formData.append("titulo", obra.titulo || "");
    formData.append("autor", obra.autor || "");
    formData.append("tecnica", obra.tecnica || "");
    formData.append("dimensiones", obra.dimensiones || "");

    const anio = obra.añoCreacion || obra.anioCreacion || obra.anoCreacion;
    if (anio) formData.append("anoCreacion", anio);

    if (obra.estado) formData.append("estado", obra.estado);
    if (obra.idExterno) formData.append("idExterno", obra.idExterno);

    // 🔴 LÓGICA HÍBRIDA DE IMAGEN 🔴
    if (obra.imagen instanceof File) {
      // Caso A: Archivo físico
      formData.append("imagenFile", obra.imagen);
    } else if (typeof obra.imagen === "string" && obra.imagen.trim() !== "") {
      // Caso B: URL
      formData.append("imagenUrlMET", obra.imagen);
    }

    return formData;
  },

  /**
   * HU43 - Crear obra (Soporta Archivo y URL)
   */
  async create(obra) {
    const formData = this._buildFormData(obra);
    // IMPORTANTE: No ponemos header manual, Axios/Browser lo pone automático con el boundary
    return (await HTTP.post("obras", formData)).data;
  },

  /**
   * HU46 - Editar obra (Soporta Archivo y URL)
   */
  async update(idObra, obra) {
    const formData = this._buildFormData(obra);
    // IMPORTANTE: No ponemos header manual
    return (await HTTP.put(`obras/${idObra}`, formData)).data;
  },

  async delete(idObra) {
    return (await HTTP.delete(`obras/${idObra}`)).data;
  }
};
