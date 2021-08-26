#include "TLVPackage.h"
#include<vector>
#include<iostream>
#include<fstream>

TLVPackage::TLVPackage(char* package)
{
    char Type[4];
    char Length[4];
    memcpy(Type, package, 4);
    memcpy(Length, package + 4, 4);
    memset(value, 0, sizeof(value));
    type = Common::byteToInt(Type);
    length = Common::byteToInt(Length);
    memcpy(value, package + 8, length - 8);
}

TLVPackage::TLVPackage()
{
    memset(value, 0, sizeof(value));
    length = 0;
    type = 0;
}

TLVPackage::TLVPackage(int Type, int Length, char* Value)
{
    type = Type;
    length = Length;
    memcpy(value, Value, Length - 8);
}

char* TLVPackage::packageValue()
{
    char package[1024];
    char Type[4];
    char Length[4];
    memset(package, 0, sizeof(package));
    Common::intToByte(length, Length);
    Common::intToByte(type, Type);
    memcpy(package, Type, 4);
    memcpy(package + 4, Length, 4);
    memcpy(package + 8, value, length - 8);
    return package;
}


int TLVPackage::getType()
{
    return type;
}

int TLVPackage::getLength()
{
    return length;
}

char* TLVPackage::getValue()
{
    return value;
}

void TLVPackage::setType(int Type)
{
    type = Type;
}

void TLVPackage::setLength(int Length)
{
    length = Length;
}

void TLVPackage::setValue(char* Value, int valueLength)
{
    memcpy(value, Value, valueLength);
}

void Common::intToByte(int n, char* result) {

    result[0] = (char)(n & 0x000000ff);
    result[1] = (char)((n & 0x0000ff00) >> 8);
    result[2] = (char)((n & 0x00ff0000) >> 16);
    result[3] = (char)((n & 0xff000000) >> 24);
}

int Common::byteToInt(char* bytes) {
    int n = 0;

    n = n + ((byte)bytes[0] & 0x000000ff);
    n = n + (((byte)bytes[1] & 0x000000ff) << 8);
    n = n + (((byte)bytes[2] & 0x000000ff) << 16);
    n = n + (((byte)bytes[3] & 0x000000ff) << 24);
    return n;
}