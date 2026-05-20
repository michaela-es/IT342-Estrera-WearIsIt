import apiClient from '@shared/utils/apiClient';

export const tagApi = {
  createTag: async (categoryId, name) => {
    return await apiClient.post('/tags', { categoryId, name });
  },

  updateTag: async (id, name) => {
    return await apiClient.put(`/tags/${id}`, { name });
  },

  deleteTag: async (id) => {
    return await apiClient.delete(`/tags/${id}`);
  },
};