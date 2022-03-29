cd ./rust/

cargo build --target aarch64-linux-android --release
cargo build --target armv7-linux-androideabi --release
cargo build --target i686-linux-android --release

cd ..

cp ./rust/target/aarch64-linux-android/release/libreader.so ./android/app/src/main/jniLibs/arm64-v8a/
cp ./rust/target/armv7-linux-androideabi/release/libreader.so ./android/app/src/main/jniLibs/armeabi-v7a
cp ./rust/target/i686-linux-android/release/libreader.so ./android/app/src/main/jniLibs/x86
