import urllib
import json
from urllib import request
from urllib import parse

def get_value():

    return '45,70,44,70,86,36,35,19,85,46,26,78,45,36,35,19,85,46,26,78,9,85,46,26,17'

def http_post(data):
    url='http://'+ip_addr+':'+port+'/handler'
    values ={'data':data}
    jdata = parse.urlencode(values).encode(encoding='UTF8')           # 对数据进行JSON格式化编码
    # headers = {'User-Agent': 'User-Agent:Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36'}
    req = request.Request(url,jdata)       # 生成页面请求的完整数据
    response = request.urlopen(req)       # 发送页面请求
    print(response.__dict__)
    return response.read().decode(encoding='UTF8')                    # 获取服务器返回的页面信息


ip_addr = '127.0.0.1'
port = '9100'
x = get_value()
resp = http_post(x)
print(resp)