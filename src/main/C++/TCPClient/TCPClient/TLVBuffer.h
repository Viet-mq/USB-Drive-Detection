#pragma once
#include "TLVPackage.h"

#ifndef TLVBUFFER_H
#define TLV_BUFFER_H

class TLVBuffer
{
private:
	char buff[10000];

public:
	int buffLen;
	TLVBuffer();
	TLVPackage getPackage();
	void addData(char* data, int len);
};

#endif // !TLVBUFFER_H
