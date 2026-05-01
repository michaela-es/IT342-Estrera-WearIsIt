
import Navbar from '../../../components/layout/Navbar';
import React, { useState } from 'react';
import {
  Box,
  Paper,
  Typography,
  TextField,
  Button,
  Grid,
  Alert,
  CircularProgress,
  Divider
} from '@mui/material';
import { useDropzone } from 'react-dropzone';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import api from '../../../api/apiClient';
import TypeSelector from '../components/TypeSelector';
import CategoryField from '../components/CategoryField';
import UploadZone from '../components/UploadZone';

const UploadClothing = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);
  
  const [formData, setFormData] = useState({
    itemName: '',
    typeId: '',
    categoryIds: [],
    tagIds: []
  });
  
  const [selectedTags, setSelectedTags] = useState([]);
  const [imageFile, setImageFile] = useState(null);
  const [imagePreview, setImagePreview] = useState(null);

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

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop: (acceptedFiles, rejectedFiles) => {
      if (rejectedFiles && rejectedFiles.length > 0) {
        const rejection = rejectedFiles[0];
        if (rejection.errors[0].code === 'file-invalid-type') {
          setError('Only PNG and JPEG images are allowed!');
        } else if (rejection.errors[0].code === 'file-too-large') {
          setError('File size must be less than 10MB');
        }
        return;
      }
      
      const file = acceptedFiles[0];
      if (file) {
        setImageFile(file);
        const previewUrl = URL.createObjectURL(file);
        setImagePreview(previewUrl);
        setError(null);
      }
    },
    accept: {
      'image/png': ['.png'],
      'image/jpeg': ['.jpg', '.jpeg']
    },
    maxFiles: 1,
    multiple: false,
    maxSize: 10 * 1024 * 1024
  });

  const handleSubmit = async () => {
    if (!formData.itemName) {
      setError('Please enter an item name');
      return;
    }
    if (!formData.typeId) {
      setError('Please select an item type');
      return;
    }
    if (!imageFile) {
      setError('Please upload an image');
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const payload = {
        itemName: formData.itemName,
        typeId: parseInt(formData.typeId),
        categoryIds: formData.categoryIds,
        tagIds: formData.tagIds
      };

      const response = await api.post('/items', payload);
      const createdItem = response.data || response;
      const itemId = createdItem.id;

      const uploadData = new FormData();
      uploadData.append('image', imageFile);

      await api.post(`/items/${itemId}/image`, uploadData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });

      setSuccess('Clothing item uploaded successfully!');
      
      setTimeout(() => {
        setFormData({
          itemName: '',
          typeId: '',
          categoryIds: [],
          tagIds: []
        });
        setSelectedTags([]);
        setImageFile(null);
        setImagePreview(null);
        setSuccess(null);
      }, 2000);
      
    } catch (err) {
      setError(err.message || 'Failed to upload clothing item');
    } finally {
      setLoading(false);
    }
  };

  const isFormValid = formData.itemName && formData.typeId && imageFile;

  return (
    <>
    <Navbar/>
    <Box
      sx={{
        minHeight: '100vh',
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        p: 2,
        bgcolor: 'var(--bg-page)'
      }}
    >
      <Paper
        sx={{
          p: 4,
          borderRadius: 3,
          width: '100%',
          maxWidth: '1000px',
          bgcolor: 'var(--bg-container)',
          color: 'var(--text-primary)'
        }}
      >
        {error && (
          <Alert
            severity="error"
            sx={{
              mb: 2,
              bgcolor: 'var(--bg-error)',
              color: 'var(--text-error)'
            }}
            onClose={() => setError(null)}
          >
            {error}
          </Alert>
        )}

        {success && (
          <Alert
            severity="success"
            sx={{
              mb: 2,
              bgcolor: 'var(--bg-success)',
              color: 'var(--text-success)'
            }}
            icon={<CheckCircleIcon />}
          >
            {success}
          </Alert>
        )}

        <Grid container spacing={3} justifyContent="center" alignItems="flex-start">
          {/* LEFT */}
            <UploadZone 
                imagePreview={imagePreview}
                setImageFile={setImageFile}
                setImagePreview={setImagePreview}
                setError={setError}
              />
          {/* RIGHT */}
          <Grid item xs={12} md={8}>
            <Box sx={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
              <Typography variant="subtitle1" gutterBottom fontWeight="bold">
                Item Details
              </Typography>
              
              <TextField
                fullWidth
                label="Item Name"
                name="itemName"
                value={formData.itemName}
                onChange={handleChange}
                required
                sx={{
                  mb: 2,
                  '& .MuiOutlinedInput-root': {
                    bgcolor: 'var(--bg-container)',
                    '& fieldset': {
                      borderColor: 'var(--border-default)'
                    },
                    '&:hover fieldset': {
                      borderColor: 'var(--primary)'
                    },
                    '&.Mui-focused fieldset': {
                      borderColor: 'var(--border-focus)'
                    }
                  },
                  '& .MuiInputLabel-root': {
                    color: 'var(--text-light)'
                  }
                }}
              />

              <TypeSelector
                value={formData.typeId}
                onChange={handleChange}
              />

              <Divider sx={{ my: 3 }} />

              <CategoryField 
                onTagsChange={handleTagsChange}
                initialSelectedTags={selectedTags}
              />

              <Box sx={{ mt: 3 }}>
                <Button
                  fullWidth
                  variant="contained"
                  size="large"
                  disabled={!isFormValid || loading}
                  onClick={handleSubmit}
                  sx={{
                    py: 1.8,
                    fontWeight: 700,
                    background: 'var(--btn-primary)',
                    '&:hover': {
                      background: 'var(--btn-primary-hover)'
                    }
                  }}
                >
                  {loading ? <CircularProgress size={28} /> : 'SAVE CLOTHING ITEM'}
                </Button>
              </Box>
            </Box>
          </Grid>
        </Grid>
      </Paper>
    </Box>
        </>
  );
};

export default UploadClothing;