import { getToken, invalidateToken } from '../auth/token-storage'
import type { AuthResponse, User } from '../types/models'
import { ApiError } from './api-error'
import type { ApiService } from './api.service'

const URL = `${import.meta.env.VITE_API_URL}/graphql`

interface GraphQlError {
  message: string
  extensions?: { classification?: string }
}

// GraphQL ID stiže kao string; pretvaramo ga u broj da tipovi budu isti kao kod REST-a
interface GqlUser extends Omit<User, 'id'> {
  id: string
}

const toUser = (user: GqlUser): User => ({ ...user, id: Number(user.id) })

const USER_FIELDS = 'id name email createdAt'

async function request<T>(query: string, variables?: Record<string, unknown>): Promise<T> {
  const token = getToken()
  const headers: Record<string, string> = { 'Content-Type': 'application/json' }
  if (token) headers.Authorization = `Bearer ${token}`

  const response = await fetch(URL, {
    method: 'POST',
    headers,
    body: JSON.stringify({ query, variables }),
    cache: 'no-store',
  })

  // Neispravan ili istekao token odbija Spring Security pre GraphQL-a, pa stiže HTTP 401
  if (response.status === 401 && token) {
    invalidateToken()
    throw new ApiError('Your session has expired. Please log in again.')
  }

  const result: { data?: T; errors?: GraphQlError[] } | null = await response.json().catch(() => null)
  const error = result?.errors?.[0]

  if (error) {
    const unauthorized = error.extensions?.classification === 'UNAUTHORIZED'
    if (unauthorized && token) {
      invalidateToken()
      throw new ApiError('Your session has expired. Please log in again.')
    }
    throw new ApiError(error.message)
  }

  if (!response.ok || !result?.data) {
    throw new ApiError(`Request failed (${response.status})`)
  }
  return result.data
}

interface GqlAuthPayload {
  token: string
  user: GqlUser
}

const toAuthResponse = ({ token, user }: GqlAuthPayload): AuthResponse => ({ token, user: toUser(user) })

export const graphqlApi: ApiService = {
  login: async (email, password) => {
    const data = await request<{ login: GqlAuthPayload }>(
      `mutation Login($email: String!, $password: String!) {
        login(email: $email, password: $password) { token user { ${USER_FIELDS} } }
      }`,
      { email, password },
    )
    return toAuthResponse(data.login)
  },

  register: async (name, email, password) => {
    const data = await request<{ register: GqlAuthPayload }>(
      `mutation Register($input: RegisterInput!) {
        register(input: $input) { token user { ${USER_FIELDS} } }
      }`,
      { input: { name, email, password } },
    )
    return toAuthResponse(data.register)
  },

  getCurrentUser: async () => {
    const data = await request<{ me: GqlUser }>(`query Me { me { ${USER_FIELDS} } }`)
    return toUser(data.me)
  },
}
