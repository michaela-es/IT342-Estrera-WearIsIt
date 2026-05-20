import apiClient from '@shared/utils/apiClient';

export const categoryApi = {
  getCategories: async () => {
    return await apiClient.get('/categories');
  },

  createCategory: async (name) => {
    return await apiClient.post('/categories', { name });
  },

  updateCategory: async (id, name) => {
    return await apiClient.put(`/categories/${id}`, { name });
  },

  deleteCategory: async (id) => {
    return await apiClient.delete(`/categories/${id}`);
  },

  getCategoryById: async (id) => {
    return await apiClient.get(`/categories/${id}`);
  },
};