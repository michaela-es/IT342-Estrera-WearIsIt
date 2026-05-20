import { createTheme } from '@mui/material/styles';

const theme = createTheme({
  palette: {
    primary: {
      main: '#2c5f8a',     
      light: '#5a8bb5',
      dark: '#1a3d5c',
      contrastText: '#ffffff',
    },
    secondary: {
      main: '#1a1a4f',      
      light: '#3d3d7a',
      dark: '#0f0f35',
      contrastText: '#ffffff',
    },
    background: {
      default: '#f8f9fc', 
      paper: '#ffffff',
      subtle: '#f0f2f6',
    },
    text: {
      primary: '#1a2a3a',
      secondary: '#5c6f87',
      disabled: '#a0abb9',
    },
    action: {
      hover: 'rgba(44, 95, 138, 0.04)',
      selected: 'rgba(44, 95, 138, 0.08)',
    },
  },
  shape: {
    borderRadius: 12,
  },
  typography: {
    fontFamily: '"Inter", "Segoe UI", "Roboto", "Helvetica Neue", sans-serif',
    h1: { fontWeight: 700, letterSpacing: '-0.02em' },
    h2: { fontWeight: 700, letterSpacing: '-0.01em' },
    h3: { fontWeight: 600, letterSpacing: '-0.01em' },
    h4: { fontWeight: 600 },
    h5: { fontWeight: 600 },
    h6: { fontWeight: 600 },
    button: { fontWeight: 600, textTransform: 'none' },
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          textTransform: 'none',
          fontWeight: 600,
          borderRadius: 10,
          padding: '8px 20px',
          transition: 'all 0.2s ease-in-out',
        },
        containedPrimary: {
          background: 'linear-gradient(135deg, #2c5f8a, #1a1a4f)',
          boxShadow: '0 4px 12px rgba(26, 26, 79, 0.15)',
          '&:hover': {
            background: 'linear-gradient(135deg, #1a3d5c, #0f0f35)',
            transform: 'translateY(-2px)',
            boxShadow: '0 6px 16px rgba(26, 26, 79, 0.25)',
          },
          '&:active': {
            transform: 'translateY(0)',
          },
        },
        containedSecondary: {
          background: 'linear-gradient(135deg, #1a1a4f, #2c5f8a)',
          '&:hover': {
            background: 'linear-gradient(135deg, #0f0f35, #1a3d5c)',
          },
        },
        outlined: {
          borderColor: '#2c5f8a',
          color: '#2c5f8a',
          '&:hover': {
            borderColor: '#1a1a4f',
            backgroundColor: 'rgba(44, 95, 138, 0.04)',
          },
        },
      },
    },
    
    MuiChip: {
      styleOverrides: {
        root: {
          borderRadius: 20,
          fontWeight: 500,
          fontSize: '0.75rem',
        },
        filled: {
          backgroundColor: '#2c5f8a',
          color: '#ffffff',
          '&:hover': {
            backgroundColor: '#1a3d5c',
          },
        },
        outlined: {
          borderColor: '#2c5f8a',
          color: '#2c5f8a',
        },
        deleteIcon: {
          color: '#ffffff',
          '&:hover': {
            color: '#e0e0e0',
          },
        },
      },
    },
    
    MuiCard: {
      styleOverrides: {
        root: {
          borderRadius: 16,
          transition: 'transform 0.25s ease-in-out, box-shadow 0.25s ease-in-out',
          '&:hover': {
            transform: 'translateY(-4px)',
            boxShadow: '0 20px 30px -12px rgba(26, 26, 79, 0.15)',
          },
        },
      },
    },
    
    MuiAppBar: {
      styleOverrides: {
        root: {
          backgroundColor: '#ffffff',
          color: '#1a2a3a',
          boxShadow: '0 1px 3px rgba(0,0,0,0.05)',
          backgroundImage: 'none',
        },
      },
    },
    
    MuiPaper: {
      styleOverrides: {
        root: {
          borderRadius: 16,
        },
        elevation1: {
          boxShadow: '0 1px 3px rgba(0,0,0,0.05), 0 1px 2px rgba(0,0,0,0.03)',
        },
        elevation2: {
          boxShadow: '0 4px 6px -1px rgba(0,0,0,0.05), 0 2px 4px -1px rgba(0,0,0,0.03)',
        },
      },
    },
    
    MuiDialog: {
      styleOverrides: {
        paper: {
          borderRadius: 20,
        },
      },
    },
    
    MuiTextField: {
      styleOverrides: {
        root: {
          '& .MuiOutlinedInput-root': {
            borderRadius: 10,
            '&:hover .MuiOutlinedInput-notchedOutline': {
              borderColor: '#2c5f8a',
            },
            '&.Mui-focused .MuiOutlinedInput-notchedOutline': {
              borderColor: '#1a1a4f',
              borderWidth: 2,
            },
          },
        },
      },
    },
    
    MuiInputLabel: {
      styleOverrides: {
        root: {
          '&.Mui-focused': {
            color: '#1a1a4f',
          },
        },
      },
    },
  },
});

export default theme;