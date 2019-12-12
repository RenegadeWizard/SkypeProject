#include <iostream>
#include "Server.h"


int main(int argc, char* argv[]) {
    Server server(1234);
    server.run();
    return 0;
}