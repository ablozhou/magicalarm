# 万能闹钟 MagicAlarm
[english version](README_en.md)

一些朋友工作太投入，以致影响身体。所以本工具初衷是提醒站起来走动喝点水。
当然可以用于提醒女朋友生日和办其他重要事情之类。

作者：周海汉 zhouhh <ablozhou@gmail.com>

#### 介绍
万能闹钟
支持crontab方式添加闹钟

#### 环境

系统最低需求java 8

良好体验：java 9 以上

#### 安装教程

mvn package

复制Alarmer.xxx.jar 和 libs 到 bin

Windows执行run.bat
Linux/Mac 执行run.sh

#### 使用说明
解压zip文件
Windows执行run.bat
Linux/Mac 执行run.sh

闹钟配置文件config.txt示例
```
#秒(0-59) 分(0-59) 时(0-23) 日(1-31) 月(0-11) 星期几(1-7,1为周日) 年(可选) 事项
# 0/n 表示从0开始，每隔n个单位。'*' 表示每一个。'?' 表示该栏不需要。日期和星期两个必须有一个是'?',但不能都是'?'
# ';' 用于分隔时间设置和提示信息

0 0/2 * * * ?; 2分钟骚扰一次
0 0/30 8-18 ? * 2-6; 8点到18点，星期一到星期五,每30分钟喝一次水
```
#### 参与贡献

1.  Fork 本仓库: https://github.com/ablozhou/magicalarm.git
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request

#### License
MIT License
