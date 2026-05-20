import React from 'react';
import {
  Box,
  Paper,
  Typography,
  Grid,
  Alert,
  CircularProgress
} from '@mui/material';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import { useClothingItemForm } from '../hooks/useClothingItemForm';
import { ImageUploadSection } from './ClothingItemForm/ImageUploadSection';
import { DetailsSection } from './ClothingItemForm/DetailsSection';
import { FormActions } from './ClothingItemForm/FormActions';
import api from '@features/shared/api/apiClient';

const ClothingItemForm = ({ initialData = null, isEdit = false, onSuccess, onCancel }) => {
  const {
    formData,
    selectedTags,
    imageFile,
    imagePreview,
    loading,
    error,
    success,
    itemTypes,
    loadingOptions,
    isFormValid,
    setError,
    setLoading,
    setSuccess,
    handleChange,
    handleTagsChange,
    handleImageChange,
    resetForm
  } = useClothingItemForm(initialData, isEdit);

const handleSubmit = async () => {
  if (!isFormValid) return;

  setLoading(true);
  setError(null);

  try {
    const payload = {
      itemName: formData.itemName,
      typeId: parseInt(formData.typeId),
      categoryIds: formData.categoryIds,
      tagIds: formData.tagIds
    };

    let itemId;
    let updatedItem = null;
    
    if (isEdit && initialData?.id) {
      await api.put(`/items/${initialData.id}`, payload);
      itemId = initialData.id;
      
      if (imageFile && imageFile instanceof File) {
        const uploadData = new FormData();
        uploadData.append('image', imageFile);
        await api.put(`/items/${itemId}/image`, uploadData);
      }
      
      sessionStorage.removeItem('gallery_items');
      
      updatedItem = await api.get(`/items/${itemId}`);
      
    } else {
      const response = await api.post('/items', payload);
      itemId = response.id;
      
      if (imageFile) {
        const uploadData = new FormData();
        uploadData.append('image', imageFile);
        await api.post(`/items/${itemId}/image`, uploadData);
      }
      
      sessionStorage.removeItem('gallery_items');
      updatedItem = await api.get(`/items/${itemId}`);
    }

    setSuccess(`Item ${isEdit ? 'updated' : 'created'} successfully!`);
    
    if (onSuccess) onSuccess(updatedItem);
    if (!isEdit) resetForm();
    
  } catch (err) {
    setError(err.message || `Failed to ${isEdit ? 'update' : 'create'} item`);
  } finally {
    setLoading(false);
  }
};

  if (loadingOptions) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box sx={{ minHeight: '100vh', display: 'flex', justifyContent: 'center', alignItems: 'center', p: 2, bgcolor: 'background.default' }}>
      <Paper sx={{ p: 4, borderRadius: 3, width: '100%', maxWidth: '1000px', bgcolor: 'background.paper' }}>
        
        {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>{error}</Alert>}
        {success && <Alert severity="success" sx={{ mb: 2 }} icon={<CheckCircleIcon />}>{success}</Alert>}

        <Typography variant="h5" sx={{ mb: 3, fontWeight: 700 }}>
          {isEdit ? 'Edit Clothing Item' : 'Upload New Clothing Item'}
        </Typography>

        <Grid container spacing={3}>
          <ImageUploadSection 
            imagePreview={imagePreview}
            onImageChange={handleImageChange}
            setError={setError}
          />
          
          <Grid item xs={12} md={8}>
            <DetailsSection 
              formData={formData}
              selectedTags={selectedTags}
              onFieldChange={handleChange}
              onTagsChange={handleTagsChange}
              itemTypes={itemTypes}
            />
            
            <FormActions 
              isEdit={isEdit}
              loading={loading}
              isFormValid={isFormValid}
              onSubmit={handleSubmit}
              onCancel={onCancel}
            />
          </Grid>
        </Grid>
      </Paper>
    </Box>
  );
};

export default ClothingItemForm;