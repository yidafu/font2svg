# build asserts
cd font2svg-app
pnpm build
cd ..
# copy asserts to webroot
cp -r font2svg-app/dist/* ./font2svg-server/src/main/resources/webroot
# build server jar
./gradlew  clean :font2svg-server:shadowJar
