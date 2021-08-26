#include "ServerAPI.h"
#include<iostream>
#include <WinSock2.h>
#include<fstream>
#include "TLVPackage.h"
using namespace std;

ServerAPI::ServerAPI(const char* path, SOCKET c)
{
	sFilter = new ServerFilter(path);
	sData = new ServerData(path);
	s = c;
}

void ServerAPI::getFile(const char* fileName)
{
	if (sFilter->checkFileExist(fileName)) {
		// gửi từ chối tới client
		TLVPackage p(404, 30, (char*)"File is already exits");
		send(s, p.packageValue(), p.getLength(), 0);
	}
	else {
		// gửi yêu cầu nhận file, nội dung file
		TLVPackage p(200, 11, (char*)"OK");
		send(s, p.packageValue(), p.getLength(), 0);
		// Bắt đầu nhận file
		char path[2048];
		memset(path, 0, sizeof(path));
		sprintf_s(path, "%s\\%s", sData->getFilePath(), fileName);
		fstream file;
		file.open(path, ios::out | ios::binary);
		int fileReceiveLength = 0;
		char buffer[1024];
		cout << "Start receive file from client" << endl;
		while (true) {
			memset(buffer, 0, sizeof(buffer));
			int byteRecv = recv(s, buffer, sizeof(buffer), 0) - 8;
			buff.addData(buffer, byteRecv + 8);
			TLVPackage pk = buff.getPackage();
			while (pk.getType() != -1) {
				cout << "Type: " << pk.getType() << ", Length: " << pk.getLength() << ", Value: " << pk.getValue() << endl << endl;
				if (file.is_open() && byteRecv > 0) {
					fileReceiveLength += byteRecv;
					cout << "receiv: " << byteRecv - 8 << endl;
					file.write(pk.getValue(), byteRecv);
				}
				else {
					cout << "file is not opened" << endl;
				}
				// Gặp Tag = 201 kết thúc file
				if (pk.getType() == 201) {
					break;
				}
				pk = buff.getPackage();
			}
			if (pk.getType() == 201) {
				break;
			}
		}
		cout << "fileReceiveLength: " << fileReceiveLength << endl;
		file.flush();
		file.close();
	}
}

void ServerAPI::sendFile(const char* fileName)
{
	// khi file chưa tồn tại
	if (!sFilter->checkFileExist(fileName)) {
		// gửi từ chối tới client
		TLVPackage p(404, 25, (char*)"File is not exits");
		send(s, p.packageValue(), p.getLength(), 0);
	}
	else {
		// gửi nội dung tới client
		cout << "file was found" << endl;
		fstream f;
		// mở file
		char path[2048];
		memset(path, 0, sizeof(path));
		sprintf_s(path, "%s\\%s", sData->getFilePath(), fileName);
		f.open(path, ios::in | ios::binary);
		// Chuyển con trỏ file tới cuối file
		f.seekg(0, ios::end);
		// lấy độ dài của file
		int flen = f.tellg();
		// chuyển con trỏ file về đầu
		f.seekg(0, ios::beg);
		// biến đếm số byte đọc được
		int sent = 0;
		// Biến lưu dữ liệu đọc được
		TLVPackage p(200, 11, (char*)"OK");
		send(s, p.packageValue(), p.getLength(), 0);
		char data[1016];
		// Đọc lần lượt file
		while (sent < flen) {
			memset(data, 0, sizeof(data));
			// Số byte đọc từ file
			int byteRead = min(1016, flen - sent);
			// Số byte gửi
			int byteSend = byteRead + 8;
			f.read(data, byteRead);
			TLVPackage pakage(200, byteSend, data);
			sent += byteRead;
			if (sent == flen) {
				// Nếu đã đọc hết file => gửi gói tin với cờ 201;
				pakage.setType(201);
			}
			char dataSend[1024];
			memset(dataSend, 0, sizeof(dataSend));
			memcpy(dataSend, pakage.packageValue(), byteSend);
			send(s, dataSend, byteSend, 0);
			/*TLVPackage p(dataSend);
			file.write(p.getValue(), byteRead);*/
		}
		cout << "Sending ok" << endl;
	}
}
