
#ifndef SERVERDATA_H
#define SERVERDATA_H
#include<vector>
#include<string>

using namespace std;

class ServerData
{
private:
	char filePath[1024];
public:
	void writeFile(const char* content, const char* fileName);
	pair<char*, int> readFile(const char* fileName);
	vector<string> getFileList();
	ServerData(const char* file_path);
	char* getFilePath();
};
#endif // !1


#pragma once


