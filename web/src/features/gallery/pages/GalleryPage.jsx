import React, { useState, useMemo, useCallback } from 'react';
import { Box, Typography, CircularProgress, IconButton, Tooltip, Button } from '@mui/material';
import RefreshIcon from '@mui/icons-material/Refresh';
import SearchBar from '@features/search/components/SearchBar';
import ImageCard from '@features/gallery/components/ImageCard';
import api from '@features/shared/api/apiClient';
import { useCache } from '@features/shared/hooks/useCache';

const GalleryPage = () => {
  const [search, setSearch] = useState('');
  const [refreshTrigger, setRefreshTrigger] = useState(0);
  
  const fetchItems = useCallback(async () => {
    const data = await api.get('/items');
    if (data && data.content) {
      return data.content;
    }
    if (Array.isArray(data)) {
      return data;
    }
    return [];
  }, []); 
  
  const { data: items = [], loading, error } = useCache(
    'gallery_items',
    fetchItems,
    5 * 60 * 1000,
    refreshTrigger
  );

  const handleRefresh = useCallback(() => {
    setRefreshTrigger(prev => prev + 1);
  }, []);

  const filteredItems = useMemo(() => {
    if (!search) return items;
    const searchLower = search.toLowerCase();
    return items.filter(item => 
      item.itemName?.toLowerCase().includes(searchLower) ||
      item.tags?.some(tag => tag.name?.toLowerCase().includes(searchLower)) ||
      item.categories?.some(cat => cat.name?.toLowerCase().includes(searchLower))
    );
  }, [items, search]);
  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '60vh' }}>
        <CircularProgress size={60} thickness={4} />
      </Box>
    );
  }

  if (error) {
    return (
      <Box sx={{ textAlign: 'center', py: 4 }}>
        <Typography color="error" gutterBottom>
          Failed to load items: {error.message || error}
        </Typography>
        <Button variant="contained" onClick={handleRefresh} startIcon={<RefreshIcon />}>
          Retry
        </Button>
      </Box>
    );
  }

  return (
    <>
      <Box sx={{ mb: 3, display: 'flex', gap: 2, alignItems: 'center' }}>
        <Box sx={{ flex: 1 }}>
          <SearchBar
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            fullWidth
            placeholder="Search by name, tag, or category..."
          />
        </Box>
      </Box>
      
      {filteredItems.length > 0 ? (
        <Box sx={{ 
          display: 'grid', 
          gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))',
          gap: 3
        }}>
          {filteredItems.map((item) => (
            <ImageCard key={item.id} image={item} />
          ))}
        </Box>
      ) : (
        <Typography variant="body1" color="text.secondary" textAlign="center" sx={{ py: 8 }}>
          {search ? 'No items match your search.' : 'Your wardrobe is empty. Add some items!'}
        </Typography>
      )}
    </>
  );
};

export default GalleryPage;