//
// Created by Krzysztof Sychla on 2019-12-12.
//


#include "Server.h"

Server::Server(int port) {
    connTable = new std::map<std::string, Connection*>();
    struct sockaddr_in serverAddr;
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_addr.s_addr = htonl(INADDR_ANY);
    serverAddr.sin_port = htons(port);

    char reuseAddrVal = 1;
    if((serverSocket = socket(AF_INET, SOCK_STREAM, 0)) < 0){
        throw "Could not create socket\n";
    }
    std::cout << "Created socket at " << serverSocket << "\n";

    if(setsockopt(serverSocket, SOL_SOCKET, SO_REUSEADDR, (char*)&reuseAddrVal, sizeof(reuseAddrVal)) < 0){
        std::cerr << "Could not set options for socket\n";
    }

    if(bind(serverSocket, (struct sockaddr*)&serverAddr, sizeof(struct sockaddr)) < 0){
        throw "Could not bind socket\n";
    }
    std::cout << "Socked binding successful\n";
}

Server::~Server(){
    close(serverSocket);
}

Connection* Server::getConnection(std::string nick) {
    return (*connTable)[nick];
}

void Server::addConn(std::string nick, Connection* conn) {
    connTable->insert(std::make_pair(nick, conn));
}

void Server::run() {
    if(listen(serverSocket, 5) < 0){
        throw "Could not listen on socket\n";
    }
    while(true){
        int sock = accept(serverSocket, nullptr, nullptr);
        auto conn = new Connection(sock);

        std::cout << "Accepted connection at " << sock << "\n";
        try{
            readInfo(conn);
            readInfo(conn);
        }catch (char const*& msg){
            std::cerr << msg;
        }
    }
}

void Server::readInfo(Connection* conn) {
    char* buff = new char(100);
    if(read(conn->getSocket(), buff, 100) < 0){
        throw "Reading info failed\n";
    }
    Connection* c = nullptr;
    Communication* com = nullptr;
    switch(buff[0]){
        case 'N':
            std::cout << "Nick: ";
            addConn(++buff, conn);
            break;
        case 'C':
            std::cout << "Connect to: ";
            c = getConnection(++buff);
            com = new Communication(conn, c);
            break;
        default:
            throw "Not recognized action\n";
    }
    std::cout << buff << "\n";
}

void Server::disconnect(Connection* conn) {
// TODO:   Delete from map
    delete conn;
}

void Server::connect(Connection* conn1, Connection* conn2) {
    auto communication = new Communication(conn1, conn2);
    bool connection = true;
    while(connection)
        connection = communication->comunicate();
}

void Server::sendInfo(Connection* conn) {
    char* buff = nullptr;   // TODO array of data with info about other clients
    conn->sendData(buff);
}