import { useState } from 'react';

const useTags = (tags, maxVisible = 5) => {
  const [showAll, setShowAll] = useState(false);
  
  const visibleTags = showAll ? tags : tags.slice(0, maxVisible);
  const hasMore = tags.length > maxVisible;
  const hiddenCount = tags.length - maxVisible;
  
  const toggleTags = () => setShowAll(!showAll);
  
  return {
    visibleTags,
    hasMore,
    hiddenCount,
    showAll,
    toggleTags
  };
};

export default useTags;