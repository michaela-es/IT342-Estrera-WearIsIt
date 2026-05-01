import React, { useState, useEffect } from 'react';
import {
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  FormHelperText
} from '@mui/material';
import api from '../../shared/api/apiClient';

const TypeSelector = ({ value, onChange, error, required = true }) => {
  const [types, setTypes] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchTypes();
  }, []);

  const fetchTypes = async () => {
    try {
      const response = await api.get('/item-types');
      const typesData = response.data || response;
      setTypes(typesData);
    } catch (error) {
      console.error('Failed to fetch types:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <FormControl fullWidth required={required} error={!!error}>
      <InputLabel>Item Type</InputLabel>
      <Select
        name="typeId"
        value={value || ''}
        onChange={onChange}
        label="Item Type"
        disabled={loading}
      >
        {types.map((type) => (
          <MenuItem key={type.id} value={type.id}>
            {type.name}
          </MenuItem>
        ))}
      </Select>
      {error && <FormHelperText>{error}</FormHelperText>}
    </FormControl>
  );
};

export default TypeSelector;