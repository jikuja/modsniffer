# modsniffer
## usage
```
$ java -jar build/libs/modsniffer-1.0-SNAPSHOT-all.jar java16-1.1.signature foo.jar > log16
$ java -jar build/libs/modsniffer-1.0-SNAPSHOT-all.jar java17-1.0.signature foo.jar > log17
$ diff -u log17 log16 > log.diff
```

## TODO
1. Add everything under libraries in IGNORED_PACKAGES
2. Find better way to handle rest of the dependensies
3. Test more that diff trick explained in usage
