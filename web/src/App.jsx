import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ThemeProvider } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import { AuthProvider, useAuth } from '@features/auth/context/AuthContext';
import AuthPage from '@features/auth/pages/AuthPage';
import ProfilePage from '@features/profile/pages/ProfilePage';
import GalleryPage from '@features/gallery/pages/GalleryPage';
import ItemDetails from '@features/item-details/pages/ItemDetails';
import UploadClothing from '@features/upload/pages/UploadClothing'; 
import theme from "@features/shared/theme/theme";
import OAuthCallback from '@features/auth/pages/OAuthCallback';
import './colors.css';
import './App.css';

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
      <Route 
        path="/" 
        element={
          <ProtectedRoute>
            <GalleryPage />
          </ProtectedRoute>
        } 
      />
      <Route path="/login" element={<AuthPage />} />
      <Route path="/oauth-callback" element={<OAuthCallback />} />
      
      <Route 
        path="/image/:id" 
        element={
          <ProtectedRoute>
            <ItemDetails />
          </ProtectedRoute>
        } 
      />
      
      <Route 
        path="/items/:id" 
        element={
          <ProtectedRoute>
            <ItemDetails />
          </ProtectedRoute>
        } 
      />
      
      <Route 
        path="/profile" 
        element={
          <ProtectedRoute>
            <ProfilePage />
          </ProtectedRoute>
        } 
      />
       <Route 
        path="/upload" 
        element={
          <ProtectedRoute>
            <UploadClothing />
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