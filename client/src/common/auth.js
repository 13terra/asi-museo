import { getStore } from "./store";
import AccountRepository from "../repositories/AccountRepository";
import { ROLES } from "@/constants";

export default {
  login,
  logout,
  register,
  getToken,
  isAdmin,
  isGestor,
  isVisitante,
  canManageUsers,
  isAuthenticationChecked // ✅ Sin paréntesis
};

async function login(credentials) {
  const response = await AccountRepository.authenticate(credentials);
  _saveToken(response.token);
  return _authenticate();
}

// ✅ CORRECCIÓN:  Logout asíncrono completo
async function logout() {
  try {
    // Intentar notificar al backend (opcional, puede fallar)
    await AccountRepository.logout();
  } catch (e) {
    // Ignorar errores del backend, limpiar estado igual
    console.warn("Backend logout failed, cleaning local state anyway");
  }
  
  // Limpiar token
  _removeToken();
  
  // Limpiar estado del usuario
  const state = getStore().state.user;
  state.id = null;
  state.login = "";
  state.authority = "";
  state.estado = "";
  state.logged = false;
  state.idUser = null;
}

async function register(userData) {
  await AccountRepository.registerAccount({
    login: userData.login,
    password: userData.password,
    passwordConfirm: userData.passwordConfirm ??  userData.password
  });

  return login({
    login: userData.login,
    password: userData.password
  });
}

function isAdmin() {
  return getStore().state.user.authority === ROLES.ADMIN;
}

function isVisitante() {
  return getStore().state.user.authority === ROLES.VISITANTE;
}

function isGestor() {
  return getStore().state.user.authority === ROLES.GESTOR;
}

function canManageUsers() {
  return isAdmin();
}

function getToken() {
  return localStorage.getItem("token");
}

function _saveToken(token) {
  localStorage.setItem("token", token);
}

function _removeToken() {
  localStorage.removeItem("token");
}

async function _authenticate() {
  const response = await AccountRepository.getAccount();
  const store = getStore();
  store.state.user.id = response.id;
  store.state.user.idUser = response.idUser || response.id;
  store.state.user.login = response.login;
  store.state.user.authority = response.authority || response.autoridad;
  store.state.user.estado = response.estado;
  store.state.user.gestorRol = response.permisoGestor || response.gestorRol || response.tipoGestor || '';
  store.state.user.logged = true;
  return store.state.user;
}

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