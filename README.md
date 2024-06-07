# font2svg

Convert Font To Svg.

Inspire by <https://www.bilibili.com/read/cv26464950/>

# Screen Recording

![](./screen-recording.gif)

# Usage

build jar

```bash
bash ./build.sh
```

execute jar

```bash
java -Dfont2svg-config-path=path/to/config.yaml -jar ./font2svg-server/build/libs/font2svg-server-1.0.0-SNAPSHOT-fat.jar
```

visit <http://127.0.0.1:8888/index.html>

# Benchmark

load testing by [k6](https://k6.io/).

benckmark script is in `./benchmark/rondam-svg-access.js`.

**Dynamic Font Svg Generate API QPS: 40K**

![performance screenshot](./benchmark/perf.png)


[benchmakr result](./benchmark/html-report.html)

## Ref

- https://juejin.cn/post/6971673576017494053
