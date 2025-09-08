Mall Spring - 电商平台后端系统
📖 项目简介
Mall Spring 是一个基于 Spring Boot 3.4.8 构建的现代化电商平台后端系统，提供完整的电商业务功能支持。项目采用微服务架构设计，支持普通用户购物、商家入驻销售和管理员后台管理等多种角色场景。

🎯 核心特性
🔐 安全认证：基于 JWT + Spring Security 的无状态认证

🛒 完整电商流程：商品浏览、购物车、订单管理、支付处理

💬 实时通信：WebSocket 实现即时消息推送

👥 多角色支持：用户、商家、管理员权限管理

📱 社交功能：商品评论、用户关注、社区互动

📦 文件管理：图片上传、多媒体内容支持

🏪 商家中心：店铺管理、商家认证、订单处理

🔧 开发工具：密码迁移、数据初始化等实用工具

🏗️ 技术架构
技术栈
技术

版本

用途

Java

17

开发语言

Spring Boot

3.4.8

核心框架

Spring Security

6.x

安全框架

MyBatis

3.0.4

ORM 框架

MySQL

8.x

数据库

JWT

0.11.5

身份认证

PageHelper

1.4.6

分页插件

WebSocket

-

实时通信

Maven

3.x

构建工具

架构设计
mall_spring/
├── src/main/java/com/coding24h/mall_spring/
│ ├── config/ # 配置类
│ ├── controller/ # 控制器层
│ ├── dto/ # 数据传输对象
│ ├── entity/ # 实体类
│ ├── exception/ # 异常处理
│ ├── jwt/ # JWT 认证
│ ├── mapper/ # 数据访问层
│ ├── service/ # 业务逻辑层
│ └── util/ # 工具类
└── src/main/resources/
├── mapper/ # MyBatis 映射文件
└── application.properties

🚀 快速开始
环境要求
JDK 17 或更高版本

Maven 3.6+

MySQL 8.0+

IDE：推荐 IntelliJ IDEA 或 Eclipse

安装步骤
克隆项目

git clone https://github.com/hjhashd/mall_spring.git
cd mall_spring

数据库配置

-- 创建数据库
CREATE DATABASE mall_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

修改配置
编辑 src/main/resources/application.properties：

# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/mall_db
spring.datasource.username=your_username
spring.datasource.password=your_password

# 文件上传路径（根据实际环境调整）
file.upload-dir=/path/to/uploads
file.base-url=http://localhost:8080/uploads

# 密码迁移工具配置（生产环境设为false）
app.password-migration.enabled=false

构建项目

mvn clean package

运行应用

# 开发环境
mvn spring-boot:run

# 或使用 jar 包
java -jar target/mall_spring-0.0.1-SNAPSHOT.jar

验证启动
访问 http://localhost:8080 确认服务正常启动。

📊 核心功能模块
用户认证 & 权限管理

用户注册、登录、退出

JWT Token 认证

角色权限控制（用户/商家/管理员）

密码加密存储

密码强度验证

商品管理

商品信息 CRUD

商品分类管理

商品搜索 & 筛选

商品图片上传

库存管理

购物车 & 订单

购物车操作（增删改查）

订单创建 & 管理

多商家订单支持

订单状态跟踪

支付处理

商家中心

商家入驻 & 认证

店铺设置管理

商家商品管理

订单处理 & 发货

销售数据统计

社交功能

商品评论 & 回复

用户关注系统

社区互动

消息通知

实时通信

WebSocket 消息推送

在线客服聊天

系统通知

后台管理

用户管理

商品审核

订单监控

数据统计

🔧 开发工具 & 实用功能
密码迁移工具
项目内置了安全的密码迁移工具 PasswordMigrationTool，用于将明文密码批量加密为 BCrypt 格式。

功能特性

✅ 事务安全：使用事务确保数据一致性

✅ 弱密码检测：自动识别和记录弱密码用户

✅ 智能跳过：自动跳过已加密的密码

✅ 详细报告：提供完整的迁移统计和错误日志

✅ 安全控制：通过配置文件控制是否启用

使用方法

在 application.properties 中启用

app.password-migration.enabled=true

运行迁移（工具会在应用启动时自动执行）

mvn spring-boot:run

迁移报告示例

=== 密码迁移报告 ===
总用户数: 150
成功迁移: 120
跳过用户: 25
失败数量: 5

⚠️  发现弱密码用户 (15个):
 - user123: 密码长度小于6位 (长度: 4)
 - admin: 匹配弱密码模式 (长度: 5)
建议通知这些用户修改密码!

密码强度验证器
内置 PasswordStrengthValidator 工具类，支持：

长度检查（推荐8位以上）

复杂度评估（大小写、数字、特殊字符）

常见弱密码检测

强度评分（0-5分）

改进建议提供

数据库工具
MyBatis 代码生成器：自动生成 Mapper 和 XML 文件

数据库 Schema 同步：支持版本化数据库结构管理

测试数据生成器：快速生成测试用的商品、用户数据

🔧 API 接口文档
认证接口

POST /api/auth/register：用户注册

POST /api/auth/login：用户登录（包含密码强度信息）

