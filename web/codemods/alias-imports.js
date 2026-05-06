const path = require('path');

module.exports = function transformer(fileInfo, api) {
  const j = api.jscodeshift;
  const root = j(fileInfo.source);

  const filePath = fileInfo.path.replace(/\\/g, '/');
  const dirty = false;

  const getSrcRelative = (resolved) => {
    const match = resolved.replace(/\\/g, '/').match(/\/src\/(.+)/);
    return match ? match[1] : null;
  };

  root.find(j.ImportDeclaration).forEach((p) => {
    const raw = p.node.source.value;

    if (!raw.startsWith('.')) return;

    const fileDir = path.dirname(filePath).replace(/\\/g, '/');
    const resolved = path
      .resolve(fileDir, raw)
      .replace(/\\/g, '/');

    const srcRelative = getSrcRelative(resolved);
    if (!srcRelative) return;

    let newImport = null;

    // =========================
    // 1. shared inside features
    // =========================
    if (srcRelative.includes('/features/shared/')) {
      newImport =
        '@features/shared/' +
        srcRelative.split('/features/shared/')[1];
    }

    // =========================
    // 2. feature modules
    // =========================
    else if (srcRelative.startsWith('features/')) {
      newImport =
        '@features/' +
        srcRelative.replace('features/', '');
    }

    // =========================
    // 3. OLD STRUCTURE SUPPORT (IMPORTANT)
    // =========================

    // hooks → shared/hooks
    else if (srcRelative.startsWith('hooks/')) {
      newImport = '@features/shared/hooks/' + srcRelative.replace('hooks/', '');
    }

    // components → shared/components
    else if (srcRelative.startsWith('components/')) {
      newImport = '@features/shared/components/' + srcRelative.replace('components/', '');
    }

    // context → feature-specific OR auth fallback
    else if (srcRelative.startsWith('context/')) {
      newImport = '@features/auth/context/' + srcRelative.replace('context/', '');
    }

    // pages → feature pages (best guess fallback)
    else if (srcRelative.startsWith('pages/')) {
      newImport = '@features/pages/' + srcRelative.replace('pages/', '');
    }

    if (newImport) {
      console.log(`${filePath}\n  ${raw} → ${newImport}`);
      p.node.source = j.stringLiteral(newImport);
    }
  });

  return root.toSource({ quote: 'single' });
};