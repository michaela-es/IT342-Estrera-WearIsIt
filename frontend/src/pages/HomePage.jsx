import React, { useState, useEffect } from 'react';
import { Container, Grid, Box, Typography } from '@mui/material';
import Navbar from '../components/layout/Navbar';
import SearchBar from '../components/common/SearchBar';
import ImageCard from '../components/image/ImageCard';
import { getImages } from '../api/mockApi'; 
import { useImages } from '../hooks/useImages';
import useSearch from '../hooks/useSearch';

const HomePage = () => {
  const [search, setSearch] = useState('');
  const { images, loading, error } = useImages();

  const filteredImages = useSearch(images, search, ['name', 'tags']);


  return (
    <Box sx={{ minHeight: '100vh', backgroundColor: '#e3e3e3' }}>
      <Box
        sx={{
          display: 'flex',
          alignItems: 'center',
          px: 2,
          py: 1.5,
          backgroundColor: '#ffffff',
          gap: 2,
        }}
      >
        <Navbar variant="light" />
        <Box sx={{ flexGrow: 1 }}>
          <SearchBar
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            fullWidth
          />
        </Box>
      </Box>

      <Container maxWidth="xl" sx={{ py: 4 }}>
        {filteredImages.length ? (
          <Box sx={{ 
            display: 'grid', 
            gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))',
            gap: 3,
            justifyContent: 'center'
          }}>
            {filteredImages.map((img, index) => (
              <Box key={index} sx={{ maxWidth: 380, mx: 'auto', width: '100%' }}>
                <ImageCard image={img} />
              </Box>
            ))}
          </Box>
        ) : (
          <Typography variant="body1" color="text.secondary" textAlign="center">
            No items found.
          </Typography>
        )}
      </Container>
    </Box>
  );
};

export default HomePage;