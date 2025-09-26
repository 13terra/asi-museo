<template>
  <div class="container py-5 text-center">
    <h1 class="mb-4">¡Bienvenido!</h1>
    <p class="lead mb-5">
      Gestiona tus notas fácilmente. Autentícate o crea una cuenta para comenzar.
    </p>
    <div class="d-grid gap-3 col-12 col-sm-6 mx-auto">
      <router-link to="/login" class="btn btn-primary btn-lg"> Autenticarse </router-link>
      <router-link to="/register" class="btn btn-outline-primary btn-lg"> Registrarse </router-link>
    </div>
  </div>
</template>
