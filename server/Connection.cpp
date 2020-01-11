//
// Created by Krzysztof Sychla on 2019-11-17.
//

#include "Connection.h"
#include "Communication.h"

Connection::Connection(int socketHandle, std::map<char*, Connection*>& conns) {
    socket = socketHandle;
    connTable = conns;
}

Connection::~Connection() = default;

void Connection::handleConnection() {
    char* buff = new char(100);
    char* confbuf = (char*)"Z\n";
    if (read(socket, buff, 100) < 0) {
        throw "Reading info failed\n";
    }
    Connection *c = nullptr;
    Communication *com = nullptr;   // TODO: Delete pointers after the thing
    char* tmp = new char(100);
    switch (buff[0]) {
        case 'N':
            std::cout << "Nick: ";
            addConn(++buff, this);
            setNick(buff);
            sendData(confbuf);
            break;
        case 'C':
            std::cout << "Connect to: ";
            c = getConnection(++buff);
            //std::cout << buff << "\n";
            strcpy(tmp, "C");
            strcat(tmp, nick);
            c->sendData(tmp);
            sendData((char*) "A\n");
            //TODO: Tworzenie wątku communication
            //connect(conn, c);
            break;
        case 'L':
            sendInfo();
            break;
        case 'D':
             disconnect();
            break;
        default:
//                std::cout << "This: " << buff << "\n";
            throw "Not recognized action\n";
    }
//    std::cout << buff << "\n";
}

char* Connection::readData() {
    char* buff = nullptr;
    read(socket, buff, 1000);
    return buff;
}

void Connection::sendData(char* buff) {
    write(socket, buff, strlen(buff));
}

int Connection::getSocket() const {
    return socket;
}

void Connection::setSocket(int socket) {
    Connection::socket = socket;
}

void Connection::addConn(char* nick, Connection* conn) {
    connTable[nick] = conn;
}

void Connection::operator()() {
    while(true) {
        handleConnection();
    }
}

void Connection::sendInfo() {
    char* wholeInfo = new char(100);
    strcpy(wholeInfo, "S");
    strcat(wholeInfo, "clients\n");
    sendData(wholeInfo);
    sleep(1);
    for(auto c : connTable){
        strcpy(wholeInfo, "I");
        strcat(wholeInfo, c.first);
        strcat(wholeInfo, "\n");
        std::cout << "Sending: " << wholeInfo;
        sendData(wholeInfo);
        sleep(1);
    }
    strcpy(wholeInfo, "E");
    strcat(wholeInfo, "clients\n");
    sendData(wholeInfo);
}

void Connection::disconnect() {
    for(auto a : connTable)
        if(a.second == this) {
            connTable.erase(a.first);
            break;
        }
    sendData((char*)"Disconnected");
    close(socket);
    delete this;
}

Connection* Connection::getConnection(char* nick) {
    return connTable[nick];
}
