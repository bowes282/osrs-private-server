#Oldschool Runescape Private Server
This project aims to provide a way to create private servers without having to edit lots of Java code. The players can add content through the included Data Manager and the included script system.

The included client and server will contain a lot of integrated content, but it's up to the developer to use the functionality with their scripts.

The server and client may be customized, but in that case will no longer be supported by the programmers of this project. If anyone wishes to add custom content through editing of Java code they are free to do so, at their own risk. The project comes with build files to compile all source code into usable, executable code.

The project contains:
* A Client loading data from `./data/client`.
* A Server loading scripts from `./scripts` and data from `./data/server`.
* A data manager loading data from `./data`.
* A package.json so users can install dependencies via `npm install`.
* Gulp to manage scripts and to build releases for the client and the server.
* A batch file that runs the client and server in their respective source folders.
* A JSON-based item, npc and object definition. ?? (SQL or Mongo?)
* A JavaScript-based plugin feature system.