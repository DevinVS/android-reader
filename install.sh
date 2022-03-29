cd ./rust/

cargo build --target aarch64-linux-android --release
cargo build --target armv7-linux-androideabi --release
cargo build --target i686-linux-android --release

cd ..

aarch64-linux-android-strip ./rust/target/aarch64-linux-android/release/libreader.so
arm-linux-androideabi-strip ./rust/target/armv7-linux-androideabi/release/libreader.so
i686-linux-android-strip ./rust/target/i686-linux-android/release/libreader.so

ls -sh ./rust/target/aarch64-linux-android/release/libreader.so
ls -sh ./rust/target/armv7-linux-androideabi/release/libreader.so
ls -sh ./rust/target/i686-linux-android/release/libreader.so

cp ./rust/target/aarch64-linux-android/release/libreader.so ./android/app/src/main/jniLibs/arm64-v8a/
cp ./rust/target/armv7-linux-androideabi/release/libreader.so ./android/app/src/main/jniLibs/armeabi-v7a
cp ./rust/target/i686-linux-android/release/libreader.so ./android/app/src/main/jniLibs/x86
