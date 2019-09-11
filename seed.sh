#!/bin/sh -x

if [ "$#" -lt 4 ]
then
  echo "Usage: seed.sh destination_dir project-name package ClassName"
  exit 1
fi

BASE_DIR=`dirname $0`
DEST_DIR=$1
PROJECT_NAME=$2
PACKAGE=$3
CLASS_NAME=$4

mkdir -p "$1/.idea"
cp -r $BASE_DIR/.idea/codeStyles "$DEST_DIR/.idea"
cp -r $BASE_DIR/.idea/dictionaries "$DEST_DIR/.idea"
cp -r $BASE_DIR/src "$DEST_DIR"
cp $BASE_DIR/.gitignore "$DEST_DIR"
sed "s/xemantic-openrndr-template/$PROJECT_NAME/g" $BASE_DIR/settings.gradle.kts >"$DEST_DIR/settings.gradle.kts"
sed "s/com.xemantic.openrndr.template.XemanticOpenrndrTemplateKt/$PACKAGE.${CLASS_NAME}Kt/g" $BASE_DIR/build.gradle.kts >"$DEST_DIR/build.gradle.kts"
rm $DEST_DIR/src/main/kotlin/XemanticOpenrndrTemplate.kt
sed "s/package com.xemantic.openrndr.template/package $PACKAGE/g" $BASE_DIR/src/main/kotlin/XemanticOpenrndrTemplate.kt >"$DEST_DIR/src/main/kotlin/$CLASS_NAME.kt"
