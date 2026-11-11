export default function ErrorMessage({ message }: { message: string | null }) {
  if (!message) return null
  return (
    <p role="alert" className="rounded-md bg-primary-light px-3 py-2 text-sm text-primary-dark">
      {message}
    </p>
  )
}
