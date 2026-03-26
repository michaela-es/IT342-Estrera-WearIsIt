import axios from 'axios';

export const getImages = async () => {
  try {
    const res = await axios.get('/mock_data/mockImages.json');
    return Array.isArray(res.data) ? res.data : []; 
  } catch (err) {
    console.error('Error fetching images:', err);
    return [];
  }
};