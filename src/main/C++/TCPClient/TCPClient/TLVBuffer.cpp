#include "TLVBuffer.h"

TLVBuffer::TLVBuffer()
{
	memset(buff, 0, sizeof(buff));
}

TLVPackage TLVBuffer::getPackage()
{
	TLVPackage p(-1, 13, (char*)NULL);

	//Check if this is a TLVPackage or not?
	if (buffLen < 8) {
		return p;
	}
	else {
		char Type[4];
		char Length[4];
		char Value[1016];
		memcpy(Type, buff, 4);
		memcpy(Length, buff + 4, 4);
		int length = Common::byteToInt(Length);
		int type = Common::byteToInt(Type);

		//return NULL if data in buffer != length of TLVPackage
		if (buffLen < length) {
			return p;
		}

		else
		{
			//Initialize TLVPackage object
			memcpy(Value, buff + 8, length - 8);
			TLVPackage package(type, length, Value);

			memcpy(buff, buff + length, buffLen - length);
			memset(buff + buffLen - length, 0, length);

			buffLen -= length;

			return package;
		}
	}

	return TLVPackage();
}

void TLVBuffer::addData(char* data, int len)
{
	memcpy(buff + buffLen, data, len);

	buffLen += len;
}
