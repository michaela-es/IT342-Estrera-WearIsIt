import React, { useState, useEffect } from 'react';
import {
  Box,
  Paper,
  Typography,
  List,
  ListItem,
  ListItemButton,
  ListItemText,
  Chip,
  Stack,
  CircularProgress,
  Alert,
  Divider,
  Checkbox
} from '@mui/material';
import api from '../../shared/api/apiClient';

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
      
      // Only show categories that have tags
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
      <Typography variant="subtitle1" gutterBottom fontWeight="bold">
        Categories & Tags
      </Typography>
      
      <Box sx={{ display: 'flex', gap: 2, minHeight: '300px' }}>
=        <Paper 
          variant="outlined"
          sx={{ 
            width: '200px', 
            overflowY: 'auto',
            borderRadius: 2,
            bgcolor: '#f5f5f5'
          }}
        >
          <List dense>
            {categories.map((category) => (
              <ListItemButton
                key={category.id}
                selected={selectedCategory?.id === category.id}
                onClick={() => setSelectedCategory(category)}
                sx={{
                  '&.Mui-selected': {
                    bgcolor: '#e3f2fd',
                    borderLeft: '3px solid #1976d2',
                  }
                }}
              >
                <ListItemText 
                  primary={category.name}
                  secondary={`${category.tags.length} tags`}
                  primaryTypographyProps={{ 
                    fontWeight: selectedCategory?.id === category.id ? 'bold' : 'normal',
                    fontSize: '0.9rem'
                  }}
                  secondaryTypographyProps={{ fontSize: '0.7rem' }}
                />
              </ListItemButton>
            ))}
          </List>
        </Paper>

=        <Paper 
          variant="outlined"
          sx={{ 
            flex: 1, 
            overflowY: 'auto',
            borderRadius: 2,
            p: 2
          }}
        >
          {selectedCategory ? (
            <>
              <Typography variant="subtitle2" gutterBottom fontWeight="bold" color="primary">
                {selectedCategory.name}
              </Typography>
              <Divider sx={{ mb: 2 }} />
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
            </>
          ) : (
            <Typography variant="body2" color="text.secondary" textAlign="center">
              Select a category to view tags
            </Typography>
          )}
        </Paper>
      </Box>

      {selectedTags.length > 0 && (
        <Box sx={{ mt: 2, p: 1.5, bgcolor: '#f5f5f5', borderRadius: 2 }}>
          <Typography variant="body2" color="text.secondary" gutterBottom>
            Selected tags:
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