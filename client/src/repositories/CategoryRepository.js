import HTTP from "@/common/http";

const resource = "categories";

export default {
  async findAll() {
    return (await HTTP.get(resource)).data;
  } /*,
  async create(name) {
    return (await HTTP.post(resource, { name })).data;
  }*/
};
