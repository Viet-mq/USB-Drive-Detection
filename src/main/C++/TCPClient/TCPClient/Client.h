#pragma once
#include "ClientData.h"
#include "TLVBuffer.h"
#include "SocketClient.h"
#include <WinSock2.h>

#ifndef CLIENT_H
#define CLIENT_H

class Client :public SocketClient
{
private:
	ClientData* cData;
	SOCKET s;
	TLVBuffer buff;
public:
	Client(const char* path, SOCKET s);
	void sendFile(const char* fileName);
	bool checkFileExist(const char* fileName);
};

#endif // !CLIENT_H
