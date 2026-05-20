import React from 'react';
import { List, ListItem, ListItemText, ListItemSecondaryAction, IconButton, Box, Typography } from '@mui/material';

export const CategoryList = ({ categories, selectedId, onSelect, onEdit, onDelete }) => {
  if (categories.length === 0) {
    return (
      <Box sx={{ textAlign: 'center', py: 4 }}>
        <Typography variant="body2" color="text.secondary">
          No categories yet. Create your first category!
        </Typography>
      </Box>
    );
  }

  return (
    <List sx={{ bgcolor: 'background.paper', borderRadius: 2 }}>
      {categories.map((category) => (
        <ListItem
          key={category.id}
          selected={selectedId === category.id}
          sx={{
            borderRadius: 1,
            mb: 0.5,
            '&.Mui-selected': {
              bgcolor: 'primary.light',
              color: 'primary.contrastText',
              '&:hover': { bgcolor: 'primary.main' },
            },
          }}
        >
          <ListItemText
            primary={category.name}
            secondary={`${category.tags?.length || 0} tags`}
            onClick={() => onSelect(category.id)}
            sx={{ cursor: 'pointer' }}
          />
          <ListItemSecondaryAction>
            <IconButton size="small" onClick={() => onEdit(category)} sx={{ mr: 1 }}>
              ✏️
            </IconButton>
            <IconButton size="small" onClick={() => onDelete(category)} color="error">
              🗑️
            </IconButton>
          </ListItemSecondaryAction>
        </ListItem>
      ))}
    </List>
  );
};