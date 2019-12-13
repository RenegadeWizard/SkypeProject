#include <iostream>
#include <string>
#include "Server.h"


int main(int argc, char* argv[]) {
    try{
        Server server(1234);
        server.run();
    }catch(char const*& msg){
        std::cerr << msg;
    }
    return 0;
}