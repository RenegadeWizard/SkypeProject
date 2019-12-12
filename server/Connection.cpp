//
// Created by Krzysztof Sychla on 2019-11-17.
//

#include "Connection.h"

Connection::Connection(int socketHandle) {
    socket = new int(socketHandle);
}

Connection::~Connection() {
    delete socket;
}

void Connection::handleConnection() {

}

void Connection::readData() {

}

void Connection::sendData() {

}
