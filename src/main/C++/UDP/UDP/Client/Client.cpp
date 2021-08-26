#include "Client.h"
#include<iostream>
#include<fstream>
#include "TLVPackage.h"
using namespace std;
Client::Client(const char* path, SOCKET c)
{
	cData = new ClientData(path);
	s = c;
}

void Client::sendFile(const char* fileName)
{
	char buffer[1024];
	memset(buffer, 0, sizeof(buffer));
	recv(s, buffer, sizeof(buffer), 0);
	TLVPackage pag(buffer);
	if (pag.getType() == 200) {
		char path[2048];
		memset(path, 0, sizeof(path));
		sprintf_s(path, "%s\\%s", cData->getFilePath(), fileName);
		// gửi nội dung tới server
		fstream f;
		// mở file
		f.open(path, ios::in | ios::binary);
		// Chuyển con trỏ file tới cuối file
		f.seekg(0, ios::end);
		// lấy độ dài của file
		int flen = f.tellg();
		// chuyển con trỏ file về đầu
		f.seekg(0, ios::beg);
		// biến đếm số byte đọc được
		int sent = 0;
		// Biến lưu dữ liệu đọc được, size = 1016 = 1024 - 4 - 4
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
			memset(buffer, 0, sizeof(buffer));
			memcpy(buffer, pakage.packageValue(), byteSend);
			send(s, buffer, byteSend, 0);
		}
		f.close();
		cout << "Sending ok" << endl;
	}
	else {
		// Thông báo file đã có trên hệ thống
		cout << "File \"" << fileName << "\" is already exist on server\n";
	}
}

bool Client::checkFileExist(const char* fileName)
{
	vector<string> files = cData->getFileList();
	for (int i = 0; i < files.size(); i++) {
		string file = files[i];
		if (!strcmp(file.c_str(), fileName)) {
			return true;
			break;
		}
	}
	return false;
}
