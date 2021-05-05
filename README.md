一  引言
日志存储分析在应用系统中扮演着重要的角色，传统的ELK对于小型团队过于繁琐，维护麻烦，腾讯云提供了CLS日志采集分析系统，可以通过LogListener来实现业务代码无侵入的方式进行采集日志，开发者还可以通过API的方式来采集日志（目前好像没有提供sdk来采集开发者应用日志，或者笔者漏读了一部分文档），官网文档对于API采集日志的最佳实践文档相对较少，本文笔者根据自己的想法实现CLS结合Java领域的最常见的两种log工具的方案。

二  Log4j/Logback知识准备
log4j和logback是Java开发领域两款最常见的开源log库，内置了诸多日志Appender类，比如Stdout，FileAppender，开发者还可以根据自己的特定需求来自定义Appender，在log配置文件中配置自定义的Appender类即可实现自定义日志采集逻辑。

三  架构图

日志收集架构图
如上图，开发者可以选择通过自定义日志收集模块达到上报日志到腾讯云CLS。

四  腾讯云CLS逻辑概念准备
日志集：一个日志集对应一个项目或应用
日志主题：一个日志主题对应一类应用或服务
日志组：包含多条日志的集合
日志分区：一个日志主题可以划分多个主题分区，但至少有一个分区（可提高检索效率）
五  准备工作
登录腾讯云，获取secretId，secretKey（比较重要）
进入CLS控制台
按照个人需求创建日志主题（复制主题ID备用）

创建日志主题图
六  最佳实践
技术选型

Java1.8+
Maven3.6+
log4j1.2.6
logback1.1.7
CLS日志上报云API
本文作者通过调用API来实现结构化日志上传，参考https://cloud.tencent.com/document/product/614/16873
云API签名访问参考https://cloud.tencent.com/document/product/614/12445
云API签名实例demo参考http://signature-1254139626.file.myqcloud.com/signature.zip
上传结构化日志API需要google protobuf将日志内容转换为BP格式
创建maven项目，项目结构如下图

项目结构图
pom.xml加入以下依赖

       <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>1.7.7</version>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-core</artifactId>
            <version>1.1.7</version>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-access</artifactId>
            <version>1.1.7</version>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.1.7</version>
        </dependency>
        <dependency>
            <groupId>log4j</groupId>
            <artifactId>log4j</artifactId>
            <version>1.2.16</version>
        </dependency>
        <dependency>
            <groupId>commons-codec</groupId>
            <artifactId>commons-codec</artifactId>
            <version>1.10</version>
        </dependency>
        <dependency>
            <groupId>com.google.protobuf</groupId>
            <artifactId>protobuf-java</artifactId>
            <version>2.6.1</version>
        </dependency>
        <dependency>
            <groupId>org.apache.httpcomponents</groupId>
            <artifactId>httpclient</artifactId>
            <version>4.5.10</version>
        </dependency>
        <dependency>
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
            <version>30.1.1-jre</version>
        </dependency>
Log4j实现日志上报
Log4j需要实现自定义Appender需要继承org.apache.log4j.AppenderSkeleton，Log4j日志输出是通过事件驱动来完成的，所以在核心逻辑中，我们只需要获取事件携带的信息，并且调用云API，将这些事件中携带的信息上传即可，逻辑图如下


日志上报逻辑图
方案实现步骤
1 在maven项目resources目录下创建log4j.properties文件（log4j日志框架默认加载的文件名），具体配置如下

log4j.rootLogger=DEBUG,clsLogAppender, STDOUT
log4j.appender.clsLogAppender=com.github.cls.log4j.ClsLog4jAppender
log4j.appender.clsLogAppender.project=smartdoc
log4j.appender.clsLogAppender.region=ap-nanjing
log4j.appender.clsLogAppender.endpoint=ap-nanjing.cls.tencentcs.com
log4j.appender.clsLogAppender.secretId=XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
log4j.appender.clsLogAppender.secretKey=XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
log4j.appender.clsLogAppender.layout=org.apache.log4j.PatternLayout
log4j.appender.clsLogAppender.layout.ConversionPattern=%-4r [%t] %-5p %c %x - %m%n
log4j.appender.clsLogAppender.topicId=b9ed1869-156a-4eb4-ae96-b0fa292cf5b5
log4j.appender.clsLogAppender.source=smart-doc
log4j.appender.clsLogAppender.timeFormat=yyyy-MM-dd'T'HH:mm:ssZ
log4j.appender.clsLogAppender.timeZone=UTC
log4j.appender.clsLogAppender.Threshold=INFO
#STDOUT
log4j.appender.STDOUT=org.apache.log4j.ConsoleAppender
log4j.appender.STDOUT.layout=org.apache.log4j.PatternLayout
log4j.appender.STDOUT.layout.ConversionPattern=%-4r [%t] %-5p %c %x - %m%n
上述代码中，clsLogAppender属于开发者需要定义的appender的名称，region，endpoint，secretId，secretKey，topicId这些信息字段都需要从控制台获取，并且这些字段都需要定义在自定的Appender中，方便从log4j.properties文件中获取自定义配置项。

2 安装proto；安装步骤参考

https://cloud.tencent.com/document/product/614/16873#pb-.E7.BC.96.E8.AF.91.E7.A4.BA.E4.BE.8B

3 定义日志BP结构体

package com.github.cls;

message Log
{
    message Content
    {
        required string key   = 1; // 每组字段的 key
        required string value = 2; // 每组字段的 value
    }
    required int64   time     = 1; // 时间戳，UNIX时间格式
    repeated Content contents = 2; // 一条日志里的多个kv组合
}

message LogTag
{
    required string key       = 1;
    required string value     = 2;
}

message LogGroup
{
    repeated Log    logs        = 1; // 多条日志合成的日志数组
    optional string contextFlow = 2; // 目前暂无效用
    optional string filename    = 3; // 日志文件名
    optional string source      = 4; // 日志来源，一般使用机器IP
    repeated LogTag logTags     = 5;
}

message LogGroupList
{
    repeated LogGroup logGroupList = 1; // 日志组列表
}
4 调用protoc --java_out=./ ./cls.proto生成BP结构体

5 将生成的Java文件拷贝到maven工程中（在此示例中，本人的Java结构体名称是Cls）

6 自定义日志上传核心逻辑（继承org.apache.log4j.AppenderSkeleton类）

7 如下实现抽象方法

@Override
protected void append(LoggingEvent event) {
        
        // 1 封装BP结构体
        Cls.LogGroupList logGroupList = Cls.LogGroupList.newBuilder().addLogGroupList(
                Cls.LogGroup.newBuilder().addLogs(
                        Cls.Log.newBuilder()
                                .setTime(event.timeStamp)
                                .addContents(Cls.Log.Content.newBuilder().setKey("exceptionTrace").setValue(String.valueOf(getThrowableStr(event))))
                                .addContents(Cls.Log.Content.newBuilder().setKey("level").setValue(String.valueOf(event.getLevel())))
                                .addContents(Cls.Log.Content.newBuilder().setKey("threadName").setValue(event.getThreadName()))
                                .addContents(Cls.Log.Content.newBuilder().setKey("time").setValue(String.valueOf(event.timeStamp)))
                                .addContents(Cls.Log.Content.newBuilder().setKey("message").setValue(String.valueOf(event.getMessage())))
                )
        ).build();
        
        // 上传BP结构体
        PostStructLogRequest req = client.newPostStructLogRequest(topicId, logGroupList);
        String result = client.send(req);
        
     }
8 编写测试测类

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Log4jAppenderExample {
    private static final Logger LOGGER = LogManager.getLogger(Log4jAppenderExample.class);
    public static void main(String[] args) {
        LOGGER.trace("cls log4j trace log");
        LOGGER.debug("cls log4j debug log");
        LOGGER.info("cls log4j info log");
        LOGGER.warn("cls log4j warn log");
        LOGGER.error("cls log4j error log", new RuntimeException("Runtime Exception"));
    }
}
9 运行测试类

10 登陆CLS控制台切换到日志检索菜单，如下图


CLS控制台
11 如果CLS控制台显示了您在应用程序中打印的日志，大功告成

logback实现日志上报到云端
loback实现步骤和上述一样，不同的是需要开发者在resources目录下定义logback.xml文件中，并且配置Appender的自定义属性，如下demo配置

<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{5} - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="TencentCLS" class="com.github.cls.logback.ClsLogbackAppender">
        <project>smartdoc</project>
        <endpoint>ap-nanjing.cls.tencentcs.com</endpoint>
        <region>ap-nanjing</region>
        <secretId>AKIDQa1B6dIsc8OSntCuVpujKgHi8HAomOIE</secretId>
        <secretKey>vWqtiGadeXCbjcg39yZtwj2EJ7ewZv03</secretKey>
        <topicId>b9ed1869-156a-4eb4-ae96-b0fa292cf5b5</topicId>
        <source>smart-doc</source>
        <timeZone>UTC</timeZone>
        <timeFormat>yyyy-MM-dd'T'HH:mmZ</timeFormat>

        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{5} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="DEBUG">
        <appender-ref ref="STDOUT"/>
        <appender-ref ref="TencentCLS"/>

    </root>
</configuration>
备注： 

com.github.cls.logback.ClsLogbackAppender是笔者自定义实现的logback Appender
需要在appender标签中配置属性
logback日志上报测试类如下
package com.github.cls.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogbackAppenderExample {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogbackAppenderExample.class);

    public static void main(String[] args) {
        LOGGER.trace("cls logback trace log");
        LOGGER.debug("cls logback debug log");
        LOGGER.info("cls logback info log");
        LOGGER.warn("cls logback warn log");
        LOGGER.error("cls logback error log", new RuntimeException("Runtime Exception"));
    }
}
上述代码github地址：https://github.com/Maple-mxf/tencent-cls（仅供参考）

七 总结
上述的日志上报实现方案相对于LogListener来讲，存在性能的差距，因为上述的日志采集方式采用的是同步的方式进行日志上报，LogListener采用监听器的方式，使得生产日志方和消费日志方进行解耦；但LogListener的仅限于CVM机器上日志采集，或者其他的腾讯云容器日志采集，如果开发者的应用在自建机房或者其他云平台上，但日志管理在腾讯云CLS上，则上述实现方案便可以解决这个问题，并且开发者可以定制化逻辑。

原创声明，本文系作者授权云+社区发表，未经许可，不得转载。
## 腾讯云CLS接入log4j/Logback


```
git clone -b main 
```

