//
// Created by Krzysztof Sychla on 2019-12-12.
//

#ifndef SERVER_SERVER_H
#define SERVER_SERVER_H


#include <map>
#include <string>
#include <unistd.h>
#include <stdio.h>
#include <iostream>
#include <sys/socket.h>
#include <stdlib.h>
#include <netinet/in.h>

#include "Connection.h"
#include "Communication.h"


class Server {
private:
    std::map<std::string, Connection*> connTable;
    int serverSocket;
public:
    Server(int);
    ~Server();
    Connection* getConnection(std::string);
    void addConn(std::string, Connection*);
    void run();
    void readInfo(Connection*);
    void disconnect(Connection*);
    void connect(Connection*, Connection*);
    void sendInfo(Connection*);
    void sendToEverybody();
};


#endif //SERVER_SERVER_H
