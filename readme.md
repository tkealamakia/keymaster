Install the Add-on

1. Copy src/main/addon/UberPwn to your {WoW Install Directory}/Interface/Addons
2. Note that when you start up the WoW client with the addon installed that it will replace most of your key bindings.
3. The addon will also create some macros for you to use. The notable macros are UberPwn_Attack1 and UberPwn_StopAttack.
4. When you load the game the add-on will appear as a white box with many color boxes inside of it. You are able to drag the add-on but you must ALWAYS restart the Java Client (if it is already running) after moving the add-on.  You must always be careful not to place the add-on box at a location where any UI elements will overlay it (i.e. opening your bags could obscure the add-on).


Build & Run the Java Client

Pre-requisites:  Java and Maven must be installed and be executable from your command line.

1. Build the project by executing 'mvn clean install'
2. Launch the WoW client in either 'Windowed' mode or 'Windowed Full Screen' mode.
3. Login to your character.
4. Ensure that your video settings in the WoW client have Multi-sampling set to '1x'.
5. Check "Do Not Flash Screen at Low Health" in the Interface / Combat settings screen.
6. On a Mac you will need to adjust your color to 'Generic RGB Profile' in Display settings. If your WoW client is open when you change the Display settings you will need to restart the WoW client.
7. Make sure you don't adjust your gamma setting in the wow client.  It should be set to '1'.
7. On a *nix platform execute 'runUber.sh'.  On a Windows platform run 'runUber.bat'.
8. Make sure the java application is laid on top of the WoW client with the addon bar visible on the screen.
9. Pick a rotation from the list, or create one.
10. Click the 'Start' button.
11. Now click into the WoW client sending the java app to the background.
12. To attack, target an enemy unit and then use the 'UberPwn_Attack1' macro.
13. To stop attacking use the 'UberPwn_StopAttack' macro.
