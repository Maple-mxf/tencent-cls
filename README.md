
## 腾讯云CLS接入log4j/Logback


###腾讯云CLS提供了两种日志采集方式：
- LogListener基于监听CVM日志文件实现日志采集
- API采集，需要日志框架自动向CLS主动报备（本项目基于日志框架主动向CLS报备达到日志采集的目的）
