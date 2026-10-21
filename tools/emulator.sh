#!/usr/bin/bash
# Start the project's Android emulator, with resources capped so it cannot
# starve the rest of the machine.
#
# Everything it writes - the virtual device, its snapshots, its logs - stays
# inside this directory. Nothing is written to the home directory.
#
# Usage: tools/emulator.sh start|stop|status

set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SDK="${ANDROID_HOME:-$HOME/Android/Sdk}"
AVD_HOME="$ROOT/.android/avd"
AVD_NAME="openstride"

export ANDROID_SDK_ROOT="$SDK"
export ANDROID_AVD_HOME="$AVD_HOME"
export ANDROID_EMULATOR_HOME="$ROOT/.android"
export ANDROID_SDK_HOME="$ROOT/.android"
export PATH="$SDK/emulator:$SDK/platform-tools:$PATH"

# The caps. The machine has 12 cores and 6.6 GB of memory, and the build
# needs most of the memory, so the emulator gets a small slice.
CORES=2
MEMORY=2048
# The disk image is small: the app and the system fit easily.
DISK=2048

case "${1:-status}" in
  create)
    mkdir -p "$AVD_HOME"
    if [ -d "$AVD_HOME/$AVD_NAME.avd" ]; then
      echo "the virtual device already exists"
      exit 0
    fi
    echo no | avdmanager create avd \
      --name "$AVD_NAME" \
      --package "system-images;android-30;default;x86_64" \
      --device "pixel_2" \
      --force
    # A small disk, and no snapshot so the state does not grow.
    sed -i "s/^disk.dataPartition.size=.*/disk.dataPartition.size=${DISK}M/" \
      "$AVD_HOME/$AVD_NAME.avd/config.ini"
    echo "the virtual device is at $AVD_HOME/$AVD_NAME.avd"
    ;;
  start)
    mkdir -p "$ROOT/.scratch/emulator"
    exec emulator \
      -avd "$AVD_NAME" \
      -no-window \
      -no-audio \
      -no-boot-anim \
      -no-snapshot \
      -gpu swiftshader_indirect \
      -memory "$MEMORY" \
      -cores "$CORES" \
      -partition-size 2048 \
      -read-only \
      -no-metrics \
      -port 5554 \
      -camera-back none \
      -camera-front none \
      -netdelay none \
      -netspeed full
    ;;
  stop)
    adb -s emulator-5554 emu kill 2>/dev/null || true
    ;;
  status)
    adb devices -l
    ;;
  *)
    echo "usage: $0 create|start|stop|status" >&2
    exit 1
    ;;
esac
