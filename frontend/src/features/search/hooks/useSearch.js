import { useMemo } from 'react';

const useSearch = (items, searchTerm, searchFields = ['name', 'tags']) => {
  const filteredItems = useMemo(() => {
    if (!searchTerm.trim()) return items;
    
    const searchLower = searchTerm.toLowerCase();
    
    return items.filter((item) => {
      return searchFields.some((field) => {
        if (field === 'tags') {
          return item.tags?.some((tag) => tag.toLowerCase().includes(searchLower));
        }
        return item[field]?.toLowerCase().includes(searchLower);
      });
    });
  }, [items, searchTerm, searchFields]);

  return filteredItems;
};

export default useSearch;