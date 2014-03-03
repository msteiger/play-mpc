Play! MPC
===============

This is a simple and fast web frontend for [Music Player Daemon](http://www.musicpd.org/). 

## Live Demo
A non-functional demo can be accessed here: [http://msteiger.github.io/play-mpc](http://msteiger.github.io/play-mpc)

## Features

- MPD Player controls
- Playlist view and modification
- Filtering and sorting of database
- User authentification
- Device-specific layout (Desktop, Tablet, Smartphone)
- Web-Radio support

It uses the following technologies:

- [Play! 2](http://www.playframework.com)
- [Bootstrap 3](http://getbootstrap.com)
- [JavaMPD 4](http://www.thejavashop.net/javampd)

## Installation

1) Download and install Play! 2.1 or later

2) Either download the latest (tagged) version as zip file or use git to clone the repository.

3) Edit `conf/application.conf` to suit your needs

4) Enter the directory and run `play start`

5) The website is served at `localhost:9000`

6) The default login is bob@example.com // secret

## License 

This project is licensed under the **MIT License**

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

