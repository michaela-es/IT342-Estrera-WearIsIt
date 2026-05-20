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
import { CategoryProvider, CategoryTagManager } from '@features/categories-tags';
import OAuthCallback from '@features/auth/pages/OAuthCallback';
import Layout from '@features/shared/components/Layout'; 
import theme from '@features/shared/theme/theme';
import EditClothingItem from '@features/clothing-item/pages/EditClothingItem';

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

function ProtectedLayout({ children }) {
  return (
    <ProtectedRoute>
      <Layout variant="light">
        {children}
      </Layout>
    </ProtectedRoute>
  );
}

function AppContent() {
  return (
    <Routes>
      <Route path="/" element={<ProtectedLayout><GalleryPage /></ProtectedLayout>} />
      <Route path="/image/:id" element={<ProtectedLayout><ItemDetails /></ProtectedLayout>} />
      <Route path="/items/:id" element={<ProtectedLayout><ItemDetails /></ProtectedLayout>} />
      <Route path="/profile" element={<ProtectedLayout><ProfilePage /></ProtectedLayout>} />
      <Route path="/upload" element={<ProtectedLayout><UploadClothing /></ProtectedLayout>} />
<Route path="/items/:id/edit" element={<ProtectedLayout><EditClothingItem /></ProtectedLayout>} />
      <Route 
        path="/categories-tags" 
        element={
          <ProtectedLayout>
            <CategoryProvider>
              <CategoryTagManager />
            </CategoryProvider>
          </ProtectedLayout>
        } 
      />
      
      <Route path="/login" element={<AuthPage />} />
      <Route path="/oauth-callback" element={<OAuthCallback />} />
      
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