# Magic Alarm
[Chinese version  中文版 ](README_cn.md)
My first target is to alert some hard working person to stand up to move and drink some water.
Also you can use to alert your girl friend's birthday or other important things.

Author: 周海汉(zhouhh) <ablozhou@gmail.com>

#### Description
Magic Alarm(万能闹钟), an alarm tool support Windows,Mac,Linux OS.
It can config like crontab.

#### Env
java 1.8 +

perfect java 1.9

#### Installation

from source:
```
mvn package
```
copy Alarmer.xxx.jar and libs to bin/.

on Windows run:
```
run.bat
```
on Mac/Linux run:
```
run.sh
```


#### Instructions

config.txt example
```
# second (0-59) minute(0-59) hour(0-23) date of month(1-31) month(0-11) day of week(1-7,1 for sunday) year(opt) 
# 0/n every n unit.'*' : every unit. '?' optional. date and day must one be '?', but not same '?'.
# ';' split a alarm note message.

0 0/2 * * * ?;  every 2 minute alarm.
0 0/30 8-18 ? * 2-6; working day every 30 minutes , alarm  drinking water
```

#### Contribution

1.  Fork the repository from https://gitee.com/ablozhou/magicalarm.git
2.  Create Feat_xxx branch
3.  Commit your code
4.  Create Pull Request

#### License
MIT License