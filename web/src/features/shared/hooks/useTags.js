import { useState, useMemo } from 'react';

const useTags = (tags, initialLimit = 5) => {
  const [showAll, setShowAll] = useState(false);
  
  const tagList = useMemo(() => {
    return tags.map(tag => typeof tag === 'string' ? tag : tag.name || String(tag));
  }, [tags]);
  
  const visibleTags = showAll ? tagList : tagList.slice(0, initialLimit);
  const hasMore = tagList.length > initialLimit;
  const hiddenCount = tagList.length - initialLimit;
  
  const toggleTags = () => setShowAll(!showAll);
  
  return {
    visibleTags,
    hasMore,
    hiddenCount,
    toggleTags,
    showAll
  };
};

export default useTags;