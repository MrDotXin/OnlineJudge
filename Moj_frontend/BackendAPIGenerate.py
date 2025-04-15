import subprocess
import re
import os
import shutil

# 此工具用来自动帮你使用openapi生成指定服务的请求库
# 你需要自定义服务模块名
# 最后输出到src/backend 下的相应模块名文件夹下


openapi_spec_path = 'http://localhost:8114/api/${service_name}/v2/api-docs'
openapi_request_path = 'http://localhost:8114'
services = ['user', 'question']
openapi_output_path = "/src/backend/"


# todo 使用AC自动机优化大体量字符替换?
def substitute(file_content : str, sources : list[str], targets : list[str]) -> str :
    for source, target in zip(sources, targets) :
        file_content = re.sub(source, target, file_content)
    return file_content

def file_substitue(file_path : str, source : list[str], target : list[str]) :
    target_content = ''
    with open(file_path, '+r') as file:
        content = file.read()
        target_content = substitute(content, source, target)

    with open(file_path, '+tw') as file :
        file.write(target_content)

def remove_backend_service(service_name : str) :
    question_dir = os.path.join('src', 'backend', service_name)

    if os.path.exists(question_dir) :
        shutil.rmtree(question_dir)
        
def run() : 
    os.chdir("moj_frontend")

    # 先把子服务文件夹都删完
    for service_name in services : 
        remove_backend_service(service_name)
    
    # 跑每个子服务的openapi接口文档
    for service_name in services :
        input_path = re.sub(r'\$\{service_name\}', service_name, openapi_spec_path)
        output_path = openapi_output_path + service_name 

        base_path = os.getcwd()
        
        print(input_path, output_path, base_path)
        
        command = [
            'openapi', '-i', input_path, '-o', base_path + output_path + '/'
        ]

        result = subprocess.run(command, capture_output=True, text=True, check=True, shell=True)
        print(result)
        
        file_path = base_path + openapi_output_path + service_name + '/core/OpenAPI.ts'
        file_substitue(
            file_path,
            [
                r'WITH_CREDENTIALS:\s*false',
                openapi_request_path + '/api/' + service_name 
            ],
            [
                'WITH_CREDENTIALS: true',
                openapi_request_path
            ])
        
if __name__ == "__main__" :
    run()
