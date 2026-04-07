import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ThemeProvider } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import { AuthProvider, useAuth } from './context/AuthContext';
import AuthPage from './pages/AuthPage';
import ProfilePage from './pages/ProfilePage';
import GalleryPage from './pages/GalleryPage';
import ItemDetails from './pages/ItemDetails';
import theme from './theme';
<<<<<<< HEAD
import OAuthCallback from './pages/OAuthCallback'; 

=======
import './colors.css';
>>>>>>> 23f831332e47161b355e918db849a2b2dd4ba5f3
import './App.css';
import { Home } from '@mui/icons-material';
const ProtectedRoute = ({ children }) => {
  const { user, initializing } = useAuth();
  
  if (initializing) {
    return <div>Loading...</div>;
  }
  
  if (!user) {
    return <Navigate to="/login" replace />;
  }
  
  return children;
};

function AppContent() {
  return (
    <Routes>
<<<<<<< HEAD
      <Route path="/" element={<AuthPage />} />
      <Route path="/home" element={<HomePage />} />
      <Route path="/login" element={<AuthPage />} />
       <Route path="/oauth-callback" element={<OAuthCallback />} />
      <Route path="/image/:id" element={<ItemDetails />} />
=======
      <Route 
        path="/" 
        element={
          <ProtectedRoute>
            <GalleryPage />
          </ProtectedRoute>
        } 
      />
      <Route path="/login" element={<AuthPage />} />
      
      <Route 
        path="/items/:id" 
        element={
          <ProtectedRoute>
            <ItemDetails />
          </ProtectedRoute>
        } 
      />
      
>>>>>>> 23f831332e47161b355e918db849a2b2dd4ba5f3
      <Route 
        path="/profile" 
        element={
          <ProtectedRoute>
            <ProfilePage />
          </ProtectedRoute>
        } 
      />
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}

function App() {
  return (
    <BrowserRouter>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <AuthProvider>
          <AppContent />
        </AuthProvider>
      </ThemeProvider>
    </BrowserRouter>
  );
}

export default App;