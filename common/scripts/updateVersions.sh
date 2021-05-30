#!/bin/bash -e

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
pushd $SCRIPT_DIR/../..

# Load new version info
source version.properties

GRADLE_PROPS="# Do not edit this file directly!\n# Edit 'version.properties' and then use 'common/scripts/updateVersions.sh' to keep everything in sync.\n$(tail -n +3 version.properties)"

# Update junit-platform-native
echo -e "$GRADLE_PROPS" > common/junit-platform-native/gradle.properties

# Update gradle plugin
echo -e "$GRADLE_PROPS" > native-image-gradle-plugin/gradle.properties

# Update gradle plugin example
echo -e "$GRADLE_PROPS" > examples/gradle/gradle.properties

# Update maven plugin
pushd native-image-maven-plugin
mvn versions:set -DnewVersion=${nativeimage_maven_version} -DgenerateBackupPoms=false
mvn versions:set-property -Dproperty=junit.platform.native -DnewVersion=${junit_platform_native_version} -DgenerateBackupPoms=false
popd

# Update maven plugin example
pushd examples/maven
mvn versions:set-property -Dproperty=nativeimage.maven.version -DnewVersion=${nativeimage_maven_version} -DgenerateBackupPoms=false
mvn versions:set-property -Dproperty=junit.jupiter.version -DnewVersion=${junit_jupiter_version} -DgenerateBackupPoms=false
mvn versions:set-property -Dproperty=junit.platform.native -DnewVersion=${junit_platform_native_version} -DgenerateBackupPoms=false
popd

popd