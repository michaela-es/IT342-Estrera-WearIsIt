import React from 'react';
import { Box, Typography, TextField, Divider } from '@mui/material';
import TypeSelector from '@features/upload/components/TypeSelector';
import CategoryField from '@features/upload/components/CategoryField';

export const DetailsSection = ({ 
  formData, 
  selectedTags, 
  onFieldChange, 
  onTagsChange,
  itemTypes 
}) => {
  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
      <Typography variant="subtitle1" gutterBottom fontWeight="bold">
        Item Details
      </Typography>
      
      <TextField
        fullWidth
        label="Item Name"
        name="itemName"
        value={formData.itemName}
        onChange={onFieldChange}
        required
        sx={{ mb: 2 }}
      />

      <TypeSelector
        value={formData.typeId}
        onChange={onFieldChange}
        types={itemTypes}
      />

      <Divider sx={{ my: 3 }} />

      <CategoryField 
        onTagsChange={onTagsChange}
        initialSelectedTags={selectedTags}
      />
    </Box>
  );
};