const TOKEN_KEY = 'token'

let onInvalidToken: (() => void) | null = null

export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token: string): void {
  localStorage.setItem(TOKEN_KEY, token)
}

export function clearToken(): void {
  localStorage.removeItem(TOKEN_KEY)
}

export function setInvalidTokenHandler(handler: (() => void) | null): void {
  onInvalidToken = handler
}

// Servisi ovo zovu kad backend odbije poslat token (istekao ili neispravan)
export function invalidateToken(): void {
  clearToken()
  onInvalidToken?.()
}
