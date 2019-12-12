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
//        Obsluga blędu
    }

    if(setsockopt(serverSocket, SOL_SOCKET, SO_REUSEADDR, (char*)&reuseAddrVal, sizeof(reuseAddrVal)) < 0){
//        Obsluga bledu
    }

    if(bind(serverSocket, (struct sockaddr*)&serverAddr, sizeof(struct sockaddr)) < 0){
//        Obsluga bledu
    }
}

Server::~Server(){
    close(serverSocket);
}

int Server::getIp(std::string nick) {
    return connTable[nick];
}

void Server::addConn(std::string nick, int sock) {
    connTable.insert(std::make_pair(nick, sock));
}

void Server::run() {
    if(listen(serverSocket, 5) < 0){
//        Obsluga bledu
    }
    while(true){
        int sock = accept(serverSocket, NULL, NULL);
        char buff[100];
        read(sock, buff, 100);
        std::string s = buff;
        addConn(s, sock);   //TODO: Not working idk why
        std::cout << getIp(s);
    }
}