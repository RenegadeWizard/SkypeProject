//
// Created by Krzysztof Sychla on 2019-12-13.
//

#include <cstring>
#include <cstdio>
#include "Communication.h"

Communication::Communication(Connection* conn1, Connection* conn2) {
    firstConn = conn1;
    secondConn = conn2;
    char* wholeInfo = new char(100);
    strcpy(wholeInfo, "C");
    strcat(wholeInfo, conn1->getNick());
    secondConn->sendData(wholeInfo);
//    if(strcmp(secondConn->readData(), "ACC") != 0){
//        throw "Connection not established";
//    }
    firstConn->sendData((char*)"ACC");
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
    return (bool) strcmp(buff, "END");
}