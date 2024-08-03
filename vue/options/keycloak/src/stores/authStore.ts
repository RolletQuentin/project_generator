import { defineStore } from 'pinia'
import keycloak from '../config/keycloak'
import type { User } from '../types/User'
import router from '@/router'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    keycloak: keycloak,
    user: null as User | null
  }),
  actions: {
    async init() {
      await keycloak.init({ onLoad: 'check-sso' })
      if (keycloak.authenticated) {
        await fetch(`${import.meta.env.VITE_API_URL}/users/isRegistered`, {
          headers: {
            Authorization: `Bearer ${keycloak.token}`
          }
        })
          .then((response) => response.json())
          .then((user) => {
            this.user = user
          })
          .catch((error) => {
            console.error('Failed to fetch user:', error)
          })
      }
    },
    async login(options: Keycloak.KeycloakLoginOptions) {
      keycloak.login(options).catch((error) => {
        console.error('Authentication failed:', error)
      })
    },

    logout(options: Keycloak.KeycloakLogoutOptions) {
      keycloak.logout(options)
      router.push('/')
    }
  }
})
