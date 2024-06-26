pnpm clean
npx swc ./src -C module.type=commonjs  -d build-cjs
npx swc ./src -C module.type=es6  -d build-esm
mkdir -p dist/cjs
mkdir -p dist/esm
cp -r build-cjs/src/* dist/cjs
cp -r build-esm/src/* dist/esm
npx tsc --emitDeclarationOnly --outDir types -p ./tsconfig.json --moduleResolution node16
