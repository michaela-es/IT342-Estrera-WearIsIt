const fs = require("fs");

let output = [];

module.exports = function transformer(file, api) {
  const j = api.jscodeshift;
  const root = j(file.source);

  const imports = [];

  root.find(j.ImportDeclaration).forEach(path => {
    imports.push(j(path).toSource());
  });

  if (imports.length > 0) {
    output.push(`// ${file.path}\n${imports.join("\n")}`);
  }

  fs.writeFileSync(
    "imports-output.txt",
    output.join("\n\n"),
    "utf-8"
  );

  return file.source;
};