import { useMemo } from 'react';

export const useItemDetails = (image) => {
  const groupedTags = useMemo(() => {
    if (!image?.tags?.length) return [];
    
    const groups = image.tags.reduce((acc, tag) => {
      const category = tag.categoryName;
      if (!acc[category]) acc[category] = [];
      acc[category].push(tag.name);
      return acc;
    }, {});

    return Object.entries(groups).map(([category, tags]) => ({
      category,
      tags
    }));
  }, [image?.tags]);

  const stats = useMemo(() => ({
    wornCount: image?.itemWc || 0,
    addedDate: image?.createdAt 
      ? new Date(image.createdAt).toLocaleDateString('en-US', {
          year: 'numeric',
          month: 'long',
          day: 'numeric'
        })
      : 'Unknown'
  }), [image?.itemWc, image?.createdAt]);

  return {
    groupedTags,
    stats
  };
};