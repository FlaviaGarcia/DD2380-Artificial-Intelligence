#!/bin/bash

mv src/* .
find . -type d -exec rm -rf {} +
rm *Test*
rm HMM[0-9]*
rm Action.java Bird.java Client.java Constants.java Deadline.java GameServer.java GameState.java Main.java
find . -type f ! -name '*.java' -delete