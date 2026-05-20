import { useState, useEffect } from 'react';
import outfitApi from '../services/outfitApi';
import api from '@features/shared/api/apiClient';

export const useOutfits = () => {
  const [outfits, setOutfits] = useState([]);
  const [availableItems, setAvailableItems] = useState([]);
  const [itemTypes, setItemTypes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchOutfits = async () => {
    try {
      setLoading(true);
      const data = await outfitApi.getAll();
      setOutfits(data || []);
    } catch (err) {
      setError(err.message || 'Failed to fetch outfits');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const fetchItemTypes = async () => {
    try {
      const data = await api.get('/item-types');
      // Support Spring Data Page or direct array
      const types = Array.isArray(data) ? data : (data.content || []);
      setItemTypes(types);
    } catch (err) {
      console.error('Failed to fetch item types', err);
    }
  };

  const fetchAvailableItems = async () => {
    try {
      const data = await api.get('/items');
      
      // Safety check: handle Spring Data Page (data.content), 
      // direct arrays, or data.items
      let itemsArray = [];
      if (Array.isArray(data)) {
        itemsArray = data;
      } else if (data && Array.isArray(data.content)) {
        itemsArray = data.content;
      } else if (data && Array.isArray(data.items)) {
        itemsArray = data.items;
      }
      
      const formatted = itemsArray.map(item => ({
        id: item.id,
        itemName: item.itemName,
        imageUrl: item.imageUrl,
        // Check both flattened and nested structures to ensure we get the type name
        typeName: item.typeName || item.type?.name || 'No Type'
      }));
      setAvailableItems(formatted);
    } catch (err) {
      console.error('Failed to fetch clothing items', err);
    }
  };

  useEffect(() => {
    fetchOutfits();
    fetchAvailableItems();
    fetchItemTypes();
  }, []);

  const handleWearOutfit = async (id) => {
    try {
      await outfitApi.wear(id);
      await fetchOutfits(); // Refresh to update wear count
    } catch (err) {
      setError(err.message || 'Failed to log outfit wear');
    }
  };

  const handleDeleteOutfit = async (id) => {
    if (!window.confirm('Are you sure you want to delete this outfit?')) return;
    try {
      await outfitApi.delete(id);
      setOutfits(prev => prev.filter(o => o.id !== id));
    } catch (err) {
      setError(err.message || 'Failed to delete outfit');
    }
  };

  return {
    outfits,
    availableItems,
    itemTypes,
    loading,
    error,
    fetchOutfits,
    handleWearOutfit,
    handleDeleteOutfit
  };
};