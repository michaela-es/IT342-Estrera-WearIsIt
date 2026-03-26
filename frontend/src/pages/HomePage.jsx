// src/pages/HomePage.jsx
import React, { useState } from 'react';
import { Container, Grid, Box, Typography, AppBar, Toolbar } from '@mui/material';
import Navbar from '../components/layout/Navbar';
import SearchBar from '../components/common/SearchBar';
import ImageCard from '../components/image/ImageCard';

const mockImages = [
  { name: 'Sunset', url: 'https://source.unsplash.com/random/400x300?nature', tags: ['nature', 'sunset'] },
  { name: 'City', url: 'https://source.unsplash.com/random/400x300?city', tags: ['urban', 'night'] },
  { name: 'Mountain', url: 'https://source.unsplash.com/random/400x300?mountain', tags: ['nature', 'mountain'] },
];

const HomePage = () => {
  const [search, setSearch] = useState('');

  const filteredImages = mockImages.filter(
    (img) =>
      img.name.toLowerCase().includes(search.toLowerCase()) ||
      img.tags.some((tag) => tag.toLowerCase().includes(search.toLowerCase()))
  );

  return (
    <Box sx={{ minHeight: '100vh', backgroundColor: '#f9fafb' }}>
        <Toolbar sx={{ gap: 2 }}>
          <Navbar variant="dark" />
          
          <Box sx={{ flexGrow: 1 }}>
            <SearchBar 
              value={search} 
              onChange={(e) => setSearch(e.target.value)} 
              fullWidth
            />
          </Box>
          
        </Toolbar>
      <Container sx={{ py: 4 }}>
        <Grid container spacing={3}>
          {filteredImages.length ? (
            filteredImages.map((img, index) => (
             <Grid item xs={12} sm={6} md={4} lg={3} key={index}>
                <ImageCard image={img} />
              </Grid>
            ))
          ) : (
            <Grid item xs={12}>
              <Typography variant="body1" color="text.secondary">
                No images found.
              </Typography>
            </Grid>
          )}
        </Grid>
      </Container>
    </Box>
  );
};

export default HomePage;