const path = require('path');

module.exports = function transformer(fileInfo, api) {
  const j = api.jscodeshift;
  const root = j(fileInfo.source);

  let changed = false;

  const PAGE_MAP = {
    AuthPage: 'auth/pages/AuthPage',
    ProfilePage: 'profile/pages/ProfilePage',
    GalleryPage: 'gallery/pages/GalleryPage',
    ItemDetails: 'item-details/pages/ItemDetails',
    UploadClothing: 'upload/pages/UploadClothing',
    OAuthCallback: 'auth/pages/OAuthCallback'
  };

  root.find(j.ImportDeclaration).forEach(p => {
    const val = p.node.source.value;

    if (!val.startsWith('@features/pages/')) return;

    const pageName = val.split('/').pop();

    const mapped = PAGE_MAP[pageName];

    if (mapped) {
      const newImport = `@features/${mapped}`;

      console.log(
        `FIX PAGE:\n  ${val} → ${newImport}`
      );

      p.node.source = j.stringLiteral(newImport);
      changed = true;
    }
  });

  return changed ? root.toSource({ quote: 'single' }) : fileInfo.source;
};