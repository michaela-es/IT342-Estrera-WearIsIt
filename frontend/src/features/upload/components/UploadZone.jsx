import React from 'react';
import { Box, Typography, Button } from '@mui/material';
import { useDropzone } from 'react-dropzone';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';

const UploadZone = ({ imagePreview, setImageFile, setImagePreview, setError }) => {
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

  const handleClearImage = (e) => {
    e.stopPropagation();
    setImageFile(null);
    setImagePreview(null);
  };

  return (
    <Box
      {...getRootProps()}
      sx={{
        border: '2px dashed',
        borderColor: isDragActive ? 'var(--primary)' : 'var(--border-default)',
        borderRadius: 2,
        p: 4,
        minHeight: 600,
        textAlign: 'center',
        minWidth: 400,
        cursor: 'pointer',
        bgcolor: isDragActive ? 'var(--bg-info)' : 'var(--bg-container)',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        position: 'relative',
        transition: '0.2s',
        '&:hover': {
          borderColor: 'var(--primary)',
          bgcolor: 'var(--bg-page)'
        }
      }}
    >
      <input {...getInputProps()} />

      {imagePreview ? (
        <Box sx={{ position: 'relative', width: '100%' }}>
          <img 
            src={imagePreview} 
            alt="Preview" 
            style={{ 
              maxWidth: '100%', 
              maxHeight: '400px', 
              borderRadius: '8px',
              objectFit: 'contain'
            }} 
          />
          <Button
            onClick={handleClearImage}
            sx={{
              position: 'absolute',
              top: 8,
              right: 8,
              minWidth: '32px',
              width: '32px',
              height: '32px',
              borderRadius: '50%',
              bgcolor: 'rgba(0,0,0,0.6)',
              color: 'white',
              '&:hover': {
                bgcolor: 'rgba(0,0,0,0.8)',
              }
            }}
          >
            ✕
          </Button>
        </Box>
      ) : (
        <>
          <CloudUploadIcon sx={{ fontSize: 64, color: 'var(--primary)', mb: 2 }} />
          <Typography>Drag & drop a PNG or JPEG image here</Typography>
          <Typography variant="body2" color="text.secondary">
            or click to select
          </Typography>
          <Typography variant="caption" color="text.secondary" sx={{ mt: 1 }}>
            PNG or JPEG only (Max 10MB)
          </Typography>
        </>
      )}
    </Box>
  );
};

export default UploadZone;