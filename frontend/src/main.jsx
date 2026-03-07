import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './colors.css'    // Import colors first
import './index.css'     // Then import Tailwind
import App from './App.jsx'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <App />
  </StrictMode>,
)