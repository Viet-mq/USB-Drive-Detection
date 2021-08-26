
#include "ServerFilter.h"
#include <iostream>
using namespace std;
ServerFilter::ServerFilter(const char* path)
{
    serverData = new ServerData(path);
}

bool ServerFilter::checkFileExist(const char* fileName)
{
    vector<string> files = serverData->getFileList();
    for (int i = 0; i < files.size(); i++) {
        string file = files[i];
        if (!strcmp(file.c_str(), fileName)) {
            return true;
            break;
        }
    }
    return false;
}

//int main() {
//    ServerFilter filter;
//    bool exist = filter.checkFileExist("ad.txt");
//    if (exist) {
//        cout << "Exist";
//    }
//    else {
//        cout << "Not Exist";
//    }
//    cin.get();
//    return 0;
//}