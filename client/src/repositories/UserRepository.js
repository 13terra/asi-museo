import HTTP from "../common/http";

const resource = "users"; //define a que endpoint hace las peticiones

export default {
  async findAll() {
    return (await HTTP.get(`${resource}`)).data; //lista básica
  },
  async findOne(id) {
    return (await HTTP.get(`${resource}/${id}`)).data; //user individual para saber numNotas
  }
};
