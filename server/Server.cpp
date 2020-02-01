//
// Created by Krzysztof Sychla on 2019-12-12.
//


#include "Server.h"

Server::Server(int port) {
    struct sockaddr_in serverAddr;
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_addr.s_addr = htonl(INADDR_ANY);
    serverAddr.sin_port = htons(port);

    long reuseAddrVal = 1;
    size_t sizeReuse = sizeof(reuseAddrVal) ;
    if((serverSocket = socket(AF_INET, SOCK_STREAM, 0)) < 0){
        throw "Could not create socket\n";
    }
    std::cout << "Created socket at " << serverSocket << "\n";

    if(setsockopt(serverSocket, SOL_SOCKET, SO_REUSEADDR, &reuseAddrVal, sizeReuse) < 0){
        std::cerr << "Could not set options for socket\n";
    }

    if(bind(serverSocket, (struct sockaddr*)&serverAddr, sizeof(struct sockaddr)) < 0){
        throw "Could not bind socket\n";
    }
    std::cout << "Socked binding successful\n";
    mtx = new std::mutex();
    mtx->unlock();
}

Server::~Server(){
    close(serverSocket);
}

//Connection* Server::getConnection(char* nick) {
//    return connTable[nick];
//}

//void Server::addConn(char* nick, Connection* conn) {
//    connTable[nick] = conn;
//}
#include <functional>
void Server::run() {

    if(listen(serverSocket, 5) < 0){
        throw "Could not listen on socket\n";
    }
    mtx = new std::mutex();     // TODO semafory lepsze (ale ich nie ma :c (mozna napisac w sumie))
    //std::thread *threadTab[10];
    while(true){
        int sock = accept(serverSocket, nullptr, nullptr);
        Connection conn(sock, connTable, mtx);
        std::cout << "Accepted connection at " << sock << "\n";
        mtx->lock();
        threadTab.push_back(new std::thread(conn));
        std::cout << "Created connection thread for " << sock << "\n";
        std::cout << "Sending info to all\n";
        sendToEverybody();
//        while(connTable.empty()){}acceptCall
//        std::cout << "Nie fajnie!";
//        try{
//            while (true){
//                readInfo(conn);
//            }
//        }catch (char const*& msg){
//            std::cerr << msg;
//        }
    }
}

//void Server::readInfo(Connection* conn) {
//    char* buff = new char(100);
//    if (read(conn->getSocket(), buff, 100) < 0) {
//        throw "Reading info failed\n";
//    }
//    Connection *c = nullptr;
//    Communication *com = nullptr;   // TODO: Delete pointers after the thing
//    switch (buff[0]) {
//        case 'N':
//            std::cout << "Nick: ";
//            addConn(++buff, conn);
//            conn->setNick(buff);
//            break;
//        case 'C':
//            std::cout << "Connect to: ";
//            c = getConnection(++buff);
//            connect(conn, c);
//            break;
//        case 'L':
//            sendInfo(conn);
//            break;
//        case 'D':
//            disconnect(conn);
//            break;
//        default:
////                std::cout << "This: " << buff << "\n";
//            throw "Not recognized action\n";
//    }
////    std::cout << buff << "\n";
//}

//void Server::disconnect(Connection* conn) {
//    for(auto a : connTable)
//        if(a.second == conn) {
//            connTable.erase(a.first);
//            break;
//        }
//    conn->sendData((char*)"Disconnected");
//    delete conn;
//}

//void Server::connect(Connection* conn1, Connection* conn2) {
//    try{
//        auto communication = new Communication(conn1, conn2);
//        while(communication->comunicate()) ;
//    }catch (char const*& msg) {
//        std::cerr << msg;
//    }
//}

//void Server::sendInfo(Connection* conn) {
//    char* wholeInfo = new char(100);
//    strcpy(wholeInfo, "S");
//    strcat(wholeInfo, "clients\n");
//    conn->sendData(wholeInfo);
//    sleep(1);
//    for(auto c : connTable){
//        strcpy(wholeInfo, "I");
//        strcat(wholeInfo, c.first);
//        strcat(wholeInfo, "\n");
//        std::cout << "Sending: " << wholeInfo;
//        conn->sendData(wholeInfo);
//        sleep(1);
//    }
//    strcpy(wholeInfo, "E");
//    strcat(wholeInfo, "clients\n");
//    conn->sendData(wholeInfo);
//}

void Server::sendToEverybody() {
    mtx->lock();
    for(auto c : connTable){
        std::cout << c.first << "\n";
        c.second->sendInfo();
    }
}