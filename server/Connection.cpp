//
// Created by Krzysztof Sychla on 2019-11-17.
//

#include "Connection.h"

Connection::Connection(int socketHandle) {
    socket = socketHandle;
}

Connection::~Connection() = default;

void Connection::handleConnection() {

}

char* Connection::readData() {
    char* buff = nullptr;
    read(socket, buff, 1000);
    return buff;
}

void Connection::sendData(char* buff) {
    write(socket, buff, 1000);
}

int Connection::getSocket() const {
    return socket;
}

void Connection::setSocket(int socket) {
    Connection::socket = socket;
}
