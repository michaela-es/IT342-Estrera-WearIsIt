import React, { useEffect, useRef } from 'react';
import { Button } from '@mui/material';
import GoogleIcon from '@mui/icons-material/Google';

const GoogleSignInButton = () => {
  const buttonRef = useRef(null);
  const initialized = useRef(false);

  useEffect(() => {
    if (!initialized.current && window.google && buttonRef.current) {
      initialized.current = true;
      
      window.google.accounts.id.initialize({
        client_id: '562223260258-nggqu0cm6e7vrs8tumahvjqfrfldohns.apps.googleusercontent.com',
        callback: async (response) => {
          console.log("Google credential received");
          try {
            const res = await fetch('http://localhost:8080/api/auth/google', {
              method: 'POST',
              headers: { 'Content-Type': 'application/json' },
              body: JSON.stringify({ idToken: response.credential })
            });
            
            const data = await res.json();
            if (res.ok) {
              localStorage.setItem('accessToken', data.accessToken);
              localStorage.setItem('refreshToken', data.refreshToken);
              window.location.href = '/home';
            }
          } catch (error) {
            console.error('Login failed:', error);
          }
        },
        prompt: 'select_account'  // ← THIS forces account picker every time
      });
      
      window.google.accounts.id.renderButton(buttonRef.current, {
        type: 'standard',
        theme: 'outline',
        size: 'large',
        text: 'continue_with'
      });
    }
  }, []);

  return (
    <div ref={buttonRef} />
  );
};

export default GoogleSignInButton;