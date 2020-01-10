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

class Connection {
public:
    Connection(int, std::map<char*, Connection*>*);
    ~Connection();
    void operator()();
    void handleConnection();
    char* readData();
    void addConn(char*, Connection*);
    void sendData(char*);
    int getSocket() const;
    void disconnect();
    void setSocket(int socket);
    void setNick(char* nick) { this->nick = nick; }
    char* getNick() { return nick; }
    void sendInfo();
    Connection* getConnection(char*);
private:
    int socket;
    char* nick;
    std::map<char*, Connection*> *connTable;
};


#endif //SERVER_CONNECTION_H
