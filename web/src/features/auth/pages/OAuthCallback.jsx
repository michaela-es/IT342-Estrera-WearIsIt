import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Box, CircularProgress, Typography } from '@mui/material';

const OAuthCallback = () => {
  const navigate = useNavigate();

  useEffect(() => {
    // Get tokens from URL hash
    const hash = window.location.hash.substring(1);
    const params = new URLSearchParams(hash);
    const accessToken = params.get('accessToken');
    const refreshToken = params.get('refreshToken');

    console.log("OAuth Callback - Hash:", hash);
    console.log("Access Token:", accessToken ? "✅ Received" : "❌ Missing");
    console.log("Refresh Token:", refreshToken ? "✅ Received" : "❌ Missing");

    if (accessToken && refreshToken) {
      // Save tokens
      localStorage.setItem('accessToken', accessToken);
      localStorage.setItem('refreshToken', refreshToken);
      
      // Fetch user data
      fetch('http://localhost:8080/api/user/me', {
        headers: { 'Authorization': `Bearer ${accessToken}` }
      })
        .then(res => res.json())
        .then(user => {
          localStorage.setItem('user', JSON.stringify(user));
          navigate('/home', { replace: true });
        })
        .catch(err => {
          console.error("Failed to fetch user:", err);
        });
    } else {
      console.error("No tokens found in URL");
      navigate('/login?error=no_tokens', { replace: true });
    }
  }, [navigate]);

  return (
    <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
      <CircularProgress />
      <Typography sx={{ ml: 2 }}>Signing you in...</Typography>
    </Box>
  );
};

export default OAuthCallback;