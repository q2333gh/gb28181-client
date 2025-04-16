import requests

# Spring Boot 应用运行的主机和端口
host = "localhost"
port = 8035 # 根据 application-dev.yml 中的 server.port

# 构建完整的 URL
url = f"http://{host}:{port}/gb28181/register"

print(f"尝试调用接口: PUT {url}")

try:
    # 发送 PUT 请求
    response = requests.put(url)

    # 检查响应状态码
    if response.status_code == 200:
        print("接口调用成功!")
        print("响应内容:")
        # 尝试打印响应文本，如果响应是 JSON 可以用 response.json()
        try:
            print(response.text)
        except Exception as e:
            print(f"(无法解析响应内容: {e})")
    else:
        print(f"接口调用失败，状态码: {response.status_code}")
        print("响应内容:")
        try:
            print(response.text)
        except Exception as e:
            print(f"(无法解析响应内容: {e})")

except requests.exceptions.ConnectionError as e:
    print(f"连接错误: 无法连接到 {url}")
    print(f"请确保 Spring Boot 应用正在运行并且端口 {port} 可访问。")
    print(f"错误详情: {e}")
except Exception as e:
    print(f"调用接口时发生未知错误: {e}")
