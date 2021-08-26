#pragma once
#include<string>
#include<WinSock2.h>
using namespace std;
#ifndef TLVPAGKAGE_H
#define TLVPACKAGE_H

namespace Common
{
	void intToByte(int n, char* result);
	int byteToInt(char* bytes);
}
/// <summary>
/// Gói tin TLV chuyển thông tin qua socket
/// </summary>
class TLVPackage
{
private:
	int type; //200: OK, 201 : ENDFILE, 404 : ERROR
	int length;
	char value[1016];
public:

	TLVPackage(char* package);

	TLVPackage(int Title, int Length, char* Value);

	TLVPackage();

	int getType();

	int getLength();

	char* getValue();

	char* packageValue();

	void setValue(char* Value, int valueLength);

	void setType(int Type);

	void setLength(int Length);
};
#endif // !TLVPAGKAGE_H


