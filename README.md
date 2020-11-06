# springboot-security-example
 Demo of api message encrypt/decrypt based on spring-boot.<br> 
 基于spingboot框架的接口报文加解密示例demo. 
 
 
## 项目结构
* **api** 服务接口定义层(interface).只定义接口，不实现具体业务.<br>
* **provider** 服务接口实现层.实现接口所要实现的业务逻辑，包含与sql、NoSql、消息队列等外部系统的交互.<br>
* **consumer** 服务消费层.接收HTTP请求获取请求报文(Controller)，调用api层定义的服务接口得到处理结果，并包装成返回报文返回出去.

## 加解密流程
1. 通过过滤器*SecurityFilter*定义接口过滤规则，对需要接解密的接口拦截并进行下一步操作
2. 将过滤器拦截到的接口的请求报文，使用*RequestHandler*进行解密操作，并将解密后的明文通过过滤器的doFilter进一步发送到Controller的接口中
3. 接口收到请求报文，对数据进行处理后，将返回数据返回出去
4. 通过*ResponseResultHandler*对返回报文加密.*ResponseResultHandler*使用@ControllerAdvice注解，指定需要对返回结果进行加密处理的接口所在的包

## 加解密算法
AES+RSA<br>
AES是常用的对称加密算法，RSA是常用的非对称加密算法.<br>
由于两种加密方式各有优劣，一般实际应用时扬长避短，使用对称加密法加密报文，使用非对称加密法加密对称加密的密钥.
### 加解密流程
#### 客户端到服务端
1. 服务端生成RSA公钥(publicKey)、私钥(privateKey).
2. 客户端生成AES密钥(aesKey)，使用AES将报文加密成密文(data).
3. 客户端获取publicKey，使用RSA对aesKey加密(encryptKey).
4. 客户端将data和encryptKey作为参数传给服务端.
#### 服务端返回结果给客户端
因为从客户端传来的aesKey是安全的，所以服务端直接用aesKey加密返回报文，将密文返回即可.
