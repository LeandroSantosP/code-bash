import { type FormEvent, useEffect, useMemo, useState } from 'react'

import { getMe, loginWithPassword, logout, registerUser, type MeResponse } from './lib/api'
import { button_variants, cn } from './lib/utils'

const anonymousUser: MeResponse = {
  authenticated: false,
  name: null,
  authorities: null,
}

function App() {
  const continueTarget = useMemo(() => {
    const value = new URLSearchParams(window.location.search).get('continue')

    if (!value) {
      return null
    }

    return value.startsWith('/') ? value : null
  }, [])

  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [isRegisterSubmitting, setIsRegisterSubmitting] = useState(false)
  const [isLoginSubmitting, setIsLoginSubmitting] = useState(false)
  const [isLoadingUser, setIsLoadingUser] = useState(true)
  const [isLoggingOut, setIsLoggingOut] = useState(false)
  const [errorMessage, setErrorMessage] = useState('')
  const [successMessage, setSuccessMessage] = useState('')
  const [user, setUser] = useState<MeResponse>(anonymousUser)

  useEffect(() => {
    let isMounted = true

    async function fetchCurrentUser() {
      setIsLoadingUser(true)
      try {
        const me = await getMe()
        if (isMounted) {
          setUser(me)
        }
      } finally {
        if (isMounted) {
          setIsLoadingUser(false)
        }
      }
    }

    fetchCurrentUser()

    return () => {
      isMounted = false
    }
  }, [])

  useEffect(() => {
    if (user.authenticated && continueTarget) {
      window.location.href = continueTarget
    }
  }, [continueTarget, user.authenticated])

  const authoritiesText = useMemo(() => {
    if (!user.authorities || user.authorities.length === 0) {
      return 'No roles'
    }

    return user.authorities.map((item) => item.authority).join(', ')
  }, [user.authorities])

  const githubLogin =
    typeof user.attributes?.['login'] === 'string' ? String(user.attributes['login']) : null
  const githubEmail =
    typeof user.attributes?.['email'] === 'string' ? String(user.attributes['email']) : null

  async function handleCreateAccount(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setErrorMessage('')
    setSuccessMessage('')
    setIsRegisterSubmitting(true)

    try {
      await registerUser({ username, password })
      setSuccessMessage('Account created! You can now log in with username/password.')
      setPassword('')
    } catch (error) {
      const message = error instanceof Error ? error.message : 'Something went wrong'
      setErrorMessage(message)
    } finally {
      setIsRegisterSubmitting(false)
    }
  }

  async function handlePasswordLogin(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setErrorMessage('')
    setSuccessMessage('')
    setIsLoginSubmitting(true)

    try {
      await loginWithPassword({ username, password })

      if (continueTarget) {
        window.location.href = continueTarget
        return
      }

      const me = await getMe()
      setUser(me)
    } catch (error) {
      const message = error instanceof Error ? error.message : 'Something went wrong'
      setErrorMessage(message)
    } finally {
      setIsLoginSubmitting(false)
    }
  }

  async function handleLogout() {
    setIsLoggingOut(true)
    setErrorMessage('')

    try {
      await logout()
      setUser(anonymousUser)
      setSuccessMessage('Logged out successfully.')
    } catch (error) {
      const message = error instanceof Error ? error.message : 'Something went wrong'
      setErrorMessage(message)
    } finally {
      setIsLoggingOut(false)
    }
  }

  if (isLoadingUser) {
    return (
      <main className="mx-auto flex min-h-screen w-full max-w-5xl items-center px-4 py-12">
        <div className="w-full rounded-2xl border border-slate-200 bg-white/80 p-8 shadow-lg shadow-slate-300/30 backdrop-blur-sm">
          <p className="text-slate-700">Loading your session...</p>
        </div>
      </main>
    )
  }

  if (user.authenticated) {
    return (
      <main className="mx-auto flex min-h-screen w-full max-w-5xl items-center px-4 py-12">
        <div className="w-full rounded-2xl border border-slate-200 bg-white/80 p-8 shadow-lg shadow-slate-300/30 backdrop-blur-sm">
          <header className="mb-8 flex items-center justify-between gap-3">
            <div>
              <span className="inline-flex rounded-full bg-amber-100 px-3 py-1 text-xs font-semibold tracking-wide text-amber-900 uppercase">
                Home
              </span>
              <h1 className="mt-3 text-3xl font-black tracking-tight text-slate-900">Welcome, {user.name}</h1>
            </div>
            <button
              className={cn(button_variants({ variant: 'secondary' }), 'cursor-pointer')}
              disabled={isLoggingOut}
              onClick={handleLogout}
              type="button"
            >
              {isLoggingOut ? 'Logging out...' : 'Logout'}
            </button>
          </header>

          <section className="grid gap-4 md:grid-cols-2">
            <article className="rounded-xl border border-slate-200 bg-white p-5">
              <h2 className="text-sm font-semibold tracking-wide text-slate-500 uppercase">Account</h2>
              <p className="mt-3 text-sm text-slate-700">
                <strong className="font-semibold text-slate-900">Username:</strong> {user.name}
              </p>
              <p className="mt-2 text-sm text-slate-700">
                <strong className="font-semibold text-slate-900">Authorities:</strong> {authoritiesText}
              </p>
            </article>

            <article className="rounded-xl border border-slate-200 bg-white p-5">
              <h2 className="text-sm font-semibold tracking-wide text-slate-500 uppercase">GitHub profile</h2>
              <p className="mt-3 text-sm text-slate-700">
                <strong className="font-semibold text-slate-900">Login:</strong> {githubLogin || 'Not linked'}
              </p>
              <p className="mt-2 text-sm text-slate-700">
                <strong className="font-semibold text-slate-900">Email:</strong> {githubEmail || 'Unavailable'}
              </p>
            </article>
          </section>

          {errorMessage ? (
            <p className="mt-4 rounded-lg border border-rose-200 bg-rose-50 px-3 py-2 text-sm text-rose-800">
              {errorMessage}
            </p>
          ) : null}

          {successMessage ? (
            <p className="mt-4 rounded-lg border border-emerald-200 bg-emerald-50 px-3 py-2 text-sm text-emerald-800">
              {successMessage}
            </p>
          ) : null}
        </div>
      </main>
    )
  }

  return (
    <main className="mx-auto flex min-h-screen w-full max-w-5xl items-center px-4 py-12">
      <div className="grid w-full gap-6 md:grid-cols-[1.1fr_1fr]">
        <section className="rounded-2xl border border-slate-200 bg-white/80 p-8 shadow-lg shadow-slate-300/30 backdrop-blur-sm">
          <span className="mb-3 inline-flex rounded-full bg-amber-100 px-3 py-1 text-xs font-semibold tracking-wide text-amber-900 uppercase">
            DevRoast Login
          </span>
          <h1 className="text-3xl font-black tracking-tight text-slate-900">Sign in to keep roasting code</h1>
          <p className="mt-3 text-slate-600">
            Use your local account or continue with GitHub OAuth2.
          </p>

          <form className="mt-6 space-y-4" onSubmit={handlePasswordLogin}>
            <div className="space-y-1">
              <label className="text-sm font-semibold text-slate-700" htmlFor="username">
                Username
              </label>
              <input
                id="username"
                className="w-full rounded-lg border border-slate-300 bg-white px-3 py-2 text-slate-900 outline-none transition focus:border-slate-400 focus:ring-2 focus:ring-slate-300/50"
                minLength={3}
                maxLength={30}
                onChange={(event) => setUsername(event.target.value)}
                required
                value={username}
              />
            </div>

            <div className="space-y-1">
              <label className="text-sm font-semibold text-slate-700" htmlFor="password">
                Password
              </label>
              <input
                id="password"
                type="password"
                className="w-full rounded-lg border border-slate-300 bg-white px-3 py-2 text-slate-900 outline-none transition focus:border-slate-400 focus:ring-2 focus:ring-slate-300/50"
                minLength={8}
                maxLength={100}
                onChange={(event) => setPassword(event.target.value)}
                required
                value={password}
              />
            </div>

            <button
              className={cn(button_variants({ variant: 'primary' }), 'w-full cursor-pointer')}
              disabled={isLoginSubmitting}
              type="submit"
            >
              {isLoginSubmitting ? 'Signing in...' : 'Sign in with username/password'}
            </button>
          </form>

          <form className="mt-3" onSubmit={handleCreateAccount}>
            <button
              className={cn(button_variants({ variant: 'secondary' }), 'w-full cursor-pointer')}
              disabled={isRegisterSubmitting}
              type="submit"
            >
              {isRegisterSubmitting ? 'Creating account...' : 'Create new account'}
            </button>
          </form>

          <div className="my-5 flex items-center gap-3">
            <div className="h-px flex-1 bg-slate-300" />
            <span className="text-xs font-semibold tracking-wide text-slate-500 uppercase">or</span>
            <div className="h-px flex-1 bg-slate-300" />
          </div>

          <a className="block" href="/oauth2/authorization/github">
            <span className={cn(button_variants({ variant: 'secondary' }), 'w-full')}>Continue with GitHub</span>
          </a>

          {errorMessage ? (
            <p className="mt-4 rounded-lg border border-rose-200 bg-rose-50 px-3 py-2 text-sm text-rose-800">
              {errorMessage}
            </p>
          ) : null}

          {successMessage ? (
            <p className="mt-4 rounded-lg border border-emerald-200 bg-emerald-50 px-3 py-2 text-sm text-emerald-800">
              {successMessage}
            </p>
          ) : null}
        </section>

        <aside className="rounded-2xl border border-slate-200 bg-slate-900 p-8 text-slate-100 shadow-lg shadow-slate-300/30">
          <h2 className="text-xl font-bold">How sign-in works</h2>
          <ul className="mt-4 space-y-3 text-sm text-slate-300">
            <li>1. Sign in with username/password in this page.</li>
            <li>2. Create a new local account with one click.</li>
            <li>3. Or click GitHub button to use OAuth2 directly.</li>
          </ul>
        </aside>
      </div>
    </main>
  )
}

export default App
