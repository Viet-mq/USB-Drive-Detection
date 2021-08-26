#pragma once
#include "ServerData.h"
#include "ServerFilter.h"
#include<WinSock2.h>
#include "TLVBuffer.h"
#ifndef SERVERAPI_H
#define SERVERAPI_H
#include"SocketServer.h"
/// <summary>
/// chứa các giao tiếp với client
/// </summary>
class ServerAPI :public SocketServer
{
protected:
	ServerData* sData;
	ServerFilter* sFilter;
	SOCKET s;
	TLVBuffer buff;
public:
	void sendFile(const char* fileName);
	void getFile(const char* fileName);
	ServerAPI(const char* path, SOCKET c);
};
#endif // !SERVERAPI_H



