import React, { useState, useEffect } from 'react';
import { Container, Grid, Box, Typography } from '@mui/material';
import Navbar from '../components/layout/Navbar';
import SearchBar from '../components/common/SearchBar';
import ImageCard from '../components/image/ImageCard';
import { getImages } from '../api/mockApi'; 

const HomePage = () => {
  const [search, setSearch] = useState('');
  const [images, setImages] = useState([]);

  useEffect(() => {
    const fetchImages = async () => {
      const data = await getImages();
      setImages(data);
    };
    fetchImages();
  }, []);

  const filteredImages = images.filter(
    (img) =>
      img.name.toLowerCase().includes(search.toLowerCase()) ||
      img.tags.some((tag) => tag.toLowerCase().includes(search.toLowerCase()))
  );

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

      <Container sx={{ py: 4 }}>
        {filteredImages.length ? (
          <Grid container spacing={3}>
            {filteredImages.map((img, index) => (
              <Grid item xs={12} sm={6} md={4} lg={3} key={index}>
                <ImageCard image={img} />
              </Grid>
            ))}
          </Grid>
        ) : (
          <Typography variant="body1" color="text.secondary">
            No images found.
          </Typography>
        )}
      </Container>
    </Box>
  );
};

export default HomePage;