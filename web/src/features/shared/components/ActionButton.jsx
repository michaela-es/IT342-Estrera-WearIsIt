// components/CategoryField.js
import React, { useState, useEffect } from 'react';
import {
  Box,
  Paper,
  Typography,
  Chip,
  Stack,
  CircularProgress,
  Alert,
  Divider
} from '@mui/material';
import api from '@features/shared/api/apiClient';

const CategoryField = ({ onTagsChange, initialSelectedTags = [] }) => {
  const [categories, setCategories] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [selectedTags, setSelectedTags] = useState(initialSelectedTags);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchCategoriesWithTags();
  }, []);

  const fetchCategoriesWithTags = async () => {
    try {
      setLoading(true);
      const response = await api.get('/categories/with-tags');
      const categoriesData = response.data || response;
      
      const categoriesWithTags = categoriesData.filter(cat => cat.tags && cat.tags.length > 0);
      setCategories(categoriesWithTags);
      
      if (categoriesWithTags.length > 0) {
        setSelectedCategory(categoriesWithTags[0]);
      }
    } catch (err) {
      console.error('Failed to fetch categories:', err);
      setError('Failed to load categories and tags');
    } finally {
      setLoading(false);
    }
  };

  const handleTagToggle = (tag) => {
    const isSelected = selectedTags.some(t => t.id === tag.id);
    let newSelectedTags;
    
    if (isSelected) {
      newSelectedTags = selectedTags.filter(t => t.id !== tag.id);
    } else {
      newSelectedTags = [...selectedTags, tag];
    }
    
    setSelectedTags(newSelectedTags);
    
    if (onTagsChange) {
      onTagsChange(newSelectedTags);
    }
  };

  const handleRemoveTag = (tagId) => {
    const newSelectedTags = selectedTags.filter(t => t.id !== tagId);
    setSelectedTags(newSelectedTags);
    if (onTagsChange) {
      onTagsChange(newSelectedTags);
    }
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" p={4}>
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Alert severity="error" sx={{ m: 2 }}>
        {error}
      </Alert>
    );
  }

  return (
    <Box>
      {/* Horizontal Categories Scroll */}
      <Typography variant="subtitle1" gutterBottom fontWeight="bold">
        Categories
      </Typography>
      <Box sx={{ 
        display: 'flex', 
        gap: 1, 
        overflowX: 'auto', 
        pb: 2,
        mb: 2,
        '&::-webkit-scrollbar': {
          height: '8px',
        },
        '&::-webkit-scrollbar-track': {
          background: '#f1f1f1',
          borderRadius: '4px',
        },
        '::-webkit-scrollbar-thumb': {
          background: '#888',
          borderRadius: '4px',
        },
      }}>
        {categories.map((category) => (
          <Chip
            key={category.id}
            label={`${category.name} (${category.tags.length})`}
            onClick={() => setSelectedCategory(category)}
            color={selectedCategory?.id === category.id ? 'primary' : 'default'}
            variant={selectedCategory?.id === category.id ? 'filled' : 'outlined'}
            sx={{ 
              cursor: 'pointer',
              px: 1,
              '&:hover': {
                transform: 'scale(1.02)',
                transition: 'transform 0.1s'
              }
            }}
          />
        ))}
      </Box>

      {/* Tags for selected category */}
      {selectedCategory && (
        <>
          <Typography variant="subtitle1" gutterBottom fontWeight="bold">
            Tags for {selectedCategory.name}
          </Typography>
          <Paper 
            variant="outlined"
            sx={{ 
              p: 2,
              mb: 2,
              borderRadius: 2,
              bgcolor: '#fafafa',
              minHeight: '80px'
            }}
          >
            <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
              {selectedCategory.tags.map((tag) => {
                const isSelected = selectedTags.some(t => t.id === tag.id);
                return (
                  <Chip
                    key={tag.id}
                    label={tag.name}
                    onClick={() => handleTagToggle(tag)}
                    color={isSelected ? 'primary' : 'default'}
                    variant={isSelected ? 'filled' : 'outlined'}
                    sx={{
                      cursor: 'pointer',
                      '&:hover': {
                        transform: 'scale(1.02)',
                        transition: 'transform 0.1s'
                      }
                    }}
                  />
                );
              })}
              {selectedCategory.tags.length === 0 && (
                <Typography variant="body2" color="text.secondary">
                  No tags available
                </Typography>
              )}
            </Box>
          </Paper>
        </>
      )}

      {/* Selected Tags Display */}
      {selectedTags.length > 0 && (
        <Box sx={{ mt: 2, p: 2, bgcolor: '#e3f2fd', borderRadius: 2 }}>
          <Typography variant="body2" fontWeight="bold" gutterBottom>
            Selected Tags:
          </Typography>
          <Stack direction="row" spacing={1} flexWrap="wrap" useFlexGap>
            {selectedTags.map((tag) => (
              <Chip
                key={tag.id}
                label={tag.name}
                size="small"
                color="primary"
                onDelete={() => handleRemoveTag(tag.id)}
                deleteIcon={<span style={{ cursor: 'pointer' }}>×</span>}
              />
            ))}
          </Stack>
        </Box>
      )}
    </Box>
  );
};

export default CategoryField;