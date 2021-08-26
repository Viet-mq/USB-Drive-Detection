#include "ServerData.h"
#include<iostream>
#include <string.h>
#include <vector>
#include<fstream>
#include<WinSock2.h>
using namespace std;
void ServerData::writeFile(const char* content, const char* fileName)
{
	fstream file;
	file.open(fileName, ios::out);
	if (file.is_open()) {
		file.write(content, strlen(content));
	}
	else {
		cout << "file isnot opened" << endl;
	}
	file.close();
}
pair<char*, int> ServerData::readFile(const char* fileName)
{
	FILE* f = fopen(fileName, "rb");
	fseek(f, 0, SEEK_END);
	int flen = ftell(f);
	fseek(f, 0, SEEK_SET);
	char* data = new char[flen];
	if (data != NULL) {
		fread(data, 1, flen, f);
		fclose(f);
		return pair<char*, int>(data, flen);
	}
	else {
		fclose(f);
		return pair<char*, int>(data, 0);
	}
}

vector<string> ServerData::getFileList()
{
	vector<string> files;
	char fileName[1024];
	WIN32_FIND_DATAA findData;
	memset(fileName, 0, sizeof(fileName));
	sprintf(fileName, "%s\\*.*", filePath);
	HANDLE hFind = FindFirstFileA(fileName, &findData);
	if (hFind != INVALID_HANDLE_VALUE) {
		while (FindNextFileA(hFind, &findData))
		{
			files.push_back(findData.cFileName);
		}
	}
	return files;
}
ServerData::ServerData(const char* file_path)
{
	memset(filePath, 0, sizeof(filePath));
	strcpy(filePath, file_path);
}
char* ServerData::getFilePath()
{
	return this->filePath;
}