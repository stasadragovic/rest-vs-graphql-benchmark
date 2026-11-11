import { useEffect, useState, type ReactNode } from 'react'
import { api } from '../services/api.service'
import type { AuthResponse, User } from '../types/models'
import { AuthContext } from './auth-context'
import { clearToken, getToken, setInvalidTokenHandler, setToken } from './token-storage'

export default function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null)
  const [loading, setLoading] = useState(() => getToken() !== null)

  useEffect(() => {
    setInvalidTokenHandler(() => setUser(null))
    return () => setInvalidTokenHandler(null)
  }, [])

  useEffect(() => {
    if (!getToken()) return
    api
      .getCurrentUser()
      .then(setUser)
      .catch(() => setUser(null))
      .finally(() => setLoading(false))
  }, [])

  function signIn({ token, user }: AuthResponse) {
    setToken(token)
    setUser(user)
  }

  const value = {
    user,
    loading,
    login: async (email: string, password: string) => signIn(await api.login(email, password)),
    register: async (name: string, email: string, password: string) =>
      signIn(await api.register(name, email, password)),
    logout: () => {
      clearToken()
      setUser(null)
    },
  }

  return <AuthContext value={value}>{children}</AuthContext>
}
