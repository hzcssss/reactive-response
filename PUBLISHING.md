# 发布指南

本文档提供了将 Reactive Response 库发布到 Maven 中央仓库的详细步骤。

## 前提条件

1. 拥有 Sonatype OSSRH 账号（https://oss.sonatype.org/）
2. 配置好 GPG 密钥
3. 在 `~/.m2/settings.xml` 中配置 Sonatype 凭据

## settings.xml 配置示例

```xml
<settings>
  <servers>
    <server>
      <id>central</id>
      <username>您的Sonatype用户名</username>
      <password>您的Sonatype密码</password>
    </server>
  </servers>
  
  <!-- 可选：配置GPG密钥信息 -->
  <profiles>
    <profile>
      <id>ossrh</id>
      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>
      <properties>
        <gpg.keyname>您的GPG密钥ID</gpg.keyname>
        <!-- 不推荐在配置文件中存储密码 -->
        <!-- <gpg.passphrase>您的GPG密钥密码</gpg.passphrase> -->
      </properties>
    </profile>
  </profiles>
</settings>
```

## GPG 密钥配置

1. 安装 GPG
   - macOS: `brew install gnupg`
   - Ubuntu: `sudo apt-get install gnupg`
   - Windows: 下载并安装 Gpg4win (https://www.gpg4win.org/)

   **注意**: 确保安装后 `gpg` 命令可用。在某些系统中，可能需要将 GPG 的安装目录添加到系统 PATH 环境变量中。

2. 生成密钥
   ```
   gpg --gen-key
   ```

3. 查看密钥
   ```
   gpg --list-keys
   ```

4. 查看密钥ID
   ```
   gpg --list-keys --keyid-format LONG
   ```
   
   输出示例:
   ```
   pub   rsa4096/ABCDEF1234567890 2025-09-01 [SC] [有效至：2027-09-01]
         1234567890ABCDEF1234567890ABCDEF12345678
   uid                 [ultimate] 您的名字 <your.email@example.com>
   sub   rsa4096/1234ABCD5678EFGH 2025-09-01 [E] [有效至：2027-09-01]
   ```
   
   在这个例子中，`ABCDEF1234567890`就是您的密钥ID。

5. 发布密钥到公钥服务器
   ```
   gpg --keyserver keyserver.ubuntu.com --send-keys 您的密钥ID
   ```
   
   如果遇到权限问题，可以尝试以下解决方案：
   
   a. 修复 .gnupg 目录的所有权和权限：
   ```bash
   # 确保 .gnupg 目录及其内容归属于当前用户
   sudo chown -R $(whoami) ~/.gnupg
   # 设置正确的权限
   chmod 700 ~/.gnupg
   chmod 600 ~/.gnupg/*
   ```
   
   b. 尝试其他公钥服务器：
   ```
   gpg --keyserver keys.openpgp.org --send-keys 您的密钥ID
   # 或
   gpg --keyserver pgp.mit.edu --send-keys 您的密钥ID
   ```
   
   c. 如果仍然遇到问题，可以尝试使用 hkp 协议：
   ```
   gpg --keyserver hkp://keyserver.ubuntu.com:80 --send-keys 您的密钥ID
   ```
   
   d. 检查防火墙设置，确保允许 GPG 进行网络连接
   
   e. 如果所有方法都失败，可以导出公钥并手动上传到 Sonatype OSSRH：
   ```
   gpg --export --armor 您的密钥ID > public-key.asc
   ```
   然后登录 Sonatype OSSRH 并在用户配置中上传此文件。

## 发布流程

### 准备发布

1. 确保版本号正确（在 pom.xml 中）
2. 确保所有测试通过
   ```
   mvn clean test
   ```

### 执行发布

项目现在有两个 profile：
- `dev` - 默认激活，用于本地开发和测试，跳过 GPG 签名
- `wYOw2G` - 包含 GPG 签名配置和发布配置，用于发布到 Maven 中央仓库

#### 本地构建和测试

```bash
mvn clean install
```

#### 发布到 Maven 中央仓库

```bash
mvn clean deploy -P wYOw2G
```

**注意**：pom.xml 中已经配置了您的 GPG 密钥 ID (6715FFDA5B7AE065)，如果您使用不同的密钥，请修改 pom.xml 中的 `<keyname>` 标签。

### 常见问题

1. **GPG 命令不可用**

   错误信息：`Cannot run program "gpg": error=2, No such file or directory`
   
   解决方案：
   - 确保已安装 GPG（参见上面的安装说明）
   - 确保 GPG 命令在系统 PATH 中

2. **找不到密钥**

   错误信息：`gpg: no default secret key: No secret key`
   
   解决方案：
   - 确保已生成 GPG 密钥，并使用 `gpg --list-secret-keys` 验证
   - 检查 pom.xml 中的 `<keyname>` 标签是否与您的密钥 ID 匹配

3. **缺少签名文件**

   错误信息：`Missing signature for file: xxx.jar`
   
   解决方案：
   - 确保使用了 `wYOw2G` profile
   - 确保 GPG 密钥配置正确
   - 尝试手动为构件生成签名：
   
   ```bash
   cd target
   gpg --armor --detach-sign -u 您的密钥ID reactive-response-builder-1.0.0.jar
   gpg --armor --detach-sign -u 您的密钥ID reactive-response-builder-1.0.0-sources.jar
   gpg --armor --detach-sign -u 您的密钥ID reactive-response-builder-1.0.0-javadoc.jar
   cd ..
   gpg --armor --detach-sign -u 您的密钥ID pom.xml
   ```

4. **"Inappropriate ioctl for device" 错误**

   错误信息：`gpg: 签名时失败： Inappropriate ioctl for device` 或 `gpg: signing failed: Inappropriate ioctl for device`
   
   解决方案：
   
   a. 在 pom.xml 中添加 `--pinentry-mode loopback` 参数（已添加）：
   ```xml
   <gpgArguments>
       <arg>--pinentry-mode</arg>
       <arg>loopback</arg>
   </gpgArguments>
   ```
   
   b. 设置 GPG_TTY 环境变量：
   ```bash
   export GPG_TTY=$(tty)
   ```
   
   c. 配置 GPG 使用 loopback 模式，编辑 ~/.gnupg/gpg.conf 文件：
   ```bash
   echo "pinentry-mode loopback" >> ~/.gnupg/gpg.conf
   ```
   
   d. 如果使用的是 macOS，可能需要安装并配置 pinentry-mac：
   ```bash
   brew install pinentry-mac
   echo "pinentry-program /usr/local/bin/pinentry-mac" >> ~/.gnupg/gpg-agent.conf
   gpgconf --kill gpg-agent
   ```
   
   e. 在命令行中提供密码：
   ```bash
   mvn clean deploy -P wYOw2G -Dgpg.passphrase=您的密钥密码
   ```

5. **IntelliJ IDEA 内置 Maven 问题**

   如果您使用的是 IntelliJ IDEA 内置的 Maven，可能会遇到 GPG 无法正常工作的问题，因为内置 Maven 可能无法正确访问 GPG 配置。
   
   解决方案：
   
   a. 在 IDEA 中配置外部 Maven：
   - 下载 Maven 二进制包：从 https://maven.apache.org/download.cgi 下载最新版本
   - 解压到您选择的目录，例如 `/Users/您的用户名/tools/apache-maven-3.9.6`
   - 在 IDEA 中，转到 Preferences > Build, Execution, Deployment > Build Tools > Maven
   - 将 Maven home path 设置为您解压的 Maven 路径
   - 应用更改并重启 IDEA
   
   b. 直接在 pom.xml 中配置 GPG 密码（仅用于本地开发，不要提交到版本控制）：
   ```xml
   <profile>
     <id>wYOw2G</id>
     <build>
       <plugins>
         <plugin>
           <groupId>org.apache.maven.plugins</groupId>
           <artifactId>maven-gpg-plugin</artifactId>
           <configuration>
             <!-- 其他配置 -->
             <passphrase>您的GPG密钥密码</passphrase>
           </configuration>
         </plugin>
       </plugins>
     </build>
   </profile>
   ```
   
   c. 使用命令行参数传递密码：
   - 打开 IDEA 的 Maven 工具窗口
   - 右键点击项目
   - 选择 "Create 'xxx'" > "Run Maven Build"
   - 在命令行参数中添加 `-P wYOw2G -Dgpg.passphrase=您的密钥密码`
   - 点击运行
   
   d. 创建 Maven 配置文件：
   - 在 `~/.m2/settings.xml` 中添加以下配置：
   ```xml
   <settings>
     <!-- 其他配置 -->
     <profiles>
       <profile>
         <id>gpg</id>
         <properties>
           <gpg.passphrase>您的GPG密钥密码</gpg.passphrase>
         </properties>
       </profile>
     </profiles>
     <activeProfiles>
       <activeProfile>gpg</activeProfile>
     </activeProfiles>
   </settings>
   ```

6. **密码问题**

   如果不想每次都输入密码，可以使用以下方法：
   
   a. 在 Maven 配置文件中存储密码（不推荐用于生产环境）：
   ```xml
   <!-- 在 ~/.m2/settings.xml 中 -->
   <settings>
     <profiles>
       <profile>
         <id>gpg</id>
         <properties>
           <gpg.passphrase>您的GPG密钥密码</gpg.passphrase>
         </properties>
       </profile>
     </profiles>
     <activeProfiles>
       <activeProfile>gpg</activeProfile>
     </activeProfiles>
   </settings>
   ```
   
   b. 使用 Maven 密码加密功能：
   ```bash
   mvn --encrypt-password 您的GPG密钥密码
   ```
   
   然后将加密后的密码放入 settings.xml：
   ```xml
   <settings>
     <profiles>
       <profile>
         <id>gpg</id>
         <properties>
           <gpg.passphrase>{加密后的密码}</gpg.passphrase>
         </properties>
       </profile>
     </profiles>
   </settings>
   ```
   
   c. 在 IDEA 的 Maven 运行配置中添加命令行参数：
   `-Dgpg.passphrase=您的密钥密码`

## 验证发布

发布后，可以在以下位置查看您的构件：

1. Sonatype OSSRH: https://oss.sonatype.org/
2. Maven 中央仓库（同步后）: https://search.maven.org/

## 使用示例

发布成功后，用户可以通过以下方式引入您的库：

### Maven

```xml
<dependency>
  <groupId>io.github.hzcssss</groupId>
  <artifactId>reactive-response-builder</artifactId>
  <version>1.0.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'io.github.hzcssss:reactive-response-builder:1.0.0'