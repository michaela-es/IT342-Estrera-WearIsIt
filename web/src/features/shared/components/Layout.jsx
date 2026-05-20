import React from 'react';
import { Box, Container } from '@mui/material';
import Navbar from './NavBar';

const Layout = ({ children, variant = 'light' }) => {
  return (
    <Box sx={{ minHeight: '100vh', backgroundColor: '#e3e3e3' }}>
      <Navbar variant={variant} />
      <Container maxWidth="xl" sx={{ py: 4 }}>
        {children}
      </Container>
    </Box>
  );
};

export default Layout;