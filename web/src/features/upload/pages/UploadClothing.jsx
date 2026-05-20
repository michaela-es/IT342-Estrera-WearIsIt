// src/features/upload/pages/UploadClothing.jsx
import React from 'react';
import { useNavigate } from 'react-router-dom';
import ClothingItemForm from '@features/clothing-item/components/ClothingItemForm';

const UploadClothing = () => {
  const navigate = useNavigate();

  return (
    <ClothingItemForm 
      isEdit={false}
      onSuccess={() => setTimeout(() => navigate('/'), 2000)}
    />
  );
};

export default UploadClothing;