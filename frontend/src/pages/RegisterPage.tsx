import { useState, type FormEvent } from 'react'
import { Link, Navigate, useLocation } from 'react-router'
import { useAuth } from '../auth/useAuth'
import AuthCard from '../components/AuthCard'
import ErrorMessage from '../components/ErrorMessage'
import FormField from '../components/FormField'
import { errorMessage } from '../services/api-error'

export default function RegisterPage() {
  const { user, register } = useAuth()
  const location = useLocation()
  const from: string = location.state?.from ?? '/'

  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState(false)

  if (user) return <Navigate to={from} replace />

  async function handleSubmit(event: FormEvent) {
    event.preventDefault()
    setError(null)
    setSubmitting(true)
    try {
      await register(name, email, password)
    } catch (e) {
      setError(errorMessage(e))
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <AuthCard title="Register">
      <form onSubmit={handleSubmit} className="space-y-4">
        <FormField
          label="Name"
          autoComplete="name"
          required
          maxLength={100}
          value={name}
          onChange={(e) => setName(e.target.value)}
        />
        <FormField
          label="Email"
          type="email"
          autoComplete="email"
          required
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />
        <FormField
          label="Password"
          type="password"
          autoComplete="new-password"
          required
          minLength={8}
          maxLength={72}
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        <ErrorMessage message={error} />
        <button
          type="submit"
          disabled={submitting}
          className="w-full rounded-md bg-primary-dark px-4 py-2 font-semibold text-white hover:bg-primary disabled:opacity-60"
        >
          {submitting ? 'Creating account…' : 'Create account'}
        </button>
      </form>
      <p className="text-sm text-black/70">
        Already have an account?{' '}
        <Link to="/login" state={location.state} className="font-medium text-primary-dark hover:underline">
          Log in
        </Link>
      </p>
    </AuthCard>
  )
}
