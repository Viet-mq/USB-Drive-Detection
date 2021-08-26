#pragma once
#include<string>
#include<WinSock2.h>

using namespace std;

#ifndef TLVPACKAGE_H
#define TLVPACKAGE_H

namespace Common {
	void intToByte(int n, char* result);
	int byteToInt(char* bytes);
}

class TLVPackage
{
private:
	int type;
	int length;
	char value[1024];

public:
	TLVPackage(char* package);
	TLVPackage(int type, int length, char* value);
	TLVPackage();
	int getType();
	int getLength();
	char* getValue();
	char* packageValue();
	void setType(int Type);
	void setLength(int Length);
	void setValue(char* Value, int valueLength);
};

#endif // !TLVPACKAGE_H

