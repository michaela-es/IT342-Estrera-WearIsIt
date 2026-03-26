import React from 'react';
import { Alert, Collapse, IconButton } from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';

const MessageBox = ({ type, text, onClose }) => {
  if (!text) return null;

  return (
    <Collapse in={!!text}>
      <Alert
        severity={type || 'info'} // 'error' | 'success' | 'warning' | 'info'
        sx={{ mb: 2 }}
        action={
          <IconButton
            size="small"
            color="inherit"
            onClick={onClose}
          >
            <CloseIcon fontSize="small" />
          </IconButton>
        }
      >
        {text}
      </Alert>
    </Collapse>
  );
};

export default MessageBox;