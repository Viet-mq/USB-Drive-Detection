#include<iostream>
#include<WinSock2.h>
#include<fstream>
#include"Client.h"
#include "TLVPackage.h"
using namespace std;
string addr, clientRepoPath;
int port = 0;

void readConfig() {
	fstream file("config.txt", ios::in || ios::out);
	// Read confix file
	while (!file.eof()) {
		char line[1024];
		char configName[1024];
		char configValue[1024];
		memset(line, 0, sizeof(line));
		memset(configName, 0, sizeof(configName));
		memset(configValue, 0, sizeof(configValue));
		file.getline(line, sizeof(line));
		sscanf(line, "%s%s", configName, configValue);
		
		if (!strcmp(configName, "ADDRESS")) {
			addr = configValue;
			cout << "Server address: " << addr << endl;
		}

		if (!strcmp(configName, "PORT")) {
			sscanf(configValue, "%d", &port);
			cout << "Port: " << port << endl;
		}
		
		if (!strcmp(configName, "CLIENT_LOCATE")) {
			clientRepoPath = configValue;
			cout << "Client locate: " << clientRepoPath << endl;
		}
	}
}
SOCKET createSocket(char* addr, int port) {
	SOCKET s = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
	SOCKADDR_IN saddr;
	saddr.sin_family = AF_INET;
	saddr.sin_addr.S_un.S_addr = inet_addr(addr);
	saddr.sin_port = htons(port);
	connect(s, (sockaddr*)&saddr, sizeof(saddr));
	return s;
}
int main() {
	readConfig();

	//Initialize Socket
	WSADATA DATA;
	WSAStartup(MAKEWORD(2, 2), &DATA);
	SOCKET s = createSocket((char*)addr.c_str(), port);
	char buffer[1024];
	memset(buffer, 0, sizeof(buffer));
	recv(s, buffer, sizeof(buffer), 0);
	cout << buffer << endl;

	// Initialize Client
	Client* client = new Client(clientRepoPath.c_str(), s);

	//Send file
	string fileName = "Events.log";
	client->sendFile(fileName.c_str());

	cout << "Disconnect to server";
	closesocket(s);

	WSACleanup();
}
