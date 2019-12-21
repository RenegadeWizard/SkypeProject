//
// Created by Krzysztof Sychla on 2019-12-13.
//

#include "Communication.h"

Communication::Communication(Connection* conn1, Connection* conn2) {
    firstConn = conn1;
    secondConn = conn2;
}

Communication::~Communication() {
    delete firstConn;
    delete secondConn;
}

Connection *Communication::getFirstConn() const {
    return firstConn;
}

void Communication::setFirstConn(Connection *firstConn) {
    Communication::firstConn = firstConn;
}

Connection *Communication::getSecondConn() const {
    return secondConn;
}

void Communication::setSecondConn(Connection *secondConn) {
    Communication::secondConn = secondConn;
}

bool Communication::comunicate() {
    char* buff;
    buff = firstConn->readData();
    secondConn->sendData(buff);
    buff = secondConn->readData();
    firstConn->sendData(buff);
    return true;    // TODO return false when connection over
}