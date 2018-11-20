#### Introduction
The project based on maven+jdk1.8 <br/>
采用机制-策略设计模式<br/>
本项目根据配置文件对word文本进行填充，支持docx文件<br/>
允许的填充类型包括字符串、word表格、echart图表。<br/>
字符串配置：<br/>
{{people_num;sql=select count(counter) from nil_analysis_age_5min where area_id=#{area_id};}}<br/>
word表格配置：<br/>
{{#table;sql=select name,statistics_time,counter from nil_heatmap_area a left join nil_analysis_age_5min b on a.id = b.area_id ORDER BY name,statistics_time;col=name;row=statistics_time;value=counter}} <br/>
echart图片配置：<br/>
{{@image;sql=select name,statistics_time,counter from nil_heatmap_area a left join nil_analysis_age_5min b on a.id = b.area_id ORDER BY name,statistics_time;col=name;row=statistics_time;value=counter;type=bar}} <br/>

流程原理：读取word中的配置文件，填充传入的参数，查询数据库，最后根据待填充的数据类型将原始数据转换成不同的需要数据，再调用poi-tl对文件进行填充<br/>
## 项目模块

####cn.core.gnerator
WordGenerator：generator方法是程序对外提供的唯一调用接口，包含生成word文件的核心机制<br/>
WordResolver：解析word文件中的配置文件解析器<br/>
ConfigExcutor：包含执行配置文件，获取数据的核心机制<br/>
DataResolver：真正获取数据，解析数据，数据类型转换的解析器<br/>

####cn.jarvan.dao
sqlAutoMapper:mybatis操作数据库的工具类<br/>

####cn.jarvan.enums
DataType:数据类型枚举类<br/>

