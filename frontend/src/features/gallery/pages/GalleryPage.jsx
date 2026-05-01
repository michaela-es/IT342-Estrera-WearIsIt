import React, { useState, useEffect, useMemo } from 'react';
import { Container, Box, Typography } from '@mui/material';
import Navbar from '../../shared/components/NavBar';
import SearchBar from '../../search/components/SearchBar';
import ImageCard from '../components/ImageCard';
import api from '../../../api/apiClient';

const GalleryPage = () => {
  const [search, setSearch] = useState('');
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchItems = async () => {
      try {
        setLoading(true);
        const data = await api.get('/items');
        console.log('Fetched items:', data);
        setItems(data || []);
      } catch (err) {
        console.error('Error fetching items:', err);
        setError(err.response?.data?.error?.message || 'Failed to load items');
      } finally {
        setLoading(false);
      }
    };

    fetchItems();
  }, []);

  const filteredItems = useMemo(() => {
    if (!search) return items;
    
    const searchLower = search.toLowerCase();
    return items.filter(item => {
      if (item.itemName?.toLowerCase().includes(searchLower)) return true;
      
      if (item.tags?.some(tag => tag.name?.toLowerCase().includes(searchLower))) return true;
      
      if (item.categories?.some(cat => cat.name?.toLowerCase().includes(searchLower))) return true;
      
      return false;
    });
  }, [items, search]); 

  if (loading) {
    return (
      <Box sx={{ minHeight: '100vh', backgroundColor: '#e3e3e3' }}>
        <Navbar variant="light" />
        <Container maxWidth="xl" sx={{ py: 4 }}>
          <Typography textAlign="center">Loading your wardrobe...</Typography>
        </Container>
      </Box>
    );
  }

  if (error) {
    return (
      <Box sx={{ minHeight: '100vh', backgroundColor: '#e3e3e3' }}>
        <Navbar variant="light" />
        <Container maxWidth="xl" sx={{ py: 4 }}>
          <Typography color="error" textAlign="center">{error}</Typography>
        </Container>
      </Box>
    );
  }

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
            placeholder="Search by name, tag, or category..."
          />
        </Box>
      </Box>

      <Container maxWidth="xl" sx={{ py: 4 }}>
        {filteredItems.length > 0 ? (
          <Box sx={{ 
            display: 'grid', 
            gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))',
            gap: 3,
            justifyContent: 'center'
          }}>
            {filteredItems.map((item) => (
              <Box key={item.id} sx={{ maxWidth: 380, mx: 'auto', width: '100%' }}>
                <ImageCard image={item} />
              </Box>
            ))}
          </Box>
        ) : (
          <Typography variant="body1" color="text.secondary" textAlign="center">
            {search ? 'No items match your search.' : 'Your wardrobe is empty. Add some items!'}
          </Typography>
        )}
      </Container>
    </Box>
  );
};

export default GalleryPage;