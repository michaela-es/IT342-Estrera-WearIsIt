export const ErrorCode = {
  CAT_001: 'CAT_001', // Category not found
  CAT_002: 'CAT_002', // Category name already exists
  CAT_003: 'CAT_003', // Category has tags, cannot delete
  UNAUTHORIZED: 'UNAUTHORIZED',
  FORBIDDEN: 'FORBIDDEN',
  NETWORK_ERROR: 'NETWORK_ERROR',
};

export const errorMessages = {
  [ErrorCode.CAT_001]: 'Category not found. It may have been deleted.',
  [ErrorCode.CAT_002]: 'A category with this name already exists.',
  [ErrorCode.CAT_003]: 'Cannot delete category with existing tags. Please delete all tags first.',
  [ErrorCode.UNAUTHORIZED]: 'Please log in to continue.',
  [ErrorCode.FORBIDDEN]: 'You don\'t have permission to perform this action.',
  [ErrorCode.NETWORK_ERROR]: 'Network error. Please check your connection.',
};

export const handleApiError = (error, dispatch) => {
  const message = errorMessages[error.code] || error.message || 'An unexpected error occurred';
  
  dispatch({
    type: 'SHOW_SNACKBAR',
    payload: { message, severity: 'error' },
  });
  
  return { error: true, message };
};