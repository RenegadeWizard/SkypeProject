//
// Created by Krzysztof Sychla on 2019-12-12.
//


#include "Server.h"

Server::Server(int port) {
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

Connection* Server::getConnection(char* nick) {
    return connTable[nick];
}

void Server::addConn(char* nick, Connection* conn) {
    connTable[nick] = conn;
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
            sendToEverybody();
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
    Communication* com = nullptr;   // TODO: Delete pointers after the thing
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
    char* wholeInfo = new char(100);
    strcpy(wholeInfo, "S");
    strcat(wholeInfo, "clients\n");
    conn->sendData(wholeInfo);
    sleep(1);
    for(auto c : connTable){
        strcpy(wholeInfo, "I");
        strcat(wholeInfo, c.first);
        strcat(wholeInfo, "\n");
        std::cout << "Sending: " << wholeInfo;
        conn->sendData(wholeInfo);
        sleep(1);
    }
    strcpy(wholeInfo, "E");
    strcat(wholeInfo, "clients\n");
    conn->sendData(wholeInfo);

}

void Server::sendToEverybody() {
    for(auto c : connTable){
        sendInfo(c.second);
    }
}
