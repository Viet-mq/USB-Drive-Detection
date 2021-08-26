#include "ClientData.h"
#include<iostream>
#include <string.h>
#include <vector>
#include<fstream>
#include<WinSock2.h>

using namespace std;

void ClientData::writeFile(const char* content, const char* fileName)
{
	fstream file;
	file.open(fileName, ios::out);
	if (file.is_open()) {
		file.write(content, strlen(content));
	}
	else {
		cout << "file is not opened" << endl;
	}
	file.close();
}
char* ClientData::readFile(const char* fileName)
{
	char* content = NULL;
	FILE* f = fopen(fileName, "rb");
	fseek(f, 0, SEEK_END);
	int flen = ftell(f);
	fseek(f, 0, SEEK_SET);
	content = (char*)calloc(flen, 1);
	if (content != NULL) {
		fread(content, 1, flen, f);
	}
	fclose(f);
	return content;
}

vector<string> ClientData::getFileList()
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
ClientData::ClientData(const char* file_path)
{
	memset(filePath, 0, sizeof(filePath));
	strcpy(filePath, file_path);
}
char* ClientData::getFilePath()
{
	return filePath;
}