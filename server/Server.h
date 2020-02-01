//
// Created by Krzysztof Sychla on 2019-12-12.
//

#ifndef SERVER_SERVER_H
#define SERVER_SERVER_H


#include <map>
#include <string>
#include <cstring>
#include <unistd.h>
#include <stdio.h>
#include <iostream>
#include <sys/socket.h>
#include <stdlib.h>
#include <netinet/in.h>
#include <thread>
#include <vector>
#include <sys/types.h>
#include <mutex>

#include "Connection.h"


class Server {
private:
    std::map<std::string, Connection*> connTable;
    int serverSocket;
    std::vector<std::thread*> threadTab;
    int numberOfClients = 0;
public:
    Server(int);
    ~Server();
    //Connection* getConnection(char*);
    //void addConn(char*, Connection*);
    void run();
    //void readInfo(Connection*);
    //void disconnect(Connection*);
    //void connect(Connection*, Connection*);
    //void sendInfo(Connection*);
    void sendToEverybody();
    std::mutex* mtx;
};


#endif //SERVER_SERVER_H
