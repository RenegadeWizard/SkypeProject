//
// Created by Krzysztof Sychla on 2019-12-13.
//

#ifndef SERVER_COMMUNICATION_H
#define SERVER_COMMUNICATION_H

#include "Connection.h"

class Communication {
private:
    Connection* firstConn;
    Connection* secondConn;
public:
    Communication(Connection*, Connection*);
    ~Communication();
    Connection *getFirstConn() const;
    void setFirstConn(Connection *firstConn);
    Connection *getSecondConn() const;
    void setSecondConn(Connection *secondConn);
    void comunicate(Connection*);
};


#endif //SERVER_COMMUNICATION_H
