import { getToken, invalidateToken } from '../auth/token-storage'
import type { AuthResponse, User } from '../types/models'
import { ApiError } from './api-error'
import type { ApiService } from './api.service'

const BASE_URL = `${import.meta.env.VITE_API_URL}/api`

async function request<T>(method: string, path: string, body?: unknown): Promise<T> {
  const token = getToken()
  const headers: Record<string, string> = {}
  if (body !== undefined) headers['Content-Type'] = 'application/json'
  if (token) headers.Authorization = `Bearer ${token}`

  const response = await fetch(`${BASE_URL}${path}`, {
    method,
    headers,
    body: body === undefined ? undefined : JSON.stringify(body),
    cache: 'no-store',
  })

  if (response.ok) {
    return (response.status === 204 ? undefined : await response.json()) as T
  }

  // Bez tokena 401 znači pogrešan email/lozinku, a ne istek prijave
  if (response.status === 401 && token) {
    invalidateToken()
    throw new ApiError('Your session has expired. Please log in again.')
  }

  const problem = await response.json().catch(() => null)
  throw new ApiError(problem?.detail ?? `Request failed (${response.status})`)
}

export const restApi: ApiService = {
  login: (email, password) => request<AuthResponse>('POST', '/auth/login', { email, password }),

  register: (name, email, password) =>
    request<AuthResponse>('POST', '/auth/register', { name, email, password }),

  getCurrentUser: () => request<User>('GET', '/users/me'),
}
