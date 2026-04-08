export type ApiErrorResponse = {
  status: string
  message: string
  timestamp: string
}

export type MeResponse = {
  authenticated: boolean
  name: string | null
  authorities: Array<{ authority: string }> | null
  attributes?: Record<string, unknown>
}

export async function registerUser(payload: { username: string; password: string }) {
  const response = await fetch('/public/register', {
    method: 'POST',
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  })

  if (!response.ok) {
    const body = (await response.json().catch(() => null)) as ApiErrorResponse | null
    throw new Error(body?.message || 'Failed to create account')
  }
}

export async function loginWithPassword(payload: { username: string; password: string }) {
  const body = new URLSearchParams()
  body.set('username', payload.username)
  body.set('password', payload.password)

  const response = await fetch('/auth/login', {
    method: 'POST',
    credentials: 'include',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: body.toString(),
  })

  if (!response.ok) {
    const error = (await response.json().catch(() => null)) as ApiErrorResponse | null
    throw new Error(error?.message || 'Invalid username or password')
  }
}

export async function getMe() {
  const response = await fetch('/me', {
    method: 'GET',
    credentials: 'include',
    headers: {
      Accept: 'application/json',
    },
  })

  if (!response.ok) {
    return {
      authenticated: false,
      name: null,
      authorities: null,
    } satisfies MeResponse
  }

  return (await response.json()) as MeResponse
}

export async function logout() {
  const response = await fetch('/logout', {
    method: 'POST',
    credentials: 'include',
  })

  if (!response.ok) {
    throw new Error('Failed to log out')
  }
}
