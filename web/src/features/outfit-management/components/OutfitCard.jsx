import React from 'react';
import { 
  Card, 
  CardContent, 
  CardMedia, 
  Typography, 
  Box, 
  Stack, 
  Avatar,
  IconButton,
  Tooltip
} from '@mui/material';
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline';
import EditIcon from '@mui/icons-material/Edit';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';

import { useNavigate } from 'react-router-dom';

const OutfitCard = ({ outfit, onWear, onEdit, onDelete }) => {
  const navigate = useNavigate();

  return (
    <Card 
      onClick={() => navigate(`/outfits/${outfit.id}`)}
      sx={{ 
        width: '100%', 
        borderRadius: 3, 
        overflow: 'hidden',
        cursor: 'pointer',
        transition: 'transform 0.2s, box-shadow 0.2s',
        '&:hover': {
          transform: 'translateY(-4px)',
          boxShadow: '0 8px 24px rgba(0,0,0,0.12)'
        }
      }}
    >
      <Box sx={{ position: 'relative' }}>
        <CardMedia
          component="img"
          height="160"
          image={outfit.coverImageUrl || '/placeholder-outfit.jpg'}
          alt={outfit.outfitName}
          sx={{ bgcolor: 'grey.100' }}
        />
        <Box 
          onClick={(e) => e.stopPropagation()}
          sx={{ 
            position: 'absolute', 
            top: 8, 
            right: 8, 
            display: 'flex', 
            gap: 0.5,
            bgcolor: 'rgba(255,255,255,0.8)',
            borderRadius: 2,
            p: 0.5
          }}
        >
          <Tooltip title="Wear Today">
            <IconButton size="small" onClick={() => onWear(outfit.id)} color="primary">
              <CheckCircleOutlineIcon fontSize="small" />
            </IconButton>
          </Tooltip>
          <Tooltip title="Edit">
            <IconButton size="small" onClick={() => onEdit(outfit)} color="inherit">
              <EditIcon fontSize="small" />
            </IconButton>
          </Tooltip>
          <Tooltip title="Delete">
            <IconButton size="small" onClick={() => onDelete(outfit.id)} color="error">
              <DeleteOutlineIcon fontSize="small" />
            </IconButton>
          </Tooltip>
        </Box>
      </Box>

      <CardContent sx={{ p: 2 }}>
        <Typography variant="h6" sx={{ fontWeight: 600, mb: 0.5, fontSize: '1rem' }} noWrap>
          {outfit.outfitName}
        </Typography>
        
        <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mb: 1.5 }}>
          Worn {outfit.outfitWc || 0} times • {outfit.items?.length || 0} items
        </Typography>

        <Stack direction="row" spacing={-1} sx={{ mt: 1 }}>
          {outfit.items?.slice(0, 4).map((item, idx) => (
            <Tooltip key={idx} title={item.itemName}>
              <Avatar 
                src={item.imageUrl} 
                sx={{ 
                  width: 32, 
                  height: 32, 
                  border: '2px solid white',
                  bgcolor: 'grey.200'
                }}
              >
                {item.itemName?.[0]}
              </Avatar>
            </Tooltip>
          ))}
          {outfit.items?.length > 4 && (
            <Avatar sx={{ 
              width: 32, 
              height: 32, 
              fontSize: '0.75rem', 
              border: '2px solid white',
              bgcolor: 'primary.main',
              color: 'white'
            }}>
              +{outfit.items.length - 4}
            </Avatar>
          )}
        </Stack>
      </CardContent>
    </Card>
  );
};

export default OutfitCard;