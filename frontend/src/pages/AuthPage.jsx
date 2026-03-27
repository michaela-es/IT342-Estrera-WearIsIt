import React, { useState } from 'react';
import {
  Box,
  Paper,
  Typography,
  Button,
  Divider
} from '@mui/material';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import LoginForm from '../components/LoginForm';
import RegisterForm from '../components/RegisterForm';
import MessageBox from '../components/MessageBox';

const AuthPage = () => {
  const [isLogin, setIsLogin] = useState(true);
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    emailOrUsername: '',
    password: '',
    confirmPassword: ''
  });

  const { login, register, loading, error, clearError } = useAuth();
  const [success, setSuccess] = useState(null);
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    if (error) clearError();
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (isLogin) {
        const data = await login(formData.emailOrUsername, formData.password);
        if (data) {
          setSuccess("Welcome back!");
          navigate('/');
        }
      } else {
        if (formData.password !== formData.confirmPassword) return;
        const data = await register(formData);
        if (data.success) {
          setSuccess("Account created successfully! You can now sign in.");
        }
      }
    } catch (err) {}
  };

  const messageText = error || success;
  const messageType = error ? 'error' : success ? 'success' : null;

  return (
    <Box
      sx={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        bgcolor: '#f3f4f6',
        px: 2
      }}
    >
      <Paper
        elevation={6}
        sx={{
          p: 4,
          width: '100%',
          maxWidth: 400,
          borderRadius: 3
        }}
      >
        {/* Title */}
        <Box textAlign="center" mb={3}>
          <Typography variant="h5" fontWeight={600}>
            {isLogin ? 'Sign In' : 'Create Account'}
          </Typography>
        </Box>

        {/* Message */}
        <MessageBox
          type={messageType}
          text={messageText}
          onClose={() => {
            if (error) clearError();
            if (success) setSuccess(null);
          }}
        />

        {/* Form */}
        <Box component="form" onSubmit={handleSubmit}>
          {isLogin ? (
            <LoginForm formData={formData} handleChange={handleChange} loading={loading} />
          ) : (
            <RegisterForm formData={formData} handleChange={handleChange} loading={loading} />
          )}

          <Button
            type="submit"
            fullWidth
            variant="contained"
            disabled={loading}
            sx={{
              mt: 2,
              py: 1.5,
              fontWeight: 600,
              background: 'linear-gradient(135deg, #486881, #19135f)',
              '&:hover': {
                background: 'linear-gradient(135deg, #19135f, #486881)'
              }
            }}
          >
            {loading ? 'Please wait...' : isLogin ? 'Sign In' : 'Sign Up'}
          </Button>
        </Box>

        {/* Toggle */}
        <Box textAlign="center" mt={3}>
          <Divider sx={{ mb: 2 }} />
          <Typography variant="body2" color="text.secondary">
            {isLogin ? "Don't have an account?" : "Already have an account?"}
          </Typography>

          <Button
            variant="text"
            onClick={() => {
              setIsLogin(!isLogin);
              clearError();
            }}
            disabled={loading}
            sx={{ textTransform: 'none', fontWeight: 600 }}
          >
            {isLogin ? 'Sign up' : 'Sign in'}
          </Button>
        </Box>
      </Paper>
    </Box>
  );
};

export default AuthPage;