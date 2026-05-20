import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { CircularProgress, Box } from '@mui/material';
import ClothingItemForm from '../components/ClothingItemForm';
import api from '@features/shared/api/apiClient';

const EditClothingItem = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [item, setItem] = useState(null);
  const [loading, setLoading] = useState(true);
  const [refreshKey, setRefreshKey] = useState(0); 
  const fetchItem = async () => {
    setLoading(true);
    try {
      const data = await api.get(`/items/${id}`);
      setItem(data);
    } catch (error) {
      navigate('/');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (id) fetchItem();
  }, [id, refreshKey]);

const handleSuccess = (updatedItem) => {
  setRefreshKey(prev => prev + 1); 
  navigate(`/items/${id}`, { state: { updatedItem } });
};

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh' }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <ClothingItemForm 
      isEdit={true}
      initialData={item}
      onSuccess={handleSuccess} 
      onCancel={() => navigate(`/items/${id}`)}
    />
  );
};

export default EditClothingItem;