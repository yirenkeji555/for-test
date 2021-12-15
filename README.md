### 要求
- 使用spring boot框架
- 在有4个文件，内容都是动态生成的，比如：controller、entity、service、serviceIpm
- 这些文件内容都是用字符串；通过内存编译，让里面的代码能跑起来；
- 因为各个文件直接存在依赖的关系，比如service里面会用到编译好的entity，简单理解就是会引入编译好的 ".class" 文件;

### 遇到的问题
- 开发环境能跑起来，但是打成jar包后，提示确少响应的依赖库；
- 已经定位到相关的问题，现在的做法有下面几种：
    - 把相关的依赖包，都复制到一个固定路径，然后动态编译时，通过 classpath 把相关的文件路径引入；
    - 解压jar后发现，其实相关的依赖包都在，是否可以直接引入jar包内的文件（依赖包）


### 验证
- 在 ScfuncApplication.java 文件中有例子
- java jar xxx.jar 运行验证

### 初始化数据sql
- 修改文件 application.properties 中数据库链接
```sql

DROP TABLE IF EXISTS `foo`;
CREATE TABLE `foo` (
                     `id` varchar(32) CHARACTER SET utf8mb4 NOT NULL,
                     `name` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL,
                     `sex` tinyint(4) DEFAULT NULL,
                     `age` int(11) DEFAULT NULL,
                     `createTime` datetime DEFAULT NULL,
                     `dept_no` varchar(50) DEFAULT NULL,
                     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of foo
-- ----------------------------
BEGIN;
INSERT INTO `foo` VALUES ('1', 'Jack', 1, 20, '2021-11-29 11:57:03', '002');
INSERT INTO `foo` VALUES ('2', 'Hellen', 0, 20, '2021-11-29 11:57:03', '003');
INSERT INTO `foo` VALUES ('3', 'Tom', NULL, 20, '2021-11-29 11:57:03', '003');
COMMIT;



  ```
