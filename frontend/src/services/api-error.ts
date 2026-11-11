export class ApiError extends Error {
  constructor(message: string) {
    super(message)
    this.name = 'ApiError'
  }
}

export function errorMessage(error: unknown): string {
  // fetch baca TypeError kad backend nije dostupan
  return error instanceof ApiError ? error.message : 'Cannot reach the server.'
}
