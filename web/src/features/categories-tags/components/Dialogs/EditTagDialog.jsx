import React, { useState, useEffect } from 'react';
import { Dialog, DialogTitle, DialogContent, DialogActions, TextField, Button } from '@mui/material';

export const EditTagDialog = ({ open, onClose, onSave, tag, categoryName }) => {
  const [name, setName] = useState(tag?.name || '');

  useEffect(() => {
    if (tag) setName(tag.name);
  }, [tag]);

  const handleSave = () => {
    if (name.trim() && tag) {
      onSave(tag.id, name.trim());
      onClose();
    }
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>Edit Tag in "{categoryName}"</DialogTitle>
      <DialogContent>
        <TextField
          autoFocus
          margin="dense"
          label="Tag Name"
          fullWidth
          variant="outlined"
          value={name}
          onChange={(e) => setName(e.target.value)}
          onKeyPress={(e) => e.key === 'Enter' && handleSave()}
        />
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Cancel</Button>
        <Button onClick={handleSave} variant="contained" disabled={!name.trim()}>
          Save
        </Button>
      </DialogActions>
    </Dialog>
  );
};