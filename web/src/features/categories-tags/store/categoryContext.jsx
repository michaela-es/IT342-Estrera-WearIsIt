import React, { createContext, useContext, useReducer } from 'react';
import { categoryReducer, initialState } from './categoryReducer';

const CategoryContext = createContext();

export const CategoryProvider = ({ children }) => {
  const [state, dispatch] = useReducer(categoryReducer, initialState);

  return <CategoryContext.Provider value={{ state, dispatch }}>{children}</CategoryContext.Provider>;
};

export const useCategoryContext = () => {
  const context = useContext(CategoryContext);
  if (!context) {
    throw new Error('useCategoryContext must be used within CategoryProvider');
  }
  return context;
};