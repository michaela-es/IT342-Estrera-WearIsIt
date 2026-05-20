// src/features/shared/components/Navbar.jsx
import React, { useState } from 'react';
import {
  IconButton,
  Drawer,
  List,
  ListItem,
  ListItemButton,
  ListItemText,
  Box,
  Divider
} from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '@features/auth/context/AuthContext';

const Navbar = () => {
  const [open, setOpen] = useState(false);
  const navigate = useNavigate();
  const { logout } = useAuth();

  const menuItems = [
    { text: 'Home', path: '/' },
    { text: 'Outfits', path: '/outfits' },
    { text: 'Profile', path: '/profile' },
    { text: 'Upload Clothing', path: '/upload' },
    { text: 'Categories & Tags', path: '/categories-tags' }, // ✅ Added here
    { text: 'Logout', path: null, action: 'logout' }
  ];

  const handleNavigation = (item) => {
    setOpen(false);
    
    if (item.action === 'logout') {
      logout();
      navigate('/login');
    } else if (item.path) {
      navigate(item.path);
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
          {menuItems.map((item, index) => (
            <React.Fragment key={item.text}>
              <ListItem disablePadding>
                <ListItemButton onClick={() => handleNavigation(item)}>
                  <ListItemText primary={item.text} />
                </ListItemButton>
              </ListItem>
              {item.text === 'Profile' && <Divider />}
            </React.Fragment>
          ))}
        </List>
      </Drawer>
    </>
  );
};

export default Navbar;