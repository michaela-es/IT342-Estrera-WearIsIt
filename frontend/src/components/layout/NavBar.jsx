import React, { useState } from 'react';
import {
  IconButton,
  Drawer,
  List,
  ListItem,
  ListItemText,
  Box
} from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const Navbar = () => {
  const [open, setOpen] = useState(false);
  const navigate = useNavigate();
  const { logout } = useAuth();

  const handleNavigation = (text) => {
    setOpen(false);
    
    switch(text) {
      case 'Home':
        navigate('/home');
        break;
      case 'Profile':
        navigate('/profile');
        break;
      case 'Logout':
        logout();
        navigate('/login');
        break;
      default:
        break;
    }
  };

  return (
    <>
      <IconButton
        color="dark"
        onClick={() => setOpen(true)}
        sx={{ 
          p: 1,
          '&:hover': {
            backgroundColor: 'rgba(0, 0, 0, 0.04)'
          }
        }}
      >
        <MenuIcon />
      </IconButton>

      <Drawer open={open} onClose={() => setOpen(false)}>
        <List sx={{ width: 250 }}>
          {['Home', 'Profile', 'Logout'].map((text) => (
            <ListItem 
              button 
              key={text} 
              onClick={() => handleNavigation(text)}
            >
              <ListItemText primary={text} />
            </ListItem>
          ))}
        </List>
      </Drawer>
    </>
  );
};

export default Navbar;