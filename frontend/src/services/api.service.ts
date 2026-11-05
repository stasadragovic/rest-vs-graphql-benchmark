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
