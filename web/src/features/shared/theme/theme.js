import { createTheme } from '@mui/material/styles';

const theme = createTheme({
  palette: {
    primary: {
      main: '#486881',     
      light: '#6a9cb8',
      dark: '#2f4a5f',
    },
    secondary: {
      main: '#19135f',      
      light: '#3a2f8f',
      dark: '#0f0c3d',
    },
    background: {
      default: '#f3f4f6',
      paper: '#ffffff',
    },
    text: {
      primary: '#111827',
      secondary: '#4b5563',
    },
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          textTransform: 'none',
          fontWeight: 600,
          borderRadius: '8px',
        },
        containedPrimary: {
          background: 'linear-gradient(135deg, #486881, #19135f)',
          '&:hover': {
            background: 'linear-gradient(135deg, #19135f, #486881)',
          },
        },
      },
    },
    
    MuiChip: {
      styleOverrides: {
        root: {
          borderRadius: '20px',
          fontWeight: 500,
          fontSize: '0.7rem',
        },
        filled: {
          backgroundColor: '#689ded',
          color: '#ffffff',
          '&:hover': {
            backgroundColor: '#e4e6e9',
          },
        },
      },
    },
    
    MuiCard: {
      styleOverrides: {
        root: {
          borderRadius: '12px',
          transition: 'transform 0.3s ease-in-out, box-shadow 0.3s ease-in-out',
          '&:hover': {
            transform: 'translateY(-4px)',
            boxShadow: '0 20px 25px -12px rgba(0,0,0,0.2)',
          },
        },
      },
    },
    
    MuiAppBar: {
      styleOverrides: {
        root: {
          backgroundColor: '#ffffff',
          color: '#111827',
          boxShadow: '0 1px 3px rgba(0,0,0,0.1)',
        },
      },
    },
  },
});

export default theme;