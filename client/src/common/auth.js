import { getStore } from "./store";
import AccountRepository from "../repositories/AccountRepository";
import { ROLES } from "@/constants";

export default {
  login,
  logout,
  register, // nuevo método para registrar nuevos usuarios
  getToken,
  isAdmin,
  isGestor,
  isVisitante,
  canManageUsers,
  isAuthenticationChecked: isAuthenticationChecked()
};

/**
 *
 * @param {} credentials - object with "login" and "password"
 * @returns
 */
async function login(credentials) {
  const response = await AccountRepository.authenticate(credentials);
  _saveToken(response.token);
  return _authenticate();
}

function logout() {
  _removeToken();
  getStore().state.user.login = "";
  getStore().state.user.authority = "";
  getStore().state.user.logged = false;
}

// HU1 (Sólo crea visitantes)
async function register(userData) {
  // call to the AccountRepository to throw a POST
  await AccountRepository.registerAccount({
    login: userData.login,
    password: userData.password
  });

  // authenticate and obtain the token
  return login({
    login: userData.login,
    password: userData.password
  });
}

/* AQUÍ ES DONDE SE COMPRUEBA SI ES ADMIN SUPONGO QUE HAY QUE USARLA PARA CUANDO HAYA 
   FUNCIONES QUE SOLO PUEDAN SER EJECUTADAS POR UN ADMIN. O TB PARA COMPROBAR QUE SEA UN USUARIO SIN PROPIEDAD DE ADMIN */
function isAdmin() {
  return getStore().state.user.authority == ROLES.ADMIN;
}

function isGestor() {
  return getStore().state.user.authority == ROLES.GESTOR;
}

function isVisitante() {
  return getStore().state.user.authority == ROLES.VISITANTE;
}

/**
 * Verifica si puede gestionar usuarios (solo ADMIN)
 */
function canManageUsers() {
  return isAdmin();
}

function getToken() {
  return localStorage.getItem("token");
}

// usamos localStorage para guardar el token, de forma
// que sea persistente (se inhabilita con el tiempo o
// al hacer logout)
function _saveToken(token) {
  localStorage.setItem("token", token);
}

function _removeToken() {
  localStorage.removeItem("token");
}

// si tenemos el token guardado, esta petición se hará
// con el filtro definido en http-common y por tanto nos
// devolverá el usuario logueado
async function _authenticate() {
  const response = await AccountRepository.getAccount();
  const store = getStore();
  store.state.user.login = response.login;
  store.state.user.authority = response.authority;
  store.state.user.logged = true;
  return store.state.user;
}

// este método devuelve una promesa que se resuelve cuando
// se haya comprobado si el token, de existir, es válido o no
function isAuthenticationChecked() {
  return new Promise((res) => {
    if (getToken()) {
      _authenticate()
        .catch(() => logout())
        .finally(() => res(true));
    } else {
      res(true);
    }
  });
}