POST /api/auth/logout：用户登出

商品接口

GET /api/products：获取商品列表

GET /api/products/{id}：获取商品详情

POST /api/products：创建商品（商家）

PUT /api/products/{id}：更新商品

DELETE /api/products/{id}：删除商品

购物车接口

GET /api/cart：获取购物车

POST /api/cart/add：添加到购物车

PUT /api/cart/update：更新购物车

DELETE /api/cart/remove：移除商品

订单接口

GET /api/orders：获取订单列表

POST /api/orders：创建订单

GET /api/orders/{id}：获取订单详情

PUT /api/orders/{id}：更新订单状态

完整的 API 文档请参考项目中的 Postman 集合或 Swagger 文档。

📁 项目结构详解
核心包说明
config/：Spring 配置类，包括安全配置、跨域配置等

controller/：REST API 控制器，处理 HTTP 请求

service/：业务逻辑层，实现核心业务功能

mapper/：数据访问层，MyBatis 映射接口

entity/：实体类，对应数据库表结构

dto/：数据传输对象，用于 API 请求响应

jwt/：JWT 认证相关工具类

exception/：自定义异常处理

util/：工具类集合

PasswordMigrationTool - 密码迁移工具

PasswordStrengthValidator - 密码强度验证器

JsonMapTypeHandler - JSON 类型处理器

FileUtil - 文件操作工具

配置文件说明
application.properties：主配置文件

mapper/*.xml：MyBatis SQL 映射文件

🛠️ 开发指南
代码规范
使用 camelCase 命名变量和方法

类名使用 PascalCase

常量使用 UPPER_SNAKE_CASE

必要时添加注释说明复杂逻辑

安全开发规范
所有密码使用 BCrypt 加密

登录时进行密码强度校验

敏感接口需要 JWT 认证

输入参数进行校验和过滤

遵循最小权限原则

新增功能
在对应的 entity 包中创建实体类

在 mapper 包中创建数据访问接口

在 resources/mapper 中创建 XML 映射文件

在 service 包中实现业务逻辑

在 controller 包中创建 REST 接口

添加对应的 DTO 类用于数据传输

工具开发指南
项目提供了丰富的开发工具模板：

密码工具：参考 PasswordMigrationTool 和 PasswordStrengthValidator

数据迁移：可扩展迁移工具支持其他数据转换

批量操作：支持事务安全的批量数据处理

报告生成：内置详细的操作报告和统计功能

🧪 测试
运行单元测试

mvn test

运行特定测试类

mvn test -Dtest=UserServiceTest

生成测试报告

mvn surefire-report:report

测试密码迁移工具（在测试环境）

mvn spring-boot:run -Dspring.profiles.active=test -Dapp.password-migration.enabled=true

📦 部署
开发环境
mvn spring-boot:run

生产环境部署
构建 JAR 包

mvn clean package -Dmaven.test.skip=true

生产环境运行（关闭迁移工具）

java -jar -Dspring.profiles.active=prod -Dapp.password-migration.enabled=false target/mall_spring-0.0.1-SNAPSHOT.jar

Docker 部署
FROM openjdk:17-jdk-slim
COPY target/mall_spring-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENV APP_PASSWORD_MIGRATION_ENABLED=false
ENTRYPOINT ["java", "-jar", "/app.jar"]

生产环境安全注意事项
确保 app.password-migration.enabled=false

使用强密码连接数据库

定期更新 JWT 密钥

配置 HTTPS 证书

启用访问日志监控

🔒 安全特性
认证与授权
JWT 无状态认证：支持分布式部署

角色权限控制：精细化权限管理

密码安全策略：BCrypt 加密 + 强度验证

会话管理：支持记住我功能

密码安全
强制加密存储：所有密码 BCrypt 加密

强度实时检测：登录时提供密码强度评估

弱密码预警：自动识别并通知弱密码用户

安全迁移工具：支持历史数据安全升级

数据保护
输入验证：防止 SQL 注入和 XSS 攻击

文件上传安全：类型和大小限制

敏感信息脱敏：日志中不显示敏感数据

🤝 贡献指南
Fork 本仓库
创建特性分支：git checkout -b feature/amazing-feature

提交更改：git commit -m 'Add some amazing feature'

推送分支：git push origin feature/amazing-feature

提交 Pull Request

代码贡献规范
遵循现有代码风格

添加必要的单元测试

更新相关文档

确保所有测试通过

📋 路线图
已完成 ✅
用户认证与权限管理

商品管理基础功能

购物车与订单系统

WebSocket 实时通信

密码安全迁移工具

密码强度验证器

开发中 🚧
支付系统集成

物流跟踪功能

商家数据统计

系统监控面板

计划中 📅
缓存机制（Redis）

接口文档（Swagger）

单元测试覆盖率提升

容器化部署优化

微服务拆分

待优化 🔧
统一分页组件（目前同时使用 PageHelper 和 JPA）

数据库连接池优化

异步任务处理

文件存储云端化

📞 联系方式
项目作者：LUO

邮箱：woqu_0716@qq.com

项目地址：https://github.com/hjhashd/mall_spring.git
