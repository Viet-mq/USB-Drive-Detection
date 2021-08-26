#pragma once
#ifndef SOCKETSERVER_H
#define SOKCETSERVER_H
class SocketServer
{
public:
	virtual void getFile(const char* fileName) = 0;
	virtual void sendFile(const char* fileName) = 0;
};
#endif // !SOCKETSERVER_h

