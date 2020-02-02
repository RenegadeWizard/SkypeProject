//
// Created by Krzysztof Sychla on 2019-11-17.
//

#include "Connection.h"

Connection::Connection(int socketHandle, std::map<std::string, Connection*>& conns, std::mutex* mut) : connTable(conns) {
    socket = socketHandle;
    mtx.reset(new std::mutex());
    mutex = mut;
    reader = new ReaderWriter(socket);
}

Connection::~Connection() = default;

void Connection::handleConnection() {
    std::string buff = reader->readData();

    Connection *c = nullptr;
    switch (buff[0]) {
        case 'N':
            std::cout << "Nick: ";
            buff = buff.substr(1);
            addConn(buff, this);
            setNick(buff);
            reader->sendData("Z\n");
            mutex->unlock();
            break;
        case 'C':
            isBusy = true;
            std::cout << "Connect to: ";
            c = getConnection(buff.substr(1));
            if(!c->checkIfBusy()) {
                c->reader->sendData("C" + connNick + "\n");
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
            c->reader->sendData("A\n");
            c->unlockMtx();
            handleCall(c);
            break;
        case 'R':
            isBusy = true;
            c = getConnection(buff.substr(1));
            c->setHasAccepted(false);
            c->reader->sendData("R\n");
            c->unlockMtx();
            break;
        case 'D':
             disconnect();
            break;
        case 'O':
            std::cout << buff.substr(1);
//            theRest = buff.substr(buff.find('\n')+1);
            buff = buff.substr(1,buff.find('\n'));
//            bytes = std::stoi(buff);
            break;
        default:
//                std::cout << "This: " << buff << "\n";
            throw "Not recognized action\n";
    }
    //delete buff;
    //delete tmp;   //TODO zwalnianie pamięci
//    std::cout << buff << "\n";
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
    reader->sendData("Sclients\n");
    for(auto c : connTable){
        reader->sendData("I" + c.first + "\n");
    }
    reader->sendData("Eclients\n");
    mutex->unlock();
}

void Connection::disconnect() {
    for(auto a : connTable)
        if(a.second == this) {
            connTable.erase(a.first);
            break;
        }
    reader->sendData("Disconnected\n");
    close(socket);
    std::cout << connNick << " disconnected\n";
    shouldClose = true;
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

void Connection::handleCall(Connection* c) {
    std::cout << getNick() << " started handling the call\n";

    call = true;
    bool hangedUpByThisClient = false;
    std::string photo;
    while(call) {
        photo = reader->readData();
        std::cout << getNick() << " read the data\n";
        if (photo[0] == 'G') {
            c->reader->sendData(photo + "\n");
            call = false;
            c->setCall(false);
            hangedUpByThisClient = true;
        }
        else{
            c->reader->sendData(photo + "\n");
        }
    }

    if(!hangedUpByThisClient) {
        reader->sendData("G\n");
    }
}

void Connection::setHasAccepted(bool hasHeThough) {
    hasAccepted = hasHeThough;
}

void Connection::setCall(bool cl) {
    this->call = cl;
}

