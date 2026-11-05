import { Outlet } from 'react-router'
import ApiSwitch from './ApiSwitch'

export default function Layout() {
  return (
    <div className="min-h-screen bg-muted-light text-black">
      <header className="bg-charcoal">
        <div className="mx-auto flex max-w-6xl flex-wrap items-center gap-x-6 gap-y-3 px-4 py-3">
          <span className="text-lg font-semibold text-white">
            Task<span className="text-primary">Board</span>
          </span>
          <div className="ml-auto">
            <ApiSwitch />
          </div>
        </div>
      </header>
      <main className="mx-auto max-w-6xl px-4 py-6">
        <Outlet />
      </main>
    </div>
  )
}
