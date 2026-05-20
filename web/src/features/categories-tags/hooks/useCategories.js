import { useEffect } from 'react';
import { useCategoryContext } from '../store/categoryContext';
import { categoryActions } from '../store/categoryActions';
import { categoryApi } from '../services/categoryApi';
import { tagApi } from '../services/tagApi';
import { handleApiError } from '@shared/utils/apiErrorHandler';

export const useCategories = () => {
  const { state, dispatch } = useCategoryContext();

  const loadCategories = async () => {
    dispatch({ type: categoryActions.SET_LOADING });
    try {
      const categories = await categoryApi.getCategories();
      
      const categoriesWithTags = await Promise.all(
        categories.map(async (category) => {
          try {
            const categoryDetail = await categoryApi.getCategoryById(category.id);
            return {
              id: category.id,
              name: category.name,
              tags: categoryDetail.tags || [],
            };
          } catch (error) {
            return {
              id: category.id,
              name: category.name,
              tags: [],
            };
          }
        })
      );
      
      dispatch({ type: categoryActions.SET_CATEGORIES, payload: categoriesWithTags });
    } catch (error) {
      handleApiError(error, dispatch);
      dispatch({ type: categoryActions.SET_CATEGORIES, payload: [] });
    }
  };

  const createCategory = async (name) => {
    try {
      const newCategory = await categoryApi.createCategory(name);
      dispatch({
        type: categoryActions.ADD_CATEGORY,
        payload: { id: newCategory.id, name: newCategory.name, tags: [] },
      });
      dispatch({
        type: categoryActions.SHOW_SNACKBAR,
        payload: { message: 'Category created!', severity: 'success' },
      });
      return true;
    } catch (error) {
      handleApiError(error, dispatch);
      return false;
    }
  };

  const updateCategory = async (id, name) => {
    try {
      await categoryApi.updateCategory(id, name);
      dispatch({ type: categoryActions.UPDATE_CATEGORY, payload: { id, name } });
      dispatch({
        type: categoryActions.SHOW_SNACKBAR,
        payload: { message: 'Category updated!', severity: 'success' },
      });
      return true;
    } catch (error) {
      handleApiError(error, dispatch);
      return false;
    }
  };

  const deleteCategory = async (id) => {
    try {
      await categoryApi.deleteCategory(id);
      dispatch({ type: categoryActions.DELETE_CATEGORY, payload: id });
      dispatch({
        type: categoryActions.SHOW_SNACKBAR,
        payload: { message: 'Category deleted!', severity: 'success' },
      });
      return true;
    } catch (error) {
      if (error.code !== 'CAT_003') {
        handleApiError(error, dispatch);
      }
      return false;
    }
  };

  const selectCategory = (id) => {
    dispatch({ type: categoryActions.SET_SELECTED_CATEGORY, payload: id });
  };

  useEffect(() => {
    loadCategories();
  }, []);

  return {
    categories: state.categories,
    selectedCategoryId: state.selectedCategoryId,
    selectedCategory: state.categories.find((c) => c.id === state.selectedCategoryId),
    loading: state.loading,
    snackbar: state.snackbar,
    createCategory,
    updateCategory,
    deleteCategory,
    selectCategory,
    dispatch,
  };
};