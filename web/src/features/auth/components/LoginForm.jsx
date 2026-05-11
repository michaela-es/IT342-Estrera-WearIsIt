import React, { useState } from 'react';
import {
  TextField,
  Box,
  InputAdornment,
  IconButton
} from '@mui/material';
import { Visibility, VisibilityOff } from '@mui/icons-material';

const LoginForm = ({ formData, handleChange, loading }) => {
  const [showPassword, setShowPassword] = useState(false);
  const [touched, setTouched] = useState({});

  const handleBlur = (e) => {
    setTouched({ ...touched, [e.target.name]: true });
  };

  const emailOrUsernameError =
    touched.emailOrUsername && !formData.emailOrUsername;

  const passwordError =
    touched.password && formData.password.length < 6;

  return (
    <Box>
      {/* Email / Username */}
      <TextField
        fullWidth
        label="Username or Email"
        name="emailOrUsername"
        value={formData.emailOrUsername}
        onChange={handleChange}
        onBlur={handleBlur}
        margin="normal"
        disabled={loading}
        required
        error={!!emailOrUsernameError}
        helperText={
          emailOrUsernameError ? 'This field is required' : ''
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
          passwordError ? 'Password must be at least 6 characters' : ''
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
    </Box>
  );
};

export default LoginForm;