import { Outlet } from 'react-router'
import { useAuth } from '../auth/useAuth'
import ApiSwitch from './ApiSwitch'

export default function Layout() {
  const { user, logout } = useAuth()

  return (
    <div className="min-h-screen bg-muted-light text-black">
      <header className="bg-charcoal">
        <div className="mx-auto flex max-w-6xl flex-wrap items-center gap-x-6 gap-y-3 px-4 py-3">
          <span className="text-lg font-semibold text-white">
            Task<span className="text-primary">Board</span>
          </span>
          <div className="ml-auto flex items-center gap-4">
            <ApiSwitch />
            <span className="text-sm text-white">{user?.name}</span>
            <button
              type="button"
              onClick={logout}
              className="rounded-md px-3 py-1 text-sm font-semibold text-muted hover:text-white"
            >
              Log out
            </button>
          </div>
        </div>
      </header>
      <main className="mx-auto max-w-6xl px-4 py-6">
        <Outlet />
      </main>
    </div>
  )
}
