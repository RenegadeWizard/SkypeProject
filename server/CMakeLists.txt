cmake_minimum_required(VERSION 3.12)
project(server)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_FLAGS -pthread)

add_executable(server main.cpp Connection.cpp Connection.h Server.cpp Server.h ReaderWriter.cpp ReaderWriter.h)