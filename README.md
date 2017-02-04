This is a simple screen sharing application written in java. 

##How to Run


* Clone the repository

```
$ git clone https://github.com/UltimatePea/ScreenSharer
```

* Go to the bin folder

```
$ cd ScreenSharer/bin/
```

* Run the Application

```
$ java ScreenShare
```

  - For the server, type server followed by a port, (any integer between 1024 and 65534 should work)
  ```
  localhost>>> server 10000
  ```

  - For the client, type client followed by server address and server port
  ```
  localhost>>> client 127.0.0.1 10000
  ```



