import { useState, useEffect } from 'react';
import api from '@features/shared/api/apiClient';

export const useClothingItemForm = (initialData = null, isEdit = false) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);
  
  const [formData, setFormData] = useState({
    itemName: initialData?.itemName || '',
    typeId: initialData?.typeId || '',
    categoryIds: initialData?.categories?.map(c => c.id) || [],
    tagIds: initialData?.tags?.map(t => t.id) || []
  });
  
  const [selectedTags, setSelectedTags] = useState(initialData?.tags || []);
  const [imageFile, setImageFile] = useState(null);
  const [imagePreview, setImagePreview] = useState(initialData?.imageUrl || null);
  
  const [itemTypes, setItemTypes] = useState([]);
  const [availableTags, setAvailableTags] = useState([]);
  const [loadingOptions, setLoadingOptions] = useState(true);

  useEffect(() => {
    const fetchOptions = async () => {
      try {
        const [types, categoriesWithTags] = await Promise.all([
          api.get('/item-types'),
          api.get('/categories/with-tags')
        ]);
        
        setItemTypes(types || []);
        
        const allTags = [];
        categoriesWithTags?.forEach(category => {
          category.tags?.forEach(tag => {
            allTags.push({
              id: tag.id,
              name: tag.name,
              categoryId: category.id,
              categoryName: category.name
            });
          });
        });
        setAvailableTags(allTags);
      } catch (err) {
        setError('Failed to load form options');
      } finally {
        setLoadingOptions(false);
      }
    };
    
    fetchOptions();
  }, []);

  const updateFormData = (updates) => {
    setFormData(prev => ({ ...prev, ...updates }));
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleTagsChange = (tags) => {
    setSelectedTags(tags);
    setFormData(prev => ({
      ...prev,
      tagIds: tags.map(t => t.id),
      categoryIds: [...new Set(tags.map(t => t.categoryId))]
    }));
  };

  const handleImageChange = (file, preview) => {
    setImageFile(file);
    setImagePreview(preview);
  };

  const resetForm = () => {
    setFormData({
      itemName: '',
      typeId: '',
      categoryIds: [],
      tagIds: []
    });
    setSelectedTags([]);
    setImageFile(null);
    setImagePreview(null);
  };

  const isFormValid = formData.itemName && formData.typeId && (isEdit || imageFile);

  return {
    formData,
    selectedTags,
    imageFile,
    imagePreview,
    loading,
    error,
    success,
    itemTypes,
    availableTags,
    loadingOptions,
    isFormValid,
    
    setLoading,
    setError,
    setSuccess,
    updateFormData,
    handleChange,
    handleTagsChange,
    handleImageChange,
    resetForm
  };
};