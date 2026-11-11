import type { InputHTMLAttributes } from 'react'

interface FormFieldProps extends InputHTMLAttributes<HTMLInputElement> {
  label: string
}

export default function FormField({ label, ...inputProps }: FormFieldProps) {
  return (
    <label className="block space-y-1 text-sm">
      <span className="font-medium">{label}</span>
      <input
        {...inputProps}
        className="w-full rounded-md border border-muted px-3 py-2 outline-none focus:border-primary focus:ring-2 focus:ring-primary/30"
      />
    </label>
  )
}
