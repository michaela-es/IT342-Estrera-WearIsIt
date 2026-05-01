import React, { useState } from 'react';
import {
  IconButton,
  Drawer,
  List,
  ListItem,
  ListItemText,
  Box,
  Divider
} from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../auth/context/AuthContext';

const Navbar = () => {
  const [open, setOpen] = useState(false);
  const navigate = useNavigate();
  const { logout } = useAuth();

  const handleNavigation = (text) => {
    setOpen(false);
    
    switch(text) {
      case 'Home':
        navigate('/');
        break;
      case 'Profile':
        navigate('/profile');
        break;
      case 'Upload Clothing':
        navigate('/upload');
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
        color="default"
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
          {['Home', 'Profile', 'Upload Clothing', 'Logout'].map((text, index) => (
            <React.Fragment key={text}>
              <ListItem 
                button 
                onClick={() => handleNavigation(text)}
              >
                <ListItemText primary={text} />
              </ListItem>
              {text === 'Profile' && <Divider />}
            </React.Fragment>
          ))}
        </List>
      </Drawer>
    </>
  );
};

export default Navbar;