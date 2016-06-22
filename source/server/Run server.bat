@echo off
title Server
java -classpath .\bin;.\lib\mysql.jar;.\lib\jython.jar;.\lib\log4j-1.2.15.jar;.\lib\mina.jar;.\lib\netty.jar;.\lib\poi.jar;.\lib\slf4j-nop.jar;.\lib\slf4j.jar;.\lib\xstream.jar nl.osrs.GameEngine
pause