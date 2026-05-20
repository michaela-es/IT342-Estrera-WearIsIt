import React from 'react';
import { Box, Button, CircularProgress } from '@mui/material';

export const FormActions = ({ 
  isEdit, 
  loading, 
  isFormValid, 
  onSubmit, 
  onCancel 
}) => {
  return (
    <Box sx={{ 
      mt: 3, 
      display: 'flex', 
      flexDirection: 'row',  // ✅ Force horizontal
      gap: 2,
      width: '100%'
    }}>
      {onCancel && (
        <Button
          variant="outlined"
          size="large"
          onClick={onCancel}
          sx={{ 
            flex: 1,  // ✅ Equal width
            py: 1.5, 
            fontWeight: 700 
          }}
        >
          Cancel
        </Button>
      )}
      <Button
        variant="contained"
        size="large"
        disabled={!isFormValid || loading}
        onClick={onSubmit}
        sx={{ 
          flex: 1,  // ✅ Equal width
          py: 1.5, 
          fontWeight: 700 
        }}
      >
        {loading ? <CircularProgress size={28} /> : (isEdit ? 'UPDATE ITEM' : 'SAVE CLOTHING ITEM')}
      </Button>
    </Box>
  );
};