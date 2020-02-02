//
// Created by krzysztof on 01.02.2020.
//

#include <unistd.h>
#include <iostream>
#include "ReaderWriter.h"

ReaderWriter::ReaderWriter(int socket){
    this->socket = socket;
    readBuffor = "";
    mutex = new std::mutex();
}

std::string ReaderWriter::readData() {
//    mutex->lock();
    int bytes = 1000;
    char *buff = (char*) malloc(bytes);
    int n;
    while(readBuffor.find('\n') == readBuffor.npos){

        n = read(socket, buff, bytes-1);
        if(n == -1){
            throw "Reading failed\n";
        }
        buff[n] = 0;
        readBuffor += std::string(buff);
    }
    if(readBuffor[0] == 'O'){
        bytesToRead = std::stoi(readBuffor.substr(1, readBuffor.find('\n')));
        readBuffor = readBuffor.substr(readBuffor.find('\n')+1);
    }

    int count = readBuffor.length();
    while(readBuffor.length() < bytesToRead) {
        n = read(socket, buff, 999);
        if (n == -1) {
            throw "Reading failed\n";
        }
        buff[n] = 0;
        readBuffor += std::string(buff);
        count += n;
    }
//    mutex->unlock();
    free(buff);
    std::string returnValue = readBuffor.substr(0, bytesToRead);
    readBuffor = readBuffor.substr(bytesToRead);
    std::cout << "Reading: " << returnValue << "\n";
    return returnValue;
}

void ReaderWriter::sendData(std::string buff) {
//    mutex->lock();
    auto s = "O" + std::to_string(buff.length()) + "\n";
    write(socket, s.c_str(), s.length());
    int k = write(socket, buff.c_str(), buff.length());
    std::cout << "Sending: " << buff;

//    while(k < buff.length()){
//        buff = buff.substr(k);
//        k = write(socket, buff.c_str(), buff.length());
//    }
//    mutex->unlock();
}
