import { useApiMode } from '../hooks/useApiMode'
import { setApiMode, type ApiMode } from '../services/api.service'

const OPTIONS: { mode: ApiMode; label: string }[] = [
  { mode: 'rest', label: 'REST' },
  { mode: 'graphql', label: 'GraphQL' },
]

export default function ApiSwitch() {
  const current = useApiMode()

  return (
    <div className="flex rounded-lg bg-white/10 p-1 text-sm font-semibold">
      {OPTIONS.map(({ mode, label }) => (
        <button
          key={mode}
          type="button"
          aria-pressed={current === mode}
          onClick={() => setApiMode(mode)}
          className={
            current === mode
              ? 'rounded-md bg-primary px-3 py-1 text-white'
              : 'rounded-md px-3 py-1 text-muted hover:text-white'
          }
        >
          {label}
        </button>
      ))}
    </div>
  )
}
