import React from 'react';
import { Chip } from '@mui/material';

const ItemTag = ({ label, size = 'small', onClick, ...props }) => {
  return (
    <Chip
      label={label}
      size={size}
      onClick={onClick}
      sx={{
        borderRadius: '2px',
        backgroundColor: 'var(--bg-input)',
        border: `1px solid var(--border-default)`,
        fontWeight: 500,
        fontSize: '13px',
        color: 'var(--text-secondary)',
        '&:hover': {
          borderColor: 'var(--primary)',
          backgroundColor: 'var(--bg-info)',
          color: 'var(--primary)'
        }
      }}
      {...props}
    />
  );
};

export default ItemTag;