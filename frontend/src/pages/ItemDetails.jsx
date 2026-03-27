import { useLocation, useNavigate } from 'react-router-dom';
import { Container, Box, Typography, Button, Paper, Chip, Stack, Grid } from '@mui/material';
import { useTheme } from '@mui/material/styles';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';

const ItemDetails = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const theme = useTheme();
  const image = location.state?.image; 

  if (!image) {
    return (
      <Container sx={{ py: 4, textAlign: 'center' }}>
        <Typography variant="h5" gutterBottom>
          Item not found
        </Typography>
        <Button variant="contained" onClick={() => navigate('/')}>
          Go Home
        </Button>
      </Container>
    );
  }

  return (
    <Container maxWidth="xl" sx={{ py: 4 }}>
      <Button 
        startIcon={<ArrowBackIcon />} 
        onClick={() => navigate(-1)}
        sx={{ mb: 3 }}
      >
        Back
      </Button>

      <Paper sx={{ p: 4 }}>
        <Grid container spacing={4}>
          {/* Image on left */}
          <Grid item xs={12} md={6}>
            <Box
              component="img"
              src={image.url}
              alt={image.name}
              sx={{
                width: '100%',
                height: 'auto',
                maxHeight: '400px',
                objectFit: 'cover',
                borderRadius: '8px'
              }}
            />
          </Grid>
          
          {/* Details on right */}
          <Grid item xs={12} md={6}>
            <Typography 
              variant="h4" 
              sx={{ 
                mb: 2, 
                fontWeight: 600,
                color: theme.palette.text.primary
              }}
            >
              {image.name}
            </Typography>
            
            {image.description && (
              <Typography 
                variant="body1" 
                sx={{ 
                  mb: 3,
                  color: theme.palette.text.secondary
                }}
              >
                {image.description}
              </Typography>
            )}
            
            {image.createdAt && (
              <Typography 
                variant="caption" 
                display="block" 
                sx={{ 
                  mb: 2, 
                  color: theme.palette.text.secondary
                }}
              >
                Uploaded: {image.createdAt}
              </Typography>
            )}
            
            {image.tags && (
              <Stack direction="row" spacing={1} sx={{ mt: 2, flexWrap: 'wrap', gap: 1 }}>
                {image.tags.map((tag, index) => (
                  <Chip 
                    key={index} 
                    label={tag}
                  />
                ))}
              </Stack>
            )}
          </Grid>
        </Grid>
      </Paper>
    </Container>
  );
};

export default ItemDetails;