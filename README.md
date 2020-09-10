# Battle City in Java

![Battle City](/images/battle_City_1.png "Battle City Java app - master branch")

Battle City - level 18  |  Battle City - level 26
:----------------------:|:----------------------:
![Battle City](/images/battle_City_3.png "Battle City Java app - dev branch")  |  ![Battle City](/images/battle_City_2.png "Battle City Java app - dev branch")

## Copyright
### This is Java's part of the Battle City. I do not own copyright on the original game.

Battle City was developed by [Namco](https://www.namcoentertainment.com/) and published in 1985.

### Map logo of the game
![Map-logo of original Battle City](/images/battle_City_logo.png "Original Battle City - map with logo by Namco")

### Menu of the original game

![Game menu of original Battle City](/images/battle_City_menu.png "Original Battle City - menu of the game")

### 2 examples of maps
Original Battle City - level 18  |  Original Battle City - level 26
:-------------------------------:|:-------------------------------:
![Map 18th of original Battle City](/images/battle_City_original_18.png "Original Battle City - level 18")  |  ![Map 26th of original Battle City](/images/battle_City_original_26.png "Original Battle City - level 26")

## Modyfications and differences from the original game
- Player tank can promote after taking the **star powerup**, but when it gets shot it degrades when its level is higher than 1 or die
- When 2 or more bullets touch each other then small explosion is simulated
- Enemy port does not create a tank when it collides with any players bullet or any other tank
- New enemy tanks are not freezed when **freeze powerup** was collected
- Player can choose maps freely

## About
It is a 4-directional shooter (vertical and horizontal directions) as a window application written in Java having an openGL extension (if supported by JVM).
One or two players controls their respective tank (yellow or green) and fight against enemy tanks.
The player's task is to protect the eagle and destroy all enemy tanks.

In development version Artificial Intelligence and Machine Learning are implemented which enables the players tank to be driven by a computer.

## Why I developed this project
- I wanted to get familiar with AI and Machine Learning
- I wanted to develop one deskop project in Java (JavaFX)

