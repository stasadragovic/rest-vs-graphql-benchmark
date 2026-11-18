import type { AuthResponse, User } from '../types/models'
import { graphqlApi } from './graphql-api.service'
import { restApi } from './rest-api.service'

export interface ApiService {
  login(email: string, password: string): Promise<AuthResponse>
  register(name: string, email: string, password: string): Promise<AuthResponse>
  getCurrentUser(): Promise<User>
}

export type ApiMode = 'rest' | 'graphql'

const STORAGE_KEY = 'apiMode'

function isApiMode(value: unknown): value is ApiMode {
  return value === 'rest' || value === 'graphql'
}

function initialMode(): ApiMode {
  const stored = localStorage.getItem(STORAGE_KEY)
  if (isApiMode(stored)) return stored
  const fromEnv = import.meta.env.VITE_API_MODE
  return isApiMode(fromEnv) ? fromEnv : 'rest'
}

let currentMode = initialMode()
const listeners = new Set<() => void>()

export function getApiMode(): ApiMode {
  return currentMode
}

export function setApiMode(mode: ApiMode): void {
  currentMode = mode
  localStorage.setItem(STORAGE_KEY, mode)
  listeners.forEach((listener) => listener())
}

export function subscribeToApiMode(listener: () => void): () => void {
  listeners.add(listener)
  return () => listeners.delete(listener)
}

function current(): ApiService {
  return currentMode === 'graphql' ? graphqlApi : restApi
}

// Implementacija se bira pri svakom pozivu, pa prekidač deluje odmah
export const api: ApiService = {
  login: (email, password) => current().login(email, password),
  register: (name, email, password) => current().register(name, email, password),
  getCurrentUser: () => current().getCurrentUser(),
}
