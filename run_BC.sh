#! /bin/sh

base_jar="$HOME/.m2/repository/org/openjfx/javafx-base/12.0.2/javafx-base-12.0.2-linux.jar"
graphics_jar="$HOME/.m2/repository/org/openjfx/javafx-graphics/12.0.2/javafx-graphics-12.0.2-linux.jar"
controls_jar="$HOME/.m2/repository/org/openjfx/javafx-controls/12.0.2/javafx-controls-12.0.2-linux.jar"
ctrl=1

if [ ! -f $base_jar ] || [ ! -f $graphics_jar ] || [ ! -f $controls_jar ] ; then
	ctrl=0

	echo $base_jar
	echo $graphics_jar
	echo $controls_jar
	echo "\tone of or all libraries are missing!"
fi

dir_app="out/production/tank_AI_-_Battle_City"
if [ ! -d $dir_app ] ; then
	ctrl=0
	echo "Directory  \"$dir_app\"  is missing!"
fi

if [ $ctrl -eq 1 ] ; then
	java --add-modules javafx.base,javafx.graphics,javafx.controls --add-reads javafx.base=ALL-UNNAMED --add-reads javafx.graphics=ALL-UNNAMED -classpath $dir_app:$base_jar:$graphics_jar:$controls_jar -p $base_jar:$graphics_jar:$controls_jar com.company.Main
fi

