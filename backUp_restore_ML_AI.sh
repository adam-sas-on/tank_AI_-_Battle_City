#! /bin/sh

pwd=$PWD
ai_resources_dir="out/production/tank_AI_-_Battle_City/resources/ai_resources/"
ai_file="tank_ai.bin"
ml_file="tanks_ml_ai.bin"
can_be_restored=0
can_be_backed_up=0

if [ -f $ai_file ] || [ -f $ml_file ] ; then
	can_be_restored=1
fi

if [ -f "$ai_resources_dir$ai_file" ] || [ -f "$ai_resources_dir$ml_file" ] ; then
	can_be_backed_up=1
fi

if [ $can_be_restored -eq 0 ] && [ $can_be_backed_up -eq 0 ] ; then
	echo "There are no ML/AI files to back up or restore"
	exit 1
fi


back_up () {
	echo "Back up ML/AI data"
	if [ -f "$ai_file" ] ; then
		rm "$ai_file"
	fi
	if [ -f "$ml_file" ] ; then
		rm "$ml_file"
	fi

	cd $ai_resources_dir
	if [ -f "$ai_file" ] ; then
		cp "$ai_file" "$pwd/"
	fi
	if [ -f "$ml_file" ] ; then
		cp "$ml_file" "$pwd/"
	fi
}

restore () {
	echo "Restore ML/AI data"
	cd "$ai_resources_dir"

	if [ -f "$ai_file" ] ; then
		rm "$ai_file"
	fi
	if [ -f "$ml_file" ] ; then
		rm "$ml_file"
	fi

	cd "$pwd"
	if [ -f "$ai_file" ] ; then
		cp "$ai_file" "$ai_resources_dir"
	fi
	if [ -f "$ml_file" ] ; then
		cp "$ml_file" "$ai_resources_dir"
	fi
}

if [ $can_be_restored -eq 1 ] && [ $can_be_backed_up -eq 1 ] ; then
	# selection=`zenity --list "Option 1" "Option 2" "Option 3" --column="commands: " --text="Select command" --title="Backup / restore ML-AI")`
	echo "1) Back-up AI/machine learning files (copy to current dir)"
	echo "2) Update AI/machine learning files on production (move to production resources)"
	echo "*) Quit"
	read -p "Please select an option (type number)  " -r opt # -n 1

	if [ $opt -eq 1 ] ; then
		back_up

	elif [ $opt -eq 1 ] ; then
		restore

	else
		echo "Operation cancelled"
		exit 0
	fi

elif [ $can_be_backed_up -eq 1 ] ; then
	read -p "Do you want to back up ML/AI files? (yes/no) " -r opt # -n 1
	if [ ! "$opt" = "y" ] && [ ! "$opt" = "Y" ] && [ ! "$opt" = "yes" ] ; then
		echo "Operation cancelled"
		exit 0
	fi

	back_up

else
	read -p "Do you want to restore ML/AI files? (yes/no) " -r opt # -n 1
	if [ ! "$opt" = "y" ] && [ ! "$opt" = "Y" ] && [ ! "$opt" = "yes" ] ; then
		echo "Operation cancelled"
		exit 0
	fi
	restore
fi

