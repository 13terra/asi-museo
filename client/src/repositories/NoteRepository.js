import HTTP from "@/common/http";

const resource = "notes"; //define a que endpoint hace las peticiones

// Para cada nota lo que hace es aplicarle dicha funcion
function applyDate(note) {
  note.timestamp = new Date(note.timestamp);
  return note;
}

// Aquí se supone que debería de registrar todas las operaciones sobre notas
export default {
  async findAll() {
    const response = await HTTP.get(`${resource}`);
    response.data.forEach(applyDate);
    return response.data;
  },
  // Básicamente esto nos va a permitir traernos UNA NOTA (p.e. para ver la nota individual)
  async findOne(id) {
    const response = await HTTP.get(`${resource}/${id}`); //esto es una peticion a un servicio REST

    return applyDate(response.data); //Aplicamos la transformación de la fecha y devolvemos la nota
  },
  // funcion para crear una nota
  async create(note) {
    // note: {titulo?, contenido?, categorias?}
    const response = await HTTP.post(`${resource}`, note);
    return applyDate(response.data); // aprovecho la función que ya está creada
  },
  // note: { id, title?, content?, categories? (array de {id,name?}), archived? }
  async update(note) {
    //payload : {id, title, content, archived, categories}
    const response = await HTTP.put(`${resource}/${note.id}`, note);
    return applyDate(response.data);
  },
  async delete(id) {
    await HTTP.delete(`${resource}/${id}`);
  }
};
