#include<iostream>
#include<WinSock2.h>
#include<fstream>
#include<thread>
#include"ServerAPI.h"
#include "TLVPackage.h"
const int MAX_CLIENT = 1024;
vector<SOCKET> clientSocket;
string serverPath;
int port = 0;
using namespace std;
void ClientThread(SOCKET clientS, SOCKADDR_IN caddr)
{
	cout << "Client" << inet_ntoa(caddr.sin_addr) << " connect!" << endl;
	send(clientS, (char*)"Welcome to file exchange server!\n", 33, 0);
	SocketServer* sApi = new ServerAPI(serverPath.c_str(), clientS);
	while (true) {
		char data[1024];
		memset(data, 0, sizeof(data));
		int rev = recv(clientS, data, sizeof(data), 0);
		if (rev > 0)
		{
			TLVPackage p(data);
			//sscanf(data, "%s\n", data);
			char command[1024];
			char content[1024];
			memset(command, 0, sizeof(command));
			memset(content, 0, sizeof(content));
			sscanf(p.getValue(), "%s%s", command, content);
			// nhận file từ client
			if (!strcmp(command, "SENDFILE")) {
				sApi->getFile(content);
			}
			// Gửi file tới client
			if (!strcmp(command, "GETFILE")) {
				sApi->sendFile(content);
			}
		}
		else {
			cout << "Client" << inet_ntoa(caddr.sin_addr) << " has  disconnected!" << endl;
			break;
		}
	}
}
void readConfig()
{
	fstream file("config.txt", ios::in || ios::out);
	// đọc các cấu hình trong file cấu hình
	while (!file.eof()) {
		char line[1024];
		char configName[1024];
		char configValue[1024];
		memset(line, 0, sizeof(line));
		memset(configName, 0, sizeof(configName));
		memset(configValue, 0, sizeof(configValue));
		file.getline(line, sizeof(line));
		sscanf(line, "%s%s", configName, configValue);

		if (!strcmp(configName, "PORT")) {
			sscanf(configValue, "%d", &port);
			cout << "port: " << port << endl;
		}

		if (!strcmp(configName, "SERVER_LOCATE")) {
			serverPath = configValue;
			cout << "Server Locate: " << serverPath << endl;
		}
	}
}
SOCKET createSocket(int port)
{
	SOCKET s = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
	struct sockaddr_in saddr, daddr;
	saddr.sin_family = AF_INET;
	saddr.sin_port = htons(port);
	saddr.sin_addr.S_un.S_addr = INADDR_ANY;
	//saddr.sin_addr.S_un.S_addr = inet_addr("127.0.0.1");
	bind(s, (sockaddr*)&saddr, sizeof(saddr));
	return s;
}
int main() {
	readConfig();
	// Khởi tạo socket
	WSADATA data;
	WSAStartup(MAKEWORD(2, 2), &data);
	SOCKET s = createSocket(port);
	listen(s, 10);
	while (true) {
		SOCKADDR_IN caddr;
		int clen = sizeof(caddr);
		SOCKET c = accept(s, (sockaddr*)&caddr, &clen);
		if (c != WSAEWOULDBLOCK) {
			clientSocket.push_back(c);
			new thread(ClientThread, c, caddr);
		}
		else {
			cout << "no connect" << endl;
		}
	}
	/*ResultMsg msg = sAPI.readFile("config.txt");
	cout << msg.getContent() << endl;
	cin.get();*/
	WSACleanup();
	return 0;
}