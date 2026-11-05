import { useSyncExternalStore } from 'react'
import { getApiMode, subscribeToApiMode } from '../services/api.service'

export function useApiMode() {
  return useSyncExternalStore(subscribeToApiMode, getApiMode)
}
