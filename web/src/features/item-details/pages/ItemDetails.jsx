// src/features/item-details/pages/ItemDetails.jsx
import { useNavigate, useParams } from 'react-router-dom';
import { useState, useEffect } from 'react';
import {
  Container,
  Box,
  Typography,
  Button,
  Paper,
  Stack,
  Grid,
  CircularProgress,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  IconButton,
  Alert,
  useTheme
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import api from '@features/shared/api/apiClient';
import ItemTag from '@features/shared/components/ItemTag';

const ItemDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const theme = useTheme(); // ✅ Get theme
  const [item, setItem] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  
  // Edit dialog state
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [editName, setEditName] = useState('');
  const [editing, setEditing] = useState(false);
  const [editError, setEditError] = useState(null);

  // Delete dialog state
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [deleting, setDeleting] = useState(false);

  useEffect(() => {
    const fetchItem = async () => {
      try {
        setLoading(true);
        const data = await api.get(`/items/${id}`);
        setItem(data);
        setEditName(data.itemName);
      } catch (err) {
        setError(err.message || 'Failed to load item');
      } finally {
        setLoading(false);
      }
    };

    if (id) fetchItem();
  }, [id]);

  const handleEdit = async () => {
    setEditing(true);
    setEditError(null);
    
    try {
      const updated = await api.put(`/items/${id}`, { itemName: editName });
      setItem(updated);
      setEditDialogOpen(false);
      sessionStorage.removeItem('gallery_items');
    } catch (err) {
      setEditError(err.message || 'Failed to update item');
    } finally {
      setEditing(false);
    }
  };

  const handleDelete = async () => {
    setDeleting(true);
    try {
      await api.delete(`/items/${id}`);
      sessionStorage.removeItem('gallery_items');
      navigate('/', { replace: true });
    } catch (err) {
      setError(err.message || 'Failed to delete item');
      setDeleteDialogOpen(false);
    } finally {
      setDeleting(false);
    }
  };

  const handleLogWear = async () => {
    try {
      const updated = await api.post(`/items/${id}/wear`);
      setItem(updated);
      sessionStorage.removeItem('gallery_items');
    } catch (err) {
      console.error('Failed to log wear:', err);
    }
  };

  const groupedTags = item?.tags?.reduce((acc, tag) => {
    const category = tag.categoryName;
    if (!acc[category]) acc[category] = [];
    acc[category].push(tag.name);
    return acc;
  }, {});

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh', bgcolor: 'background.default' }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error || !item) {
    return (
      <Container sx={{ py: 4, textAlign: 'center' }}>
        <Typography variant="h5" gutterBottom color="error">
          {error || 'Item not found'}
        </Typography>
        <Button variant="contained" onClick={() => navigate('/')}>
          Go Home
        </Button>
      </Container>
    );
  }

  return (
    <Box sx={{ bgcolor: 'background.default', minHeight: '100vh' }}>
      <Container maxWidth="lg" sx={{ py: 5 }}>
        
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
          <Button
            startIcon={<ArrowBackIcon />}
            onClick={() => navigate(-1)}
            sx={{ 
              color: 'text.primary',
              '&:hover': { bgcolor: 'action.hover' }
            }}
          >
            Back
          </Button>
          
          <Box>
            <IconButton 
              onClick={() => navigate(`/items/${id}/edit`)}                
              sx={{ 
                mr: 1,
                color: 'text.secondary',
                '&:hover': { bgcolor: 'action.hover' }
              }}
            >
              <EditIcon />
            </IconButton>

            <IconButton 
              onClick={() => setDeleteDialogOpen(true)}
              sx={{ 
                color: 'error.main',
                '&:hover': { bgcolor: 'action.hover' }
              }}
            >
              <DeleteIcon />
            </IconButton>
          </Box>
        </Box>

        <Paper 
          elevation={0} 
          sx={{ 
            borderRadius: 3, 
            overflow: 'hidden', 
            bgcolor: 'background.paper',
            border: 1,
            borderColor: 'divider'
          }}
        >
          <Grid container>
            {/* Image Section */}
            <Grid item xs={12} md={6}>
              <Box sx={{ 
                p: 5, 
                bgcolor: 'grey.50', 
                minHeight: 500, 
                display: 'flex', 
                alignItems: 'center', 
                justifyContent: 'center' 
              }}>
                <Box
                  component="img"
                  src={item.imageUrl || 'https://via.placeholder.com/500x600?text=No+Image'}
                  alt={item.itemName}
                  sx={{
                    width: '100%',
                    maxWidth: 420,
                    height: 'auto',
                    objectFit: 'contain',
                    transition: 'transform 0.3s ease',
                    '&:hover': { transform: 'scale(1.03)' }
                  }}
                />
              </Box>
            </Grid>

            {/* Details Section */}
            <Grid item xs={12} md={6}>
              <Box sx={{ p: { xs: 3, md: 5 } }}>
                <Typography 
                  variant="body2" 
                  sx={{ 
                    color: 'text.secondary', 
                    mb: 1, 
                    textTransform: 'uppercase', 
                    fontSize: '12px', 
                    letterSpacing: '1px',
                    fontWeight: 600
                  }}
                >
                  {item.typeName}
                </Typography>

                <Typography 
                  variant="h3" 
                  sx={{ 
                    fontWeight: 700, 
                    mb: 4, 
                    letterSpacing: '-0.5px', 
                    color: 'text.primary' 
                  }}
                >
                  {item.itemName}
                </Typography>

                {groupedTags && Object.keys(groupedTags).length > 0 && (
                  <Box sx={{ mb: 4 }}>
                    {Object.entries(groupedTags).map(([category, tags]) => (
                      <Box key={category} sx={{ mb: 3 }}>
                        <Typography 
                          variant="subtitle2" 
                          sx={{ 
                            mb: 1.5, 
                            fontWeight: 600, 
                            color: 'text.secondary',
                            fontSize: '13px',
                            letterSpacing: '0.5px'
                          }}
                        >
                          {category}
                        </Typography>
                        <Stack direction="row" spacing={1} flexWrap="wrap" sx={{ gap: 1 }}>
                          {tags.map((tag, idx) => (
                            <ItemTag key={`${category}-${idx}`} label={tag} />
                          ))}
                        </Stack>
                      </Box>
                    ))}
                  </Box>
                )}

                {/* Stats */}
                <Box sx={{ mt: 5, pt: 2, borderTop: 1, borderColor: 'divider' }}>
                  <Typography variant="body2" sx={{ color: 'text.secondary', mb: 0.5 }}>
                    Worn <strong>{item.itemWc || 0}</strong> times
                  </Typography>
                  <Typography variant="body2" sx={{ color: 'text.secondary' }}>
                    Added {item.createdAt ? new Date(item.createdAt).toLocaleDateString() : 'Unknown'}
                  </Typography>
                </Box>

                <Button
                  variant="contained"
                  fullWidth
                  onClick={handleLogWear}
                  sx={{ mt: 4, py: 1.5 }}
                >
                  Wear This Item
                </Button>
              </Box>
            </Grid>
          </Grid>
        </Paper>
      </Container>

      {/* Edit Dialog */}
      <Dialog open={editDialogOpen} onClose={() => setEditDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Edit Item</DialogTitle>
        <DialogContent>
          {editError && (
            <Alert severity="error" sx={{ mb: 2 }} onClose={() => setEditError(null)}>
              {editError}
            </Alert>
          )}
          <TextField
            autoFocus
            margin="dense"
            label="Item Name"
            fullWidth
            value={editName}
            onChange={(e) => setEditName(e.target.value)}
            variant="outlined"
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setEditDialogOpen(false)}>Cancel</Button>
          <Button onClick={handleEdit} variant="contained" disabled={editing || !editName.trim()}>
            {editing ? <CircularProgress size={24} /> : 'Save'}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Delete Dialog */}
      <Dialog open={deleteDialogOpen} onClose={() => setDeleteDialogOpen(false)}>
        <DialogTitle>Delete Item</DialogTitle>
        <DialogContent>
          <Typography>
            Are you sure you want to delete "{item?.itemName}"? This action cannot be undone.
          </Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDeleteDialogOpen(false)}>Cancel</Button>
          <Button onClick={handleDelete} color="error" variant="contained" disabled={deleting}>
            {deleting ? <CircularProgress size={24} /> : 'Delete'}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default ItemDetails;