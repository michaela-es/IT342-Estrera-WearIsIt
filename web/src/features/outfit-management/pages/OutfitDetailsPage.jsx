import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Container,
  Box,
  Typography,
  Paper,
  Grid,
  Button,
  Stack,
  Divider,
  CircularProgress,
  Alert,
  IconButton,
  Tooltip,
  Avatar
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline';
import EditIcon from '@mui/icons-material/Edit';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import CalendarMonthIcon from '@mui/icons-material/CalendarMonth';
import outfitApi from '../services/outfitApi';

const OutfitDetailsPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [outfit, setOutfit] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [actionLoading, setActionLoading] = useState(false);

  const fetchOutfitDetails = async () => {
    try {
      setLoading(true);
      const data = await outfitApi.getById(id);
      setOutfit(data);
    } catch (err) {
      setError('Failed to load outfit details');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOutfitDetails();
  }, [id]);

  const handleWear = async () => {
    try {
      setActionLoading(true);
      await outfitApi.wear(id);
      await fetchOutfitDetails();
    } catch (err) {
      setError('Failed to log wear');
    } finally {
      setActionLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!window.confirm('Are you sure you want to delete this outfit?')) return;
    try {
      setActionLoading(true);
      await outfitApi.delete(id);
      navigate('/outfits');
    } catch (err) {
      setError('Failed to delete outfit');
      setActionLoading(false);
    }
  };

  if (loading) return <Box sx={{ display: 'flex', justifyContent: 'center', p: 8 }}><CircularProgress /></Box>;
  if (!outfit) return <Container sx={{ py: 4 }}><Alert severity="error">Outfit not found</Alert></Container>;

  const groupedItems = outfit.items?.reduce((acc, item) => {
    const typeName = item.itemTypeName  || 'Other';
    
    if (!acc[typeName]) acc[typeName] = [];
    acc[typeName].push(item);
    return acc;
  }, {}) || {};

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Button 
        startIcon={<ArrowBackIcon />} 
        onClick={() => navigate('/outfits')}
        sx={{ mb: 3 }}
      >
        Back to Outfits
      </Button>

      {error && <Alert severity="error" sx={{ mb: 3 }} onClose={() => setError(null)}>{error}</Alert>}

      <Paper sx={{ p: 4, borderRadius: 4, overflow: 'hidden' }}>
        <Grid container spacing={4}>
          {/* Left Side: Image and Quick Stats */}
          <Grid item xs={12} md={5}>
            <Box 
              component="img"
              src={outfit.coverImageUrl || '/placeholder-outfit.jpg'}
              alt={outfit.outfitName}
              sx={{ 
                width: '100%', 
                height: 400, 
                objectFit: 'cover', 
                borderRadius: 3,
                boxShadow: '0 4px 20px rgba(0,0,0,0.1)',
                bgcolor: 'grey.100'
              }}
            />
            
            <Box sx={{ mt: 3, p: 2, bgcolor: 'grey.50', borderRadius: 2 }}>
              <Stack spacing={2}>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                  <CalendarMonthIcon color="action" />
                  <Box>
                    <Typography variant="caption" color="text.secondary">LAST WORN</Typography>
                    <Typography variant="body2" fontWeight={600}>
                      {outfit.lastWorn ? new Date(outfit.lastWorn).toLocaleDateString() : 'Never worn'}
                    </Typography>
                  </Box>
                </Box>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                  <CheckCircleOutlineIcon color="action" />
                  <Box>
                    <Typography variant="caption" color="text.secondary">TIMES WORN</Typography>
                    <Typography variant="body2" fontWeight={600}>{outfit.outfitWc || 0} times</Typography>
                  </Box>
                </Box>
              </Stack>
            </Box>
          </Grid>

          {/* Right Side: Details and Items */}
          <Grid item xs={12} md={7}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2 }}>
              <Typography variant="h3" fontWeight={700} sx={{ color: 'text.primary' }}>
                {outfit.outfitName}
              </Typography>
              <Stack direction="row" spacing={1}>
                <Tooltip title="Edit Outfit">
                  <IconButton onClick={() => navigate(`/outfits/${id}/edit`)} color="primary">
                    <EditIcon />
                  </IconButton>
                </Tooltip>
                <Tooltip title="Delete Outfit">
                  <IconButton onClick={handleDelete} color="error">
                    <DeleteOutlineIcon />
                  </IconButton>
                </Tooltip>
              </Stack>
            </Box>

            <Button 
              variant="contained" 
              fullWidth 
              size="large"
              startIcon={actionLoading ? <CircularProgress size={20} color="inherit" /> : <CheckCircleOutlineIcon />}
              onClick={handleWear}
              disabled={actionLoading}
              sx={{ py: 1.5, mb: 4, borderRadius: 2, fontSize: '1.1rem' }}
            >
              Wear This Outfit
            </Button>

            <Typography variant="h6" fontWeight={600} gutterBottom>Outfit Pieces</Typography>
            <Divider sx={{ mb: 3 }} />

            <Stack spacing={3}>
              {Object.entries(groupedItems).map(([groupName, items]) => (
                <Box key={groupName}>
                  <Typography 
                    variant="overline" 
                    fontWeight={700} 
                    color="text.secondary"
                    sx={{ letterSpacing: 1.5 }}
                  >
                    {groupName}
                  </Typography>
                  <Grid container spacing={2} sx={{ mt: 0.5 }}>
                    {items.map(item => (
                      <Grid item xs={12} sm={6} key={item.id || item.itemId}>
                        <Paper 
                          elevation={0}
                          sx={{ 
                            p: 1.5, 
                            display: 'flex', 
                            alignItems: 'center', 
                            gap: 2, 
                            bgcolor: 'background.default',
                            borderRadius: 2,
                            border: '1px solid',
                            borderColor: 'divider',
                            cursor: 'pointer',
                            '&:hover': { bgcolor: 'action.hover' }
                          }}
                          onClick={() => navigate(`/items/${item.itemId || item.id}`)}
                        >
                          <Avatar 
                            src={item.imageUrl} 
                            variant="rounded"
                            sx={{ width: 50, height: 50 }}
                          >
                            {item.itemName?.[0]}
                          </Avatar>
                          <Box sx={{ overflow: 'hidden' }}>
                            <Typography variant="body2" fontWeight={600} noWrap>
                              {item.itemName}
                            </Typography>
                          </Box>
                        </Paper>
                      </Grid>
                    ))}
                  </Grid>
                </Box>
              ))}
            </Stack>
          </Grid>
        </Grid>
      </Paper>
    </Container>
  );
};

export default OutfitDetailsPage;