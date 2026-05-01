import React from 'react';
import { useNavigate } from 'react-router-dom';
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
import { useTheme } from '@mui/material/styles';
import useTags from '../../../hooks/useTags';

const ImageCard = ({ image }) => {
  const navigate = useNavigate();
  const theme = useTheme();
  
  const tagNames = image.tags?.map(tag => tag.name) || [];
  const { visibleTags, hasMore, hiddenCount, toggleTags, showAll } = useTags(tagNames, 5);
  
  const handleClick = () => {
    navigate(`/items/${image.id}`);
  };
  
  return (
    <Card 
      onClick={handleClick}
      sx={{ 
        borderRadius: 2,
        overflow: 'hidden',
        transition: 'transform 0.3s ease-in-out, box-shadow 0.3s ease-in-out',
        '&:hover': {
          transform: 'translateY(-8px)',
          boxShadow: theme.shadows[10],
          cursor: 'pointer'
        },
        height: '100%',
        display: 'flex',
        flexDirection: 'column'
      }}
    >
      <Box sx={{ position: 'relative', paddingTop: '100%' }}>
        <CardMedia
          component="img"
          image={image.imageUrl || 'https://via.placeholder.com/300x300?text=No+Image'}
          alt={image.itemName}
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
            mb: 1.5,
            color: theme.palette.text.primary
          }}
        >
          {image.itemName}
        </Typography>

        <Stack direction="row" flexWrap="wrap" sx={{ gap: 0.75, alignItems: 'center' }}>
          {visibleTags.map((tag, index) => (
            <Chip 
              key={index} 
              label={tag} 
              size="small"
            />
          ))}
          
          {hasMore && (
            <Button
              size="small"
              onClick={(e) => {
                e.stopPropagation();
                toggleTags();
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