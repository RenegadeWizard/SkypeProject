//
// Created by Krzysztof Sychla on 2019-11-17.
//

#ifndef SERVER_CONNECTION_H
#define SERVER_CONNECTION_H

#include <sys/socket.h>

class Connection {
public:
    Connection(int);
    ~Connection();
    void handleConnection();
    void readData();
    void sendData();
private:
    int* socket;
};


#endif //SERVER_CONNECTION_H
