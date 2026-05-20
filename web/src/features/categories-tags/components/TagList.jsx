import React from 'react';
import { Box, Chip, Paper, Typography } from '@mui/material';

export const TagList = ({ tags, categoryId, onEdit, onDelete }) => {
  if (!tags || tags.length === 0) {
    return (
      <Paper sx={{ p: 3, textAlign: 'center', bgcolor: 'grey.50' }}>
        <Typography variant="body2" color="text.secondary">
          No tags yet. Create your first tag!
        </Typography>
      </Paper>
    );
  }

  return (
    <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
      {tags.map((tag) => (
        <Chip
          key={tag.id}
          label={tag.name}
          onDelete={() => onDelete(tag.id, categoryId)}
          onClick={() => onEdit(tag)}
          sx={{
            fontSize: '0.85rem',
            fontWeight: 500,
            '&:hover': { bgcolor: 'action.hover' },
          }}
        />
      ))}
    </Box>
  );
};