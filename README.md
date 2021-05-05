
## 腾讯云CLS接入log4j/Logback


### 腾讯云CLS提供了两种日志采集方式：
- LogListener基于监听CVM日志文件实现日志采集
- API采集，需要日志框架自动向CLS主动报备（本项目基于日志框架主动向CLS报备达到日志采集的目的）

### 开发者需要修改的文件：
- src/main/resources/logback.xml
- src/main/resources/log4j.properties
替换以上两个文件中的secretId，secretKey，topicId，这三个字段在腾讯云CLS控制台获取


