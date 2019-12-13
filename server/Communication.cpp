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

void Communication::comunicate(Connection* conn) {
    char buff[1000];
    ssize_t r = read(conn->getSocket(), buff, 1000);
    write(conn->getSocket(), buff, (size_t) r); // TODO idk if ok
}