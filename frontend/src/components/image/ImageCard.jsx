import React from 'react';
import {
  Card,
  CardMedia,
  CardContent,
  Typography,
  Chip,
  Stack,
  Box,
  Button
} from '@mui/material';
import useTags from '../../hooks/useTags';

const ImageCard = ({ image }) => {
  const { visibleTags, hasMore, hiddenCount, toggleTags } = useTags(image.tags, 5);

  return (
    <Card 
      sx={{ 
        borderRadius: 2,
        overflow: 'hidden',
        transition: 'transform 0.3s ease-in-out, box-shadow 0.3s ease-in-out',
        '&:hover': {
          transform: 'translateY(-8px)',
          boxShadow: '0 20px 25px -12px rgba(0,0,0,0.2)',
          cursor: 'pointer'
        },
        height: '100%',
        display: 'flex',
        flexDirection: 'column'
      }}
    >
      <Box sx={{ position: 'relative', paddingTop: '75%' }}>
        <CardMedia
          component="img"
          image={image.url}
          alt={image.name}
          sx={{
            position: 'absolute',
            top: 0,
            left: 0,
            width: '100%',
            height: '100%',
            objectFit: 'cover',
            transition: 'transform 0.5s ease-in-out',
            '&:hover': {
              transform: 'scale(1.05)'
            }
          }}
        />
      </Box>

      <CardContent sx={{ flexGrow: 1, p: 2.5 }}>
        <Typography 
          variant="h6" 
          gutterBottom 
          sx={{ 
            fontWeight: 700,
            fontSize: '1.1rem',
            mb: 1.5
          }}
        >
          {image.name}
        </Typography>

        <Stack direction="row" flexWrap="wrap" sx={{ gap: 0.75, alignItems: 'center' }}>
          {visibleTags.map((tag, index) => (
            <Chip 
              key={index} 
              label={tag} 
              size="small"
              sx={{
                fontSize: '0.7rem',
                fontWeight: 500,
                backgroundColor: '#f0f2f5'
              }}
            />
          ))}
          
          {hasMore && (
            <Button
              size="small"
              onClick={toggleTags}
              sx={{
                fontSize: '0.7rem',
                minWidth: 'auto',
                textTransform: 'none',
                color: '#666',
                '&:hover': {
                  backgroundColor: '#f0f2f5'
                }
              }}
            >
              {showAll ? 'Show less' : `+${hiddenCount} more`}
            </Button>
          )}
        </Stack>
      </CardContent>
    </Card>
  );
};

export default ImageCard;