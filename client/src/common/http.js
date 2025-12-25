import axios from "axios";
import { BACKEND_URL, ERROR_MESSAGES } from "../constants. js";
import auth from "./auth.js";
import { setNotification } from "./store. js";

const HTTP = axios.create({
  baseURL: BACKEND_URL
});

/**
 * Handler cuando el usuario no tiene autorización
 */
const onUnauthorized = () => {
  console.error("Access denied - Logging out");
  auth.logout();
};

const onResponseSuccess = (response) => response;

/**
 * Handler global de errores HTTP
 * ✅ CORRECCIÓN: No ejecutar logout si estamos en rutas de autenticación
 */
const onResponseFailure = (err) => {
  // Si no hay respuesta del servidor (error de red)
  if (!err. response) {
    console.error("Network error:", err);
    setNotification(ERROR_MESSAGES.NETWORK_ERROR, "error");
    return Promise.reject(err);
  }

  const responseStatus = err.response.status;
  const responseUrl = err.config?. url ?? "";

  // No hacer logout si es una petición de autenticación
  const isAuthRequest = 
    responseUrl.includes("auth/login") || 
    responseUrl.includes("auth/register") ||
    responseUrl.includes("auth/me");

  // Manejo según código de estado
  switch (responseStatus) {
    case 401:
      if (! isAuthRequest) {
        // Solo si NO es una petición de auth
        setNotification(ERROR_MESSAGES. SESSION_EXPIRED, "error");
        onUnauthorized();
      }
      break;

    case 403:
      if (!isAuthRequest) {
        setNotification(ERROR_MESSAGES.UNAUTHORIZED, "error");
      }
      break;

    case 404:
      setNotification(ERROR_MESSAGES.NOT_FOUND, "error");
      break;

    case 400:  {
      const errorMsg = err.response.data?.message || ERROR_MESSAGES.VALIDATION_ERROR;
      setNotification(errorMsg, "error");
      break;
    }

    case 500:
    case 502:
    case 503:
      console.error("Server error:", err. response.data);
      setNotification(ERROR_MESSAGES. GENERIC, "error");
      break;

    default:
      if (! isAuthRequest) {
        setNotification(ERROR_MESSAGES. GENERIC, "error");
      }
  }

  return Promise.reject(err);
};

/**
 * Interceptor de peticiones - Añade token de autorización
 */
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