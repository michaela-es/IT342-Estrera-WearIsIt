import React, { useState } from 'react';
import {
  TextField,
  Box,
  InputAdornment,
  IconButton
} from '@mui/material';
import { Visibility, VisibilityOff } from '@mui/icons-material';

const RegisterForm = ({ formData, handleChange, loading }) => {
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);
  const [touched, setTouched] = useState({});

  const handleBlur = (e) => {
    setTouched({ ...touched, [e.target.name]: true });
  };

  // Validation
  const nameError = touched.name && !formData.name;

  const emailError =
    touched.email &&
    (!formData.email || !/\S+@\S+\.\S+/.test(formData.email));

  const passwordError =
    touched.password && formData.password.length < 6;

  const confirmPasswordError =
    touched.confirmPassword &&
    formData.confirmPassword !== formData.password;

  return (
    <Box>
      {/* Username */}
      <TextField
        fullWidth
        label="Username"
        name="name"
        value={formData.name}
        onChange={handleChange}
        onBlur={handleBlur}
        margin="normal"
        disabled={loading}
        required
        error={!!nameError}
        helperText={nameError ? 'Username is required' : ''}
      />

      {/* Email */}
      <TextField
        fullWidth
        label="Email Address"
        name="email"
        type="email"
        value={formData.email}
        onChange={handleChange}
        onBlur={handleBlur}
        margin="normal"
        disabled={loading}
        required
        error={!!emailError}
        helperText={
          emailError ? 'Enter a valid email address' : ''
        }
      />

      {/* Password */}
      <TextField
        fullWidth
        label="Password"
        name="password"
        type={showPassword ? 'text' : 'password'}
        value={formData.password}
        onChange={handleChange}
        onBlur={handleBlur}
        margin="normal"
        disabled={loading}
        required
        error={!!passwordError}
        helperText={
          passwordError ? 'Minimum 6 characters' : ''
        }
        InputProps={{
          endAdornment: (
            <InputAdornment position="end">
              <IconButton
                onClick={() => setShowPassword(!showPassword)}
                edge="end"
                disabled={loading}
              >
                {showPassword ? <VisibilityOff /> : <Visibility />}
              </IconButton>
            </InputAdornment>
          )
        }}
      />

      {/* Confirm Password */}
      <TextField
        fullWidth
        label="Confirm Password"
        name="confirmPassword"
        type={showConfirm ? 'text' : 'password'}
        value={formData.confirmPassword}
        onChange={handleChange}
        onBlur={handleBlur}
        margin="normal"
        disabled={loading}
        required
        error={!!confirmPasswordError}
        helperText={
          confirmPasswordError ? 'Passwords do not match' : ''
        }
        InputProps={{
          endAdornment: (
            <InputAdornment position="end">
              <IconButton
                onClick={() => setShowConfirm(!showConfirm)}
                edge="end"
                disabled={loading}
              >
                {showConfirm ? <VisibilityOff /> : <Visibility />}
              </IconButton>
            </InputAdornment>
          )
        }}
      />
    </Box>
  );
};

export default RegisterForm;