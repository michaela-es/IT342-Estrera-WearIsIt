import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { useState, useEffect } from 'react';
import ItemTag from '../components/small/ItemTag';
import {
  Container,
  Box,
  Typography,
  Button,
  Paper,
  Chip,
  Stack,
  Grid,
  CircularProgress
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import api from '../api/apiClient';

const ItemDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [item, setItem] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchItem = async () => {
      try {
        setLoading(true);
        const data = await api.get(`/items/${id}`);
        console.log('Fetched item:', data); // Debug
        setItem(data);
      } catch (err) {
        console.error('Error fetching item:', err);
        setError(err.response?.data?.error?.message || 'Failed to load item');
      } finally {
        setLoading(false);
      }
    };

    if (id) {
      fetchItem();
    }
  }, [id]);

  const groupedTags = item?.tags?.reduce((acc, tag) => {
    const category = tag.categoryName;
    if (!acc[category]) acc[category] = [];
    acc[category].push(tag.name);
    return acc;
  }, {});

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh' }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error || !item) {
    return (
      <Container sx={{ py: 4, textAlign: 'center' }}>
        <Typography variant="h5" gutterBottom>
          {error || 'Item not found'}
        </Typography>
        <Button variant="contained" onClick={() => navigate('/')}>
          Go Home
        </Button>
      </Container>
    );
  }

  return (
    <Box sx={{ backgroundColor: '#fafafa', minHeight: '100vh' }}>
      <Container maxWidth="lg" sx={{ py: 5 }}>
        
        <Button
          startIcon={<ArrowBackIcon />}
          onClick={() => navigate(-1)}
          sx={{ mb: 4, color: '#333', '&:hover': { backgroundColor: 'transparent' } }}
        >
          Back
        </Button>

        <Paper elevation={0} sx={{ borderRadius: 4, overflow: 'hidden', backgroundColor: '#fff' }}>
          <Grid container>

            <Grid item xs={12} md={6}>
              <Box sx={{ p: 5, backgroundColor: '#f5f5f5', minHeight: 500, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
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

            <Grid item xs={12} md={6}>
              <Box sx={{ p: { xs: 3, md: 5 } }}>

                <Typography variant="body2" sx={{ color: '#777', mb: 1, textTransform: 'uppercase', fontSize: '12px', letterSpacing: '1px' }}>
                  {item.typeName}
                </Typography>

                <Typography variant="h3" sx={{ fontWeight: 700, mb: 4, letterSpacing: '-0.5px', color: '#111' }}>
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
                            color: '#666',
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
                <Box sx={{ mt: 5, pt: 2, borderTop: '1px solid #eee' }}>
                  <Typography variant="body2" sx={{ color: '#777', mb: 0.5 }}>
                    Worn {item.itemWc || 0} times
                  </Typography>
                  <Typography variant="body2" sx={{ color: '#aaa' }}>
                    Added {item.createdAt ? new Date(item.createdAt).toLocaleDateString() : 'Unknown'}
                  </Typography>
                </Box>

                <Button
                  variant="contained"
                  fullWidth
                  sx={{
                    mt: 4,
                    py: 1.5,
                    borderRadius: 2,
                    backgroundColor: '#111',
                    '&:hover': { backgroundColor: '#333' }
                  }}
                >
                  Wear This Outfit
                </Button>

              </Box>
            </Grid>

          </Grid>
        </Paper>
      </Container>
    </Box>
  );
};

export default ItemDetails;