import React, { useState } from 'react';
import { 
  Box, 
  Container, 
  Grid, 
  Typography, 
  Paper, 
  Button, 
  Divider,
  CircularProgress,
  Alert,
  Fab,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  IconButton,
  Stack
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import SaveIcon from '@mui/icons-material/Save';
import CloseIcon from '@mui/icons-material/Close';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';
import { useDropzone } from 'react-dropzone';
import { useOutfits } from '../hooks/useOutfits';
import ScalingItemSelector from '../components/ScalingItemSelector';
import OutfitCard from '../components/OutfitCard';
import outfitApi from '../services/outfitApi';

const OutfitKanbanPage = () => {
  const { outfits, availableItems, loading, error, handleWearOutfit, handleDeleteOutfit, fetchOutfits } = useOutfits();
  const [openBuilder, setOpenBuilder] = useState(false);
  const [outfitName, setOutfitName] = useState('');
  const [selectedItems, setSelectedItems] = useState([]);
  const [imageFile, setImageFile] = useState(null);
  const [imagePreview, setImagePreview] = useState(null);
  const [saving, setSaving] = useState(false);
  const [builderError, setBuilderError] = useState(null);
  const [editingOutfit, setEditingOutfit] = useState(null);

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop: (acceptedFiles) => {
      const file = acceptedFiles[0];
      if (file) {
        setImageFile(file);
        setImagePreview(URL.createObjectURL(file));
      }
    },
    accept: { 'image/*': [] },
    multiple: false
  });

const handleEdit = (outfit) => {
  setEditingOutfit(outfit);
  setOutfitName(outfit.outfitName);
  setImagePreview(outfit.coverImageUrl);
  
  const itemsToSelect = outfit.items?.map(item => ({
    id: item.itemId,        
    itemName: item.itemName,
    imageUrl: item.imageUrl,
    typeName: item.itemTypeName || 'No Type' 
  })) || [];
  
  console.log('Mapped items for edit:', itemsToSelect); 
  
  setSelectedItems(itemsToSelect);
  setOpenBuilder(true);
};

  const handleSaveOutfit = async () => {
    if (!outfitName || selectedItems.length === 0) return;
    
    setSaving(true);
    setBuilderError(null);
    try {
      const payload = {
        outfitName,
        items: selectedItems.map((item, index) => ({
          itemId: item.id,
          position: index,
          notes: "" 
        }))
      };
      
      let outfitId;
      try {
        if (editingOutfit) {
          await outfitApi.update(editingOutfit.id, payload);
          outfitId = editingOutfit.id;
        } else {
          const response = await outfitApi.create(payload);
          outfitId = response.id;
        }
      } catch (apiErr) {
        setBuilderError(apiErr.message || 'Failed to save outfit details');
        setSaving(false);
        return;
      }

      if (imageFile && outfitId) {
        try {
          await outfitApi.uploadImage(outfitId, imageFile);
        } catch (imgErr) {
          console.error('Failed to upload image', imgErr);
          // Don't block success if only image failed, but warn
          setBuilderError('Outfit saved, but image upload failed: ' + (imgErr.message || 'Unknown error'));
          setSaving(false);
          fetchOutfits();
          return;
        }
      }

      setOpenBuilder(false);
      resetBuilder();
      fetchOutfits();
    } catch (err) {
      console.error('Unexpected error', err);
      setBuilderError('An unexpected error occurred');
    } finally {
      setSaving(false);
    }
  };

  const resetBuilder = () => {
    setOutfitName('');
    setSelectedItems([]);
    setImageFile(null);
    setImagePreview(null);
    setEditingOutfit(null);
    setBuilderError(null);
  };

  const handleCloseBuilder = () => {
    if (saving) return;
    setOpenBuilder(false);
    resetBuilder();
  };

  const getGroupedSelectedItems = () => {
    return selectedItems.reduce((acc, item) => {
      const type = item.typeName || 'Other';
      if (!acc[type]) acc[type] = [];
      acc[type].push(item);
      return acc;
    }, {});
  };

  if (loading) return <Box sx={{ display: 'flex', justifyContent: 'center', p: 8 }}><CircularProgress /></Box>;

  return (
    <Container maxWidth="xl" sx={{ py: 4 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
        <Typography variant="h4" fontWeight={700}>Outfit Management</Typography>
        <Button 
          variant="contained" 
          startIcon={<AddIcon />} 
          onClick={() => setOpenBuilder(true)}
          sx={{ borderRadius: 2 }}
        >
          Create Outfit
        </Button>
      </Box>

      {error && <Alert severity="error" sx={{ mb: 3 }}>{error}</Alert>}

      <Grid container spacing={3}>
        {outfits.length === 0 ? (
          <Grid item xs={12}>
            <Paper sx={{ p: 6, textAlign: 'center', borderRadius: 4, bgcolor: 'background.default', border: '2px dashed', borderColor: 'divider' }}>
              <Typography color="text.secondary">No outfits created yet. Start by building your first look!</Typography>
            </Paper>
          </Grid>
        ) : (
          outfits.map(outfit => (
            <Grid item xs={12} sm={6} md={4} lg={3} key={outfit.id}>
              <OutfitCard 
                outfit={outfit} 
                onWear={handleWearOutfit} 
                onDelete={handleDeleteOutfit}
                onEdit={handleEdit}
              />
            </Grid>
          ))
        )}
      </Grid>

      {/* Outfit Builder Dialog */}
      <Dialog 
        open={openBuilder} 
        onClose={handleCloseBuilder}
        maxWidth="md"
        fullWidth
        PaperProps={{ sx: { borderRadius: 3 } }}
      >
        <DialogTitle sx={{ m: 0, p: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Typography variant="h6" fontWeight={600}>
            {editingOutfit ? 'Edit Outfit' : 'Build New Outfit'}
          </Typography>
          <IconButton onClick={handleCloseBuilder} disabled={saving}>
            <CloseIcon />
          </IconButton>
        </DialogTitle>
        <Divider />
        <DialogContent sx={{ p: 3 }}>
          {builderError && <Alert severity="error" sx={{ mb: 3 }} onClose={() => setBuilderError(null)}>{builderError}</Alert>}
          
          <TextField
            fullWidth
            label="Outfit Name"
            placeholder="e.g., Casual Friday, Date Night"
            value={outfitName}
            onChange={(e) => setOutfitName(e.target.value)}
            sx={{ mb: 3 }}
          />

          <Typography variant="subtitle2" color="text.secondary" sx={{ mb: 1, fontWeight: 600 }}>
            COVER IMAGE (OPTIONAL)
          </Typography>
          <Box 
            {...getRootProps()} 
            sx={{ 
              border: '2px dashed', 
              borderColor: isDragActive ? 'primary.main' : 'divider',
              borderRadius: 3,
              p: 2,
              textAlign: 'center',
              cursor: 'pointer',
              mb: 3,
              bgcolor: isDragActive ? 'action.hover' : 'background.paper',
              transition: 'all 0.2s',
              '&:hover': { borderColor: 'primary.main', bgcolor: 'action.hover' }
            }}
          >
            <input {...getInputProps()} />
            {imagePreview ? (
              <Box sx={{ position: 'relative', width: '100%', height: 150 }}>
                <Box 
                  component="img" 
                  src={imagePreview} 
                  sx={{ width: '100%', height: '100%', objectFit: 'cover', borderRadius: 2 }} 
                />
                <Box sx={{ 
                  position: 'absolute', 
                  top: 0, left: 0, right: 0, bottom: 0, 
                  display: 'flex', alignItems: 'center', justifyContent: 'center',
                  bgcolor: 'rgba(0,0,0,0.3)', borderRadius: 2, opacity: 0,
                  '&:hover': { opacity: 1 }, transition: 'opacity 0.2s'
                }}>
                  <Typography color="white" variant="button">Change Image</Typography>
                </Box>
              </Box>
            ) : (
              <Box sx={{ py: 2 }}>
                <CloudUploadIcon sx={{ fontSize: 40, color: 'text.secondary', mb: 1 }} />
                <Typography color="text.secondary">
                  {isDragActive ? 'Drop image here' : 'Click or drag cover image here'}
                </Typography>
              </Box>
            )}
          </Box>

          <Typography variant="subtitle2" color="text.secondary" sx={{ mb: 1, fontWeight: 600 }}>
            ADD ITEMS TO OUTFIT
          </Typography>
          <ScalingItemSelector 
            options={availableItems}
            selectedItems={selectedItems}
            onChange={setSelectedItems}
          />

          <Box sx={{ mt: 4 }}>
            <Typography variant="subtitle2" color="text.secondary" sx={{ mb: 2, fontWeight: 600 }}>
              OUTFIT PREVIEW (BY TYPE)
            </Typography>
            <Box sx={{ 
              display: 'flex', 
              gap: 2, 
              overflowX: 'auto', 
              pb: 2,
              '&::-webkit-scrollbar': { height: 8 },
              '&::-webkit-scrollbar-thumb': { bgcolor: 'divider', borderRadius: 4 }
            }}>
              {Object.entries(getGroupedSelectedItems()).map(([typeName, items]) => (
                <Paper key={typeName} sx={{ 
                  p: 2, 
                  minWidth: 200, 
                  bgcolor: 'grey.50',
                  borderTop: '4px solid',
                  borderColor: 'primary.main',
                  borderRadius: '0 0 12px 12px'
                }}>
                  <Typography variant="overline" fontWeight={700} color="text.secondary">
                    {typeName}
                  </Typography>
                  <Divider sx={{ my: 1 }} />
                  <Stack spacing={1}>
                    {items.map(item => (
                      <Paper key={item.id} sx={{ p: 1, display: 'flex', alignItems: 'center', gap: 1, borderRadius: 2 }}>
                        <Box 
                          component="img" 
                          src={item.imageUrl} 
                          sx={{ width: 40, height: 40, borderRadius: 1, objectFit: 'cover' }}
                        />
                        <Typography variant="caption" fontWeight={600} noWrap sx={{ maxWidth: 120 }}>
                          {item.itemName}
                        </Typography>
                      </Paper>
                    ))}
                  </Stack>
                </Paper>
              ))}
              {selectedItems.length === 0 && (
                <Typography variant="caption" color="text.disabled" sx={{ py: 4, width: '100%', textAlign: 'center' }}>
                  Select items to see preview
                </Typography>
              )}
            </Box>
          </Box>
        </DialogContent>
        <DialogActions sx={{ p: 2, px: 3 }}>
          <Button onClick={handleCloseBuilder} color="inherit">Cancel</Button>
          <Button 
            variant="contained" 
            startIcon={saving ? <CircularProgress size={20} /> : <SaveIcon />} 
            onClick={handleSaveOutfit}
            disabled={saving || !outfitName || selectedItems.length === 0}
            sx={{ borderRadius: 2, px: 4 }}
          >
            Save Outfit
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default OutfitKanbanPage;