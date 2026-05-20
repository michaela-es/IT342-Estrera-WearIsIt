import { categoryActions } from './categoryActions';

export const initialState = {
  categories: [],
  selectedCategoryId: null,
  loading: false,
  snackbar: { open: false, message: '', severity: 'success' },
};

export const categoryReducer = (state, action) => {
  switch (action.type) {
    case categoryActions.SET_CATEGORIES:
      return { ...state, categories: action.payload, loading: false };

    case categoryActions.ADD_CATEGORY:
      return { ...state, categories: [...state.categories, action.payload] };

    case categoryActions.UPDATE_CATEGORY:
      return {
        ...state,
        categories: state.categories.map((cat) =>
          cat.id === action.payload.id ? { ...cat, name: action.payload.name } : cat
        ),
      };

    case categoryActions.DELETE_CATEGORY:
      return {
        ...state,
        categories: state.categories.filter((cat) => cat.id !== action.payload),
        selectedCategoryId:
          state.selectedCategoryId === action.payload ? null : state.selectedCategoryId,
      };

    case categoryActions.ADD_TAG:
      return {
        ...state,
        categories: state.categories.map((cat) =>
          cat.id === action.payload.categoryId
            ? { ...cat, tags: [...cat.tags, action.payload] }
            : cat
        ),
      };

    case categoryActions.UPDATE_TAG:
      return {
        ...state,
        categories: state.categories.map((cat) =>
          cat.id === action.payload.categoryId
            ? {
                ...cat,
                tags: cat.tags.map((tag) =>
                  tag.id === action.payload.id ? { ...tag, name: action.payload.name } : tag
                ),
              }
            : cat
        ),
      };

    case categoryActions.DELETE_TAG:
      return {
        ...state,
        categories: state.categories.map((cat) =>
          cat.id === action.payload.categoryId
            ? { ...cat, tags: cat.tags.filter((tag) => tag.id !== action.payload.tagId) }
            : cat
        ),
      };

    case categoryActions.SET_SELECTED_CATEGORY:
      return { ...state, selectedCategoryId: action.payload };

    case categoryActions.SET_LOADING:
      return { ...state, loading: true };

    case categoryActions.SHOW_SNACKBAR:
      return {
        ...state,
        snackbar: { open: true, message: action.payload.message, severity: action.payload.severity },
      };

    case categoryActions.HIDE_SNACKBAR:
      return { ...state, snackbar: { ...state.snackbar, open: false } };

    default:
      return state;
  }
};