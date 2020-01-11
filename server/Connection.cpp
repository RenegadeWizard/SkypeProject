//
// Created by Krzysztof Sychla on 2019-11-17.
//

#include "Connection.h"

Connection::Connection(int socketHandle, std::map<std::string, Connection*>& conns) : connTable(conns) {
    socket = socketHandle;
    mtx.reset(new std::mutex());
}

Connection::~Connection() = default;

void Connection::handleConnection() {
    char* buff = new char(100);
    if (read(socket, buff, 100) < 0) {
        throw "Reading info failed\n";
    }
    Connection *c = nullptr;
    char* tmp = new char(100);
    std::string stringNick;
    switch (buff[0]) {
        case 'N':
            std::cout << "Nick: ";
            buff++;
            stringNick = std::string(buff);
            addConn(stringNick, this);
            setNick(stringNick);
            sendData((char*)"Z\n");
            break;
        case 'C':
            isBusy = true;
            std::cout << "Connect to: ";
            c = getConnection(++buff);
            if(!c->checkIfBusy()) {
                strcpy(tmp, "C");
                strcat(tmp, connNick.c_str());
                c->sendData(tmp);
                //sendData((char *) "A\n");
                mtx->lock();
                mtx->lock();
                if (hasAccepted)
                    handleCall(c);

                isBusy = false;
            }
            //connect(conn, c);
            break;
        case 'L':
            sendInfo();
            break;
        case 'A':
            isBusy = true;
            c = getConnection(++buff);
            c->setHasAccepted(true);
            strcpy(tmp, "A");
            c->sendData(tmp);
            c->unlockMtx();
            handleCall(c);
            break;
        case 'R':
            isBusy = true;
            c = getConnection(++buff);
            c->setHasAccepted(false);
            strcpy(tmp, "R");
            c->sendData(tmp);
            c->unlockMtx();
            break;
        case 'D':
             disconnect();
            break;
        default:
//                std::cout << "This: " << buff << "\n";
            throw "Not recognized action\n";
    }
    delete buff;
    delete tmp;
//    std::cout << buff << "\n";
}

char* Connection::readData(int bytes) {
    char* buff = nullptr;
    read(socket, buff, bytes);
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

void Connection::addConn(std::string nick, Connection* conn) {
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
    //sleep(1);
    for(auto c : connTable){
        strcpy(wholeInfo, "I");
        strcat(wholeInfo, c.first.c_str());
        strcat(wholeInfo, "\n");
        std::cout << "Sending: " << wholeInfo;
        sendData(wholeInfo);
        //sleep(1);
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
    std::string snick = std::string(nick);
    return connTable[snick];
}

bool Connection::checkIfBusy() {
    return isBusy;
}

void Connection::unlockMtx() {
    mtx->unlock();
}

void Connection::handleCall(Connection* c) {
    char* buf ;
    call = true;
    bool hangedUpByThisClient = false;
    while(call) {
        buf = readData(1000);
        if (buf[0] == 'G') {
            call = false;
            c->setCall(false);
            hangedUpByThisClient = true;
        }
        else{
            c->sendData(buf);
        }
    }
    if(!hangedUpByThisClient) {
        sendData((char*)"G\n");
    }
}

void Connection::setHasAccepted(bool didThey) {
    hasAccepted = didThey;
}

void Connection::setCall(bool cl) {
    this->call = cl;
}