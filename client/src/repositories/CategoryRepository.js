import HTTP from "@/common/http";

const resource = "categories";

export default {
  async findAll() {
    return (await HTTP.get(resource)).data;
  },
  async findOne(id) {
    return (await HTTP.get(`${resource}/${id}`)).data;
  },
  async delete(id) {
    await HTTP.delete(`${resource}/${id}`); //no tiene que devolver nada
  },
  //mandar en el body {id: ..., name: ...}
  async create(category) {
    return (await HTTP.post(`${resource}`, category)).data;
  },
  //id en el path + body lo mismo que create
  async update(category) {
    return (await HTTP.put(`${resource}/${category.id}`, category)).data;
  }
};
