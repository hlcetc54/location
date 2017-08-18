# -*- coding: utf-8 -*-

from http.server import BaseHTTPRequestHandler, HTTPServer
from urllib import parse as urlparse
from urllib import parse
import numpy as np
import pandas as pd
from sklearn.preprocessing import scale
from sklearn import preprocessing
from sklearn.externals import joblib
import numpy


## 字符串to数组
def str2arr(str):
    arr_str = str.split(',')
    arr = []
    for i in arr_str:
        arr.append(float(i))
    return arr

## 定义归一化函数normal
def normal(arr,f = [10,8,3,4]):
    arr_1 = arr[0:f[0]]
    max_1 = max(arr_1)
    arr_1_= []
    for i in arr_1:
        arr_1_.append(i/max_1)

    arr_2 = arr[f[0]:f[0]+f[1]]
    print(arr_2)
    max_2 = max(arr_2)
    arr_2_ = []
    for i in arr_2:
        arr_2_.append(i/max_2)

    arr_3 = arr[f[0] + f[1]:f[0] + f[1] + f[2]]
    max_3 = max (arr_3)
    arr_3_ = []
    for i in arr_3:
        arr_3_.append (i / max_3)

    arr_4 = arr[f[0] + f[1] + f[2]:f[0] + f[1] + f[2] + f[3]]
    max_4 = max (arr_4)
    arr_4_ = []
    for i in arr_4:
        arr_4_.append (i / max_4)

    return  arr_1_ + arr_2_ + arr_3_ + arr_4_


## 返回定位结果lable
def get_label(data):

    # 如果结尾时&就截取掉
    if(data.endswith("&")):
        data = data[:-1]
    print(data)


    arr = str2arr(data.split('=')[1])[0:25]
    arr = normal(arr)
    print(arr)
    arr = np.array(arr).reshape(1,25)
    # 导入训练好的模型
    nn = joblib.load ("train_model.m")
    # 给出定位结果
    lable = nn.predict (arr)  # 可以选择为在路径上连续行走
    print ("lable:", lable)
    # 保存定位结果
    numpy.savetxt ('result.csv', lable, fmt='%d')  # fmt='%d'将各个结果保存为十进制整数
    return str(lable[0])


class Handler (BaseHTTPRequestHandler):
    def __init__(self, request, client_address, server):
        BaseHTTPRequestHandler.__init__ (self, request, client_address, server)
        self.errorcode = 200
        self.body = ""

    def do_POST(self):
        path_query = urlparse.urlparse (self.path)
        path = path_query.path

        if path == "/handler":
            data = self.rfile.read(int (self.headers['content-length'])).decode(encoding='UTF8')

            data=parse.unquote (data)
            # data = parse.urlencode(data)
            # print(data)
            # data = parse.quote( data)
            # print(data)
            self.errorcode = 200
            self.body = get_label (data)

        else:
            self.send_error (404, "file not found");
            return
        self.resp ()

    def resp(self):
        self.send_response (self.errorcode)
        self.send_header ("Content-type", "text/html")
        self.end_headers ()

        self.wfile.write(bytes(self.body.encode(encoding='UTF8')))
        self.wfile.flush ()


PORT = 9100
httpd = HTTPServer (("", PORT), Handler)
print ("serving at port", PORT)
httpd.serve_forever ()