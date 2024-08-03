import type { RouteLocationNormalized } from 'vue-router'
import { useAuthStore } from '../stores/authStore'
import router from '@/router'

export function authenticated(
  to: RouteLocationNormalized,
  from: RouteLocationNormalized,
  next: Function
) {
  const authStore = useAuthStore()
  if (authStore.keycloak.authenticated) {
    next()
  } else {
    authStore.login({})
  }
}

export function admin(to: RouteLocationNormalized, from: RouteLocationNormalized, next: Function) {
  const authStore = useAuthStore()
  if (authStore.keycloak.authenticated && authStore.user?.roles.includes('admin')) {
    next()
  } else {
    router.push('/error')
  }
}
