export const BACKEND_URL = "http://localhost:8080/api";


// Roles del sistema
export const ROLES = {
  ADMIN: 'ADMIN',
  GESTOR:  'GESTOR',
  VISITANTE: 'VISITANTE'
};

// Estados de Exposición
export const ESTADOS_EXPOSICION = {
  BORRADOR: 'BORRADOR',
  ACTIVA: 'ACTIVA',
  ARCHIVADA: 'ARCHIVADA'
};

// Estados de Edición
export const ESTADOS_EDICION = {
  BORRADOR: 'BORRADOR',
  PUBLICADA: 'PUBLICADA',
  FINALIZADA: 'FINALIZADA',
  CANCELADA: 'CANCELADA'
};

// Estados de Sesión
export const ESTADOS_SESION = {
  DISPONIBLE: 'DISPONIBLE',
  COMPLETA: 'COMPLETA',
  CANCELADA: 'CANCELADA'
};

// Estados de Reserva
export const ESTADOS_RESERVA = {
  CONFIRMADA: 'CONFIRMADA',
  CANCELADA: 'CANCELADA',
  FINALIZADA: 'FINALIZADA'
};

// Tipos de Permiso
export const TIPOS_PERMISO = {
  CREADOR: 'CREADOR',
  EDITOR: 'EDITOR'
};

// Mensajes de error amigables
export const ERROR_MESSAGES = {
  GENERIC: 'Ha ocurrido un error.  Por favor, inténtalo de nuevo.',
  UNAUTHORIZED: 'No tienes permisos para realizar esta acción.',
  SESSION_EXPIRED: 'Tu sesión ha expirado. Por favor, inicia sesión nuevamente.',
  NOT_FOUND: 'El recurso solicitado no fue encontrado.',
  NETWORK_ERROR: 'Error de conexión.  Verifica tu conexión a internet.',
  VALIDATION_ERROR: 'Por favor, verifica los datos ingresados.'
};
