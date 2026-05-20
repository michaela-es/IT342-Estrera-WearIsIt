import api from '@features/shared/api/apiClient';

export const outfitApi = {
  getAll: () => api.get('/outfits'),
  getById: (id) => api.get(`/outfits/${id}`),
  create: (data) => api.post('/outfits/new', data),
  update: (id, data) => api.put(`/outfits/${id}`, data),
  delete: (id) => api.delete(`/outfits/${id}`),
  wear: (id) => api.post(`/outfits/${id}/wear`),
  uploadImage: (id, file) => {
    const formData = new FormData();
    formData.append('image', file);
    return api.post(`/outfits/${id}/image`, formData);
  },
  deleteImage: (id) => api.delete(`/outfits/${id}/image`),
  validate: (items) => api.post('/outfits/validate', items),
};

export default outfitApi;