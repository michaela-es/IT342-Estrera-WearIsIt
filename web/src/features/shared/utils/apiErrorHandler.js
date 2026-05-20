export const ErrorCode = {
  CAT_001: 'CAT_001',
  CAT_002: 'CAT_002',
  CAT_003: 'CAT_003',
  UNAUTHORIZED: 'UNAUTHORIZED',
  FORBIDDEN: 'FORBIDDEN',
  NETWORK_ERROR: 'NETWORK_ERROR',
};

export const errorMessages = {
  [ErrorCode.CAT_001]: 'Category not found. It may have been deleted.',
  [ErrorCode.CAT_002]: 'A category with this name already exists.',
  [ErrorCode.CAT_003]: 'Cannot delete category with existing tags. Please delete all tags first.',
  [ErrorCode.UNAUTHORIZED]: 'Please log in to continue.',
  [ErrorCode.FORBIDDEN]: "You don't have permission to perform this action.",
  [ErrorCode.NETWORK_ERROR]: 'Network error. Please check your connection.',
};

export const handleApiError = (error, dispatch) => {
  const message = errorMessages[error.code] || error.message || 'An unexpected error occurred';
  
  if (error.status === 401 || error.code === ErrorCode.UNAUTHORIZED) {
    localStorage.removeItem('accessToken');
    dispatch({
      type: 'SHOW_SNACKBAR',
      payload: { message: 'Session expired. Please log in again.', severity: 'warning' },
    });
    return;
  }
  
  dispatch({
    type: 'SHOW_SNACKBAR',
    payload: { message, severity: 'error' },
  });
  
  return { error: true, message };
};