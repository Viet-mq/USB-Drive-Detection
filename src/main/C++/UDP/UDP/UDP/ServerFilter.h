#pragma once
#include "ServerData.h"
#ifndef SERVERFILTER_H
#define SERVERFILTER_H
/// <summary>
/// Lọc các dữ liệu đầu vào
/// 
/// </summary>
class ServerFilter
{
private:
	ServerData* serverData;
public:
	ServerFilter(const char* path);
	bool checkFileExist(const char* fileName);
};
#endif // !1




