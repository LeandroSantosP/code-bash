import { twMerge } from 'tailwind-merge'
import { tv } from 'tailwind-variants'

export function cn(...classes: Array<string | false | null | undefined>) {
  return twMerge(classes.filter(Boolean).join(' '))
}

export const button_variants = tv({
  base: 'inline-flex items-center justify-center rounded-md px-4 py-2 text-sm font-semibold transition hover:opacity-90 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-slate-500/40',
  variants: {
    variant: {
      primary: 'bg-slate-900 text-slate-50',
      secondary: 'bg-amber-100 text-amber-900',
    },
  },
  defaultVariants: {
    variant: 'primary',
  },
})
