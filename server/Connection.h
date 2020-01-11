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
    Connection(int, std::map<std::string, Connection*>&);
    ~Connection();
    void operator()();
    void handleConnection();
    char* readData(int);
    void addConn(std::string, Connection*);
    void sendData(char*);
    int getSocket() const;
    void disconnect();
    void setSocket(int socket);
    void setNick(std::string nick) { this->connNick = nick; }
    std::string getNick() { return connNick; }
    void sendInfo();
    Connection* getConnection(char*);
    bool checkIfBusy();
    void unlockMtx();
    void handleCall(Connection*);
    void setHasAccepted(bool);
    void setCall(bool);
private:
    int socket;
    bool isBusy = false;
    bool hasAccepted = false;
    bool call = false;
    std::string connNick;
    std::map<std::string, Connection*> &connTable;
    std::shared_ptr<std::mutex> mtx;
};


#endif //SERVER_CONNECTION_H
