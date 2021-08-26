#ifndef CLIENTDATA_H
#define CLIENTDATA_H
#include<vector>
#include<string>
using namespace std;
class ClientData
{
private:
	char filePath[1024];
public:
	void writeFile(const char* content, const char* fileName);
	char* readFile(const char* fileName);
	vector<string> getFileList();
	ClientData(const char* file_path);
	char* getFilePath();
};
#endif // !1


#pragma once


