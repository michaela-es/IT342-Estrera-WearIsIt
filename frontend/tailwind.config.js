/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: 'var(--primary-color)',
        'bg-page': 'var(--bg-page)',
        'bg-container': 'var(--bg-container)',
      },
    },
  },
  plugins: [require('daisyui')],
}