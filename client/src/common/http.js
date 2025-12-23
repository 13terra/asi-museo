import axios from "axios";
import { BACKEND_URL } from "../constants.js";
import auth from "./auth.js";
import { setNotification } from "./store.js";

/* LO QUE HACE ES MANDAR TODAS LAS PETICIONES A ESTA URL 
  DEFINE UNA CONSTANTE Y CREA LA URL BASE DE TODAS LAS PETICIONES */
const HTTP = axios.create({
  baseURL: BACKEND_URL
});

/**
 * Handler cuando el usuario no tiene autorización
 */
const onUnauthorized = () => {
  console.error("Access denied!");

  auth.logout();
};

const onResponseSuccess = (response) => response;

/**
 * Handler global de errores HTTP
 * CRÍTICO: Jamás mostrar SQLExceptions o trazas Java al usuario
 */
const onResponseFailure = (err) => {
  // Si no hay respuesta del servidor (error de red)
  if (! err.response) {
    console.error('Network error:', err);
    setNotification(ERROR_MESSAGES.NETWORK_ERROR, 'error');
    return Promise.reject(err);
  }

  const status = err.response.status;
  const url = err.config.url;

  // No mostrar error de autenticación si estamos en /authenticate o /register
  if (url.includes('authenticate') || url.includes('register')) {
    return Promise.reject(err);
  }

  // Manejo según código de estado
  switch (status) {
    case 401:
      // Sesión expirada o no autenticado
      setNotification(ERROR_MESSAGES.SESSION_EXPIRED, 'error');
      onUnauthorized();
      break;

    case 403:
      // Sin permisos suficientes
      setNotification(ERROR_MESSAGES.UNAUTHORIZED, 'error');
      break;

    case 404:
      // Recurso no encontrado
      setNotification(ERROR_MESSAGES.NOT_FOUND, 'error');
      break;

    case 400:
      // Validación fallida
      const errorMsg = err.response.data?.message || ERROR_MESSAGES.VALIDATION_ERROR;
      setNotification(errorMsg, 'error');
      break;

    case 500:
    case 502:
    case 503:
      // Error del servidor - NUNCA mostrar trazas técnicas
      console.error('Server error:', err.response.data);
      setNotification(ERROR_MESSAGES. GENERIC, 'error');
      break;

    default:
      setNotification(ERROR_MESSAGES.GENERIC, 'error');
  }

  return Promise.reject(err);
};


// en cada request hay que añadir el token de autenticación
// si es que lo tenemos
const onRequest = (config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
};

HTTP.interceptors.response.use(onResponseSuccess, onResponseFailure);
HTTP.interceptors.request.use(onRequest);

export default HTTP;
