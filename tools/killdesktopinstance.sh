#!/bin/bash

ps -ef | grep 'co[m].strategames.catchdastars.desktop.DesktopLauncher' | awk '{print $2}' | xargs kill
