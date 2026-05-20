import { useCategoryContext } from '../store/categoryContext';
import { categoryActions } from '../store/categoryActions';
import { tagApi } from '../services/tagApi';
import { handleApiError } from '@shared/utils/apiErrorHandler';

export const useTags = () => {
  const { dispatch } = useCategoryContext();

  const createTag = async (categoryId, name) => {
    try {
      const newTag = await tagApi.createTag(categoryId, name);
      dispatch({
        type: categoryActions.ADD_TAG,
        payload: { id: newTag.id, name: newTag.name, categoryId },
      });
      dispatch({
        type: categoryActions.SHOW_SNACKBAR,
        payload: { message: 'Tag added!', severity: 'success' },
      });
      return true;
    } catch (error) {
      handleApiError(error, dispatch);
      return false;
    }
  };

  const updateTag = async (tagId, name, categoryId) => {
    try {
      await tagApi.updateTag(tagId, name);
      dispatch({
        type: categoryActions.UPDATE_TAG,
        payload: { id: tagId, name, categoryId },
      });
      dispatch({
        type: categoryActions.SHOW_SNACKBAR,
        payload: { message: 'Tag updated!', severity: 'success' },
      });
      return true;
    } catch (error) {
      handleApiError(error, dispatch);
      return false;
    }
  };

  const deleteTag = async (tagId, categoryId) => {
    try {
      await tagApi.deleteTag(tagId);
      dispatch({
        type: categoryActions.DELETE_TAG,
        payload: { tagId, categoryId },
      });
      dispatch({
        type: categoryActions.SHOW_SNACKBAR,
        payload: { message: 'Tag deleted!', severity: 'success' },
      });
      return true;
    } catch (error) {
      handleApiError(error, dispatch);
      return false;
    }
  };

  return { createTag, updateTag, deleteTag };
};