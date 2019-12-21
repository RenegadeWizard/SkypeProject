//
// Created by Krzysztof Sychla on 2019-11-17.
//

#ifndef SERVER_CONNECTION_H
#define SERVER_CONNECTION_H

#include <sys/socket.h>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <netinet/in.h>

class Connection {
public:
    Connection(int);
    ~Connection();
    void handleConnection();
    char* readData();
    void sendData(char*);
    int getSocket() const;
    void setSocket(int socket);
private:
    int socket;
};


#endif //SERVER_CONNECTION_H
