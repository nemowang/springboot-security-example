# springboot-security-example
 Demo of api message encrypt/decrypt based on spring-boot.<br> 
 基于Spring-Boot框架的接口报文加解密示例demo. 
 
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

## 项目结构
* **api** 服务接口定义层(interface).只定义接口，不实现具体业务.<br>
* **provider** 服务接口实现层.实现接口所要实现的业务逻辑，包含与sql、NoSql、消息队列等外部系统的交互.<br>
* **consumer** 服务消费层.接收HTTP请求获取请求报文(Controller)，调用api层定义的服务接口得到处理结果，并包装成返回报文返回出去.

## 实现原理
通过过滤器filter拦截接口，解密报文；对返回报文加密处理。<br>
步骤如下：
1. consumer中的filter.[SecurityFilter](springboot-security-example-consumer/src/main/java/com/nemo/consumer/filter/SecurityFilter.java "拦截器")拦截需要解密的接口，将请求报文交给handler.[RequestHandler](springboot-security-example-consumer/src/main/java/com/nemo/consumer/handler/RequestHandler.java)处理。其中，请求报文的格式如下:<br>
     - bizContent: AES加密后的接口入参密文
     - domain:     RSA加密后的AES密钥
     - appId:      appId用于区分请求来源
2. [RequestHandler](springboot-security-example-consumer/src/main/java/com/nemo/consumer/handler/RequestHandler.java "请求报文处理")解析请求报文中的数据。<br>
通过appId调用[SecurityService](springboot-security-example-api/src/main/java/com/nemo/api/service/SecurityService.java "加解密密钥相关服务")服务从redis中获取到RSA私钥privateKey。<br>
使用privateKey对domain进行RSA解密，得到AES密钥domainKey。<br>
使用domainKey对bizContent进行AES解密得到明文data。<br>
将data和domainKey重新放回HttpRequest.body，发送给Controller接口
3. Controller层接口接收到data后进行业务处理得到返回数据，将返回数据和domainKey一起返回出去
4. [ResponseResultHandler](springboot-security-example-consumer/src/main/java/com/nemo/consumer/handler/ResponseResultHandler.java "处理返回数据")拦截到返回数据，使用domainKey对data进行加密，再将加密后的响应报文返回给接口调用方
至此，接口数据的加解密完成。

## 接口调用Demo
位于consumer的[CipherTest.java](springboot-security-example-consumer/src/test/java/com/nemo/consumer/CipherTest.java "API加解密完整调用demo")展示了一次完整的接口调用，包括：
1. 原始请求报文AES加密；
2. 对AES密钥加密；
3. 构造加密请求报文；
4. 调用接口获取返回报文；
5. 对返回报文进行AES解密 <br>

等主要步骤。
