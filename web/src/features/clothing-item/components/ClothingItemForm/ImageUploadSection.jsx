import React from 'react';
import { Box, Grid } from '@mui/material';
import { useDropzone } from 'react-dropzone';
import UploadZone from '@features/upload/components/UploadZone';

export const ImageUploadSection = ({ imagePreview, onImageChange, setError }) => {
  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop: (acceptedFiles, rejectedFiles) => {
      if (rejectedFiles?.length > 0) {
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
        const previewUrl = URL.createObjectURL(file);
        onImageChange(file, previewUrl);
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

  return (
    <Grid item xs={12} md={4}>
      <UploadZone
        imagePreview={imagePreview}
        setImageFile={(file) => onImageChange(file, URL.createObjectURL(file))}
        setError={setError}
        getRootProps={getRootProps}
        getInputProps={getInputProps}
        isDragActive={isDragActive}
      />
    </Grid>
  );
};