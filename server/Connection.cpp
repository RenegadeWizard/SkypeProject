//
// Created by Krzysztof Sychla on 2019-11-17.
//

#include "Connection.h"

Connection::Connection(int socketHandle, std::map<std::string, Connection*>& conns, std::mutex* mut) : connTable(conns) {
    socket = socketHandle;
    mtx.reset(new std::mutex());
    mutex = mut;
}

Connection::~Connection() = default;

void Connection::handleConnection() {
    std::string buff;
    if(!theRest.length()){
        buff = readData(1000);
    }else{
        buff = theRest.substr(0,theRest.find('\n'));
        if(buff != theRest)
            theRest = theRest.substr(theRest.find('\n')+1);
        else
            theRest = "";
    }


    Connection *c = nullptr;
    switch (buff[0]) {
        case 'N':
            std::cout << "Nick: ";
            buff = buff.substr(1);
            addConn(buff, this);
            setNick(buff);
            sendData("Z\n");
            mutex->unlock();
            break;
        case 'C':
            isBusy = true;
            std::cout << "Connect to: ";
            c = getConnection(buff.substr(1));
            if(!c->checkIfBusy()) {
                c->sendData("C" + connNick + "\n");
                //sendData((char *) "A\n");
                mtx->lock();
                mtx->lock();
                mtx->unlock();
                std::cout << "unlocked mtx for" << getNick() << "\n";

                if (hasAccepted) {
                    std::cout << c->getNick() << " has accepted the call\n";
                    handleCall(c);
                }
                else{
                    std::cout << c->getNick() << " has rejected the call\n";
                }

                isBusy = false;
            }
            break;
        case 'L':
            sendInfo();
            break;
        case 'A':
            isBusy = true;
            c = getConnection(buff.substr(1));
            c->setHasAccepted(true);
            c->sendData("A\n");
            c->unlockMtx();
            handleCall(c);
            break;
        case 'R':
            isBusy = true;
            c = getConnection(buff.substr(1));
            c->setHasAccepted(false);
            c->sendData("R\n");
            c->unlockMtx();
            break;
        case 'D':
             disconnect();
            break;
        case 'O':
            std::cout << buff.substr(1);
            theRest = buff.substr(buff.find('\n')+1);
            buff = buff.substr(1,buff.find('\n'));
            bytes = std::stoi(buff);
            break;
        default:
//                std::cout << "This: " << buff << "\n";
            throw "Not recognized action\n";
    }
    //delete buff;
    //delete tmp;   //TODO zwalnianie pamięci
//    std::cout << buff << "\n";
}

std::string Connection::readData(int bytes) {
    char *buff = (char*) malloc(bytes);
    int n = read(socket, buff, bytes-1);
    if(n == -1){
        throw "Reading failed\n";
    }
    buff[n] = 0;
    std::string string = std::string(buff);
    free(buff);
    return string;
}

void Connection::sendData(std::string buff) {
    auto s = "O" + std::to_string(buff.length()) + "\n";
    write(socket, s.c_str(), s.length());
    write(socket, buff.c_str(), buff.length());
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
    while(!shouldClose) {
        handleConnection();
    }
}

void Connection::sendInfo() {
    sendData("Sclients\n");
    for(auto c : connTable){
        std::cout << "Sending: " << "I" + c.first + "\n";
        sendData("I" + c.first + "\n");
    }
    sendData("Eclients\n");
    mutex->unlock();
}

void Connection::disconnect() {
    for(auto a : connTable)
        if(a.second == this) {
            connTable.erase(a.first);
            break;
        }
    sendData("Disconnected\n");
    close(socket);
    shouldClose = true;
}

void Connection::request() {
    sendData("Sclients\n");
}

Connection* Connection::getConnection(std::string nick) {
    return connTable[nick];
}

bool Connection::checkIfBusy() {
    return isBusy;
}

void Connection::unlockMtx() {
    mtx->unlock();
}

std::string Connection::readPhoto() {
    std::string buff, rest;
    int ile = 0, juz = 0;
    buff = readData(1000);
    if(buff[0] != 'O')
        return "Fail";
    int i;
    while((i = buff.find('P')) < 0){
        buff += readData(100);
    }

    rest = buff.substr(i);
    buff = buff.substr(1, i-1);
    ile = std::stoi(buff);
    juz = rest.length();
    while(juz < ile - 1){
        if(ile - juz < 1000)
            buff = readData(ile - juz + 1);
        else
            buff = readData(1000);
//        std::cout << buff;
        rest.append(buff);
        juz += buff.length();
    }
    return rest;
}

void Connection::handleCall(Connection* c) {
    std::cout << getNick() << " started handling the call\n";

    call = true;
    bool hangedUpByThisClient = false;
    std::string photo;
    while(call) {
        photo = readPhoto();
        if(photo == "Fail")
            continue;
        std::cout << getNick() << " read the data\n";
        if (photo[0] == 'G') {
            call = false;
            c->setCall(false);
            hangedUpByThisClient = true;
        }
        else{

            c->sendData(photo + "\n");
        }
    }

    if(!hangedUpByThisClient) {
        sendData("G\n");
    }
}

void Connection::setHasAccepted(bool hasHeThough) {
    hasAccepted = hasHeThough;
}

void Connection::setCall(bool cl) {
    this->call = cl;
}

