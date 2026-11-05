import { createBrowserRouter, RouterProvider } from 'react-router'
import Layout from './components/Layout'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'

const router = createBrowserRouter([
  { path: '/login', element: <LoginPage /> },
  { path: '/register', element: <RegisterPage /> },
  { path: '/', element: <Layout /> },
])

export default function App() {
  return <RouterProvider router={router} />
}
