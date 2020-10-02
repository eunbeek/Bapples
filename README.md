# BAPPLES <img src="https://github.com/NesaByte/Bapples/blob/master/assets/apple.png" width="48">
Command line tool that takes in a textfile or url. Find Good, Bad, Unknown links.

# FEATURES
- Reads from local html files and processes URLS https:// or https
- Reads from web link and processes URLS https:// or http://
- After processing URLS, displays URLS and its status code
- Accepts multiple commands (e.g. --v --200 <url>)
- Added --version or -v command to check the current version of downloaded jarfile
- Added --secure command to check whether http:// URLs actually work using https://
- Added --XXX command to display URL with specific status code the user wants(e.g. --404 <url or .html>)
- Add support for timeouts, DNS resolution issues, or other server errors when accessing a bad URL
- Added support for coloured terminal text


# INSTALLATION
Make sure you have java properly installed in your pc

```java -version``` should give you "java version "15" 2020-09-15"

```javac -version``` should give you "javac 15"

If not, install java in your system from https://www.java.com/en/download/

After cloning the repository, unzip folder, open cmd prompt, cd into /assets. 
Finally, execution:

```java -jar bapples.jar <command>```

# COMMANDS
| code| description | how to use|
|-----------------------|----------------------------------------------|--|
|--v or --version | to check the Bapple version                  | java -jar bapples.jar --v|
|--h or --help    | to check the Bapple help                     |java -jar bapples.jar --h |
|--200            | to list urls with status code: SUCCESS       |java -jar bapples.jar --200 <url or .html>|
|--400 or --404    | to list urls with status code: CLIENT ERRORS |java -jar bapples.jar --404 <url or .html>|
|--XXX            | to list urls with status code: UNKNOWNS      |java -jar bapples.jar --310 <url or .html>|
|--secure         | to check URLS with http:// if they work with https://|java -jar bapples.jar --secure <url or .html>|



# LICENSE
- [MIT](https://github.com/NesaByte/Bapples/blob/master/LICENSE)
- Icons made by mangsaabguru from www.flaticon.com