import React from 'react';
import { 
  Autocomplete, 
  TextField, 
  Chip, 
  Box, 
  Typography,
  Avatar
} from '@mui/material';

/**
 * A searchable select component that scales its height as items are selected.
 * Uses Material UI Autocomplete for a polished feel.
 */
const ScalingItemSelector = ({ 
  options, 
  selectedItems, 
  onChange, 
  label = "Select Items",
  placeholder = "Search items (e.g. 'Blue Jeans')"
}) => {
  return (
    <Box sx={{ width: '100%', mt: 2 }}>
      <Autocomplete
        multiple
        options={options}
        value={selectedItems}
        onChange={(event, newValue) => onChange(newValue)}
        getOptionLabel={(option) => option.itemName || ''}
        isOptionEqualToValue={(option, value) => option.id === value.id}
        renderInput={(params) => (
          <TextField
            {...params}
            variant="outlined"
            label={label}
            placeholder={selectedItems.length === 0 ? placeholder : ""}
            sx={{
              '& .MuiOutlinedInput-root': {
                borderRadius: 2,
                transition: 'all 0.2s ease-in-out',
                '&:hover': {
                  boxShadow: '0 2px 8px rgba(0,0,0,0.1)'
                }
              }
            }}
          />
        )}
        renderOption={(props, option) => (
          <Box component="li" {...props} sx={{ display: 'flex', alignItems: 'center', gap: 2, p: 1 }}>
            <Avatar 
              src={option.imageUrl} 
              variant="rounded" 
              sx={{ width: 40, height: 40, bgcolor: 'grey.200' }}
            >
              {option.itemName?.[0]}
            </Avatar>
            <Box>
              <Typography variant="body1" fontWeight={500}>
                {option.itemName}
              </Typography>
              <Typography variant="caption" color="text.secondary">
                {option.typeName || 'No Type'}
              </Typography>
            </Box>
          </Box>
        )}
        renderTags={(value, getTagProps) =>
          value.map((option, index) => (
            <Chip
              {...getTagProps({ index })}
              key={option.id}
              label={option.itemName}
              avatar={<Avatar src={option.imageUrl} />}
              size="medium"
              sx={{ 
                borderRadius: '8px',
                bgcolor: 'background.paper',
                border: '1px solid',
                borderColor: 'divider',
                '& .MuiChip-label': { fontWeight: 500 }
              }}
            />
          ))
        }
        sx={{
          '& .MuiAutocomplete-tag': {
            m: 0.5
          }
        }}
      />
    </Box>
  );
};

export default ScalingItemSelector;