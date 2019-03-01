## Introduction
The project based on maven+jdk1.8 <br/>
1、本项目分为word、excel两个模块。
2、word模块可根据自定义的模板自动生成最终的填充后文件(本模块流程较为复杂)，模板中可包含sql语句，最终会根据查出的数据进行替换，替换的内容可以是文本，表格，echart图表。
3、excel模块可根据数据生成excel报表

## 设计模式
策略模式<br/>
单例模式<br/>
构建者模式<br/>

## 用到的技术
poi、文件读写、泛型、反射、自定义注解、枚举、mybatis<br/>

## word模板配置
本项目目前支持：<br/>
1、直接文本替换<br/>
2、sql文本替换<br/>
3、直接表格替换<br/>

预留功能
1、sql表格替换<br/>
2、sql echart替换<br/>
这两个策略暂时没有实现，如果你想要支持这两个功能，可自行实现DataResolver的相应方法
相关的word模板如何配置见resource/template.docx

## word流程原理
读取word中的配置文件，填充传入的参数，查询数据库，最后根据待填充的数据类型将原始数据转换成不同的需要数据，再调用poi-tl对文件进行填充<br/>
## 项目模块

#### cn.core.gnerator.word
WordGenerator：generator方法是程序对外提供的唯一调用接口，包含生成word文件的核心机制<br/>
WordResolver：解析word文件中的配置文件解析器<br/>
ConfigExcutor：包含执行配置文件，获取数据的核心机制<br/>
DataResolver：真正获取数据，解析数据，数据类型转换的解析器<br/>

#### cn.core.gnerator.excel
ExcelGenerator：对外提供两个接口generator和generatorByList<br/>

#### cn.jarvan.dao
sqlAutoMapper:mybatis操作数据库的工具类<br/>

#### cn.jarvan.enums
DataType:数据类型枚举类<br/>

## 项目部署
1、下载项目<br/>
2、修改resources/cache.properties的中间文档存放目录<br/>
3、创建数据库，执行nil_cell_subscriber_province.sql（pgsql）<br/>
4、修改resources/database.properties的数据库连接信息<br/>
5、执行test包中的client.java完成客户端调用

## 项目不足
由于一开始设计时出现纰漏，将sql直接配置在word文本中，在实际生产环境使用时发现有些sql及其复杂，冗长，<br/>
导致word配置文件十分繁杂.一开始应该在word中只填写填充符，sql用properties文件单独配置
