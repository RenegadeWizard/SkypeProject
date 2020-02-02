//
// Created by krzysztof on 01.02.2020.
//

#ifndef SERVER_READERWRITER_H
#define SERVER_READERWRITER_H


#include <string>
#include <mutex>

class ReaderWriter {
private:
    std::string readBuffor;
    int bytesToRead;
    int socket;
    std::mutex* mutex;
public:
    explicit ReaderWriter(int);
    std::string readData();
    void sendData(std::string);
};


#endif //SERVER_READERWRITER_H
