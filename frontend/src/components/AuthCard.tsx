import type { ReactNode } from 'react'
import ApiSwitch from './ApiSwitch'

interface AuthCardProps {
  title: string
  children: ReactNode
}

export default function AuthCard({ title, children }: AuthCardProps) {
  return (
    <div className="relative flex min-h-screen items-center justify-center overflow-hidden bg-charcoal px-4 py-10">
      <div aria-hidden className="absolute -top-40 -left-40 h-[28rem] w-[28rem] rounded-full bg-primary/35 blur-3xl" />
      <div aria-hidden className="absolute -right-40 -bottom-40 h-[28rem] w-[28rem] rounded-full bg-sand/20 blur-3xl" />

      <div className="relative w-full max-w-sm space-y-6">
        <div className="space-y-3 text-center">
          <p className="text-3xl font-bold text-white">
            Task<span className="text-primary">Board</span>
          </p>
          <div className="flex justify-center">
            <ApiSwitch />
          </div>
        </div>

        <div className="space-y-5 rounded-2xl bg-white px-6 py-7 shadow-2xl">
          <h1 className="text-xl font-semibold">{title}</h1>
          {children}
        </div>
      </div>
    </div>
  )
}
