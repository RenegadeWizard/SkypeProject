//
// Created by Krzysztof Sychla on 2019-11-17.
//

#ifndef SERVER_CONNECTION_H
#define SERVER_CONNECTION_H

#include <map>
#include <sys/socket.h>
#include <unistd.h>
#include <cstring>
#include <string>
#include <stdio.h>
#include <stdlib.h>
#include <netinet/in.h>
#include <thread>
#include <iostream>
#include <mutex>

class Connection {
public:
    Connection(int, std::map<std::string, Connection*>&, std::mutex*);
    ~Connection();
    void operator()();
    void handleConnection();
    std::string readData(int);
    void addConn(std::string, Connection*);
    void sendData(std::string);
    int getSocket() const;
    void disconnect();
    void setSocket(int socket);
    void setNick(std::string nick) { this->connNick = nick; }
    std::string getNick() { return connNick; }
    void sendInfo();
    Connection* getConnection(std::string);
    bool checkIfBusy();
    void unlockMtx();
    void handleCall(Connection*);
    void setHasAccepted(bool);
    void setCall(bool);
    std::string readPhoto();
    void request();
    std::mutex* mutex;
private:
    int socket;
    bool isBusy = false;
    bool hasAccepted = false;
    bool call = false;
    bool shouldClose = false;
    int bytes = -1;
    std::string theRest = "";
    std::string connNick;
    std::map<std::string, Connection*> &connTable;
    std::shared_ptr<std::mutex> mtx;
};


#endif //SERVER_CONNECTION_H
