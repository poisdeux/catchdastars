#!/bin/bash

ps -ef | grep 'gdx-platform-[0-9.]*-natives-desktop.jar' | awk '{print $2}' | xargs kill -9
