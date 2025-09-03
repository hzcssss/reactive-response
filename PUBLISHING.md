# 发布指南

本文档提供了将 Reactive Response 库发布到 Maven 中央仓库的详细步骤。

## 前提条件

1. 拥有 Maven Central Repository 账号
   - 注册地址：https://central.sonatype.com/
   - 创建并验证命名空间（namespace）
   - 参考文档：https://central.sonatype.org/publish/generate-portal-token/

2. 安装 GPG 工具
   - 下载地址：https://gnupg.org/download/index.html#sec-1-2
   - 生成密钥并发布到公钥服务器（详见下文）

3. 在 `~/.m2/settings.xml` 中配置 Maven Central 凭据

## Maven Central Repository 配置

### 创建和验证命名空间

1. 访问 Maven Central Repository：https://central.sonatype.com/
2. 注册并登录您的账号
3. 创建一个新的命名空间（通常是您的域名反转，如 `io.github.username`）
4. 验证您对该命名空间的所有权（通常需要验证域名或 GitHub 账号）
5. 等待审核通过（通常需要几个小时到几天）

### 生成访问令牌

1. 登录 Maven Central Repository 门户：https://central.sonatype.com/
2. 导航到用户设置页面
3. 生成新的访问令牌（Access Token）
4. 保存生成的用户名和密码（实际上是令牌）

### settings.xml 配置示例

在 `~/.m2/settings.xml` 中添加以下配置：

```xml
<settings>
  <servers>
    <server>
      <id>central</id>
      <!-- 使用生成的令牌用户名 -->
      <username>您的令牌用户名</username>
      <!-- 使用生成的令牌密码 -->
      <password>您的令牌密码</password>
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
        <gpg.keyname>您的GPG密钥ID（使用后八位）</gpg.keyname>
        <!-- 不推荐在配置文件中存储密码 -->
        <!-- <gpg.passphrase>您的GPG密钥密码</gpg.passphrase> -->
      </properties>
    </profile>
  </profiles>
</settings>
```

### pom.xml 配置示例

在项目的 `pom.xml` 文件中添加以下配置，以支持发布到 Maven 中央仓库：

```xml
<project>
  <!-- 基本项目信息 -->
  <groupId>io.github.yourusername</groupId>
  <artifactId>your-project</artifactId>
  <version>1.0.0</version>
  <packaging>jar</packaging>
  
  <!-- 项目元数据（必需） -->
  <name>项目名称</name>
  <description>项目描述</description>
  <url>https://github.com/yourusername/your-project</url>
  
  <!-- 许可证信息（必需） -->
  <licenses>
    <license>
      <name>MIT License</name>
      <url>http://www.opensource.org/licenses/mit-license.php</url>
    </license>
  </licenses>
  
  <!-- 开发者信息（必需） -->
  <developers>
    <developer>
      <name>您的姓名</name>
      <email>您的邮箱</email>
      <organization>您的组织</organization>
      <organizationUrl>https://github.com/yourusername</organizationUrl>
    </developer>
  </developers>
  
  <!-- SCM信息（必需） -->
  <scm>
    <connection>scm:git:git://github.com/yourusername/your-project.git</connection>
    <developerConnection>scm:git:ssh://github.com:yourusername/your-project.git</developerConnection>
    <url>https://github.com/yourusername/your-project/tree/main</url>
  </scm>
  
  <!-- 构建配置 -->
  <build>
    <plugins>
      <!-- 编译插件 -->
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.11.0</version>
        <configuration>
          <source>11</source>
          <target>11</target>
        </configuration>
      </plugin>
      
      <!-- 源码插件（必需） -->
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-source-plugin</artifactId>
        <version>3.3.0</version>
        <executions>
          <execution>
            <id>attach-sources</id>
            <goals>
              <goal>jar-no-fork</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
      
      <!-- JavaDoc插件（必需） -->
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-javadoc-plugin</artifactId>
        <version>3.5.0</version>
        <executions>
          <execution>
            <id>attach-javadocs</id>
            <goals>
              <goal>jar</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
  
  <!-- 发布配置（使用profile） -->
  <profiles>
    <profile>
      <id>release</id>
      <build>
        <plugins>
          <!-- GPG签名插件 -->
          <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-gpg-plugin</artifactId>
            <version>3.1.0</version>
            <executions>
              <execution>
                <id>sign-artifacts</id>
                <phase>verify</phase>
                <goals>
                  <goal>sign</goal>
                </goals>
              </execution>
            </executions>
          </plugin>
          
          <!-- 中央仓库发布插件 -->
          <plugin>
            <groupId>org.sonatype.central</groupId>
            <artifactId>central-publishing-maven-plugin</artifactId>
            <version>0.8.0</version>
            <extensions>true</extensions>
            <configuration>
              <publishingServerId>central</publishingServerId>
            </configuration>
          </plugin>
        </plugins>
      </build>
    </profile>
  </profiles>
</project>
```

这个配置包含了发布到 Maven 中央仓库所需的所有必要元素：

1. **基本项目信息**：groupId、artifactId 和 version
2. **项目元数据**：名称、描述和 URL
3. **许可证信息**：至少需要一个许可证
4. **开发者信息**：至少需要一个开发者
5. **SCM 信息**：源代码管理信息
6. **构建插件**：
   - maven-compiler-plugin：编译源代码
   - maven-source-plugin：生成源码 JAR（必需）
   - maven-javadoc-plugin：生成 JavaDoc JAR（必需）
7. **发布配置**：
   - maven-gpg-plugin：对构件进行 GPG 签名
   - central-publishing-maven-plugin：发布到 Maven 中央仓库

## GPG 密钥配置

GPG（GNU Privacy Guard）是一个加密软件，用于对 Maven 构件进行签名，这是发布到 Maven 中央仓库的必要条件。

### 1. 安装 GPG

建议通过官方网站下载并安装 GPG：https://gnupg.org/download/index.html#sec-1-2

- **Windows**:
  - 下载并安装 Gpg4win

- **macOS**:
  - 下载并安装 GPG Suite
  - 注意：如果使用 Homebrew 安装的 GPG 无法发布密钥到公钥服务器，建议通过官方网站下载安装可视化工具进行操作

- **Linux**:
  - 下载并安装官方提供的二进制包
  - 或使用包管理器：Ubuntu/Debian: `sudo apt-get install gnupg`，Fedora/RHEL: `sudo dnf install gnupg`

**注意**: 安装后，确保 `gpg` 命令可用。在某些系统中，可能需要将 GPG 的安装目录添加到系统 PATH 环境变量中。

### 2. 生成 GPG 密钥对

```bash
gpg --full-generate-key
```

推荐设置:
- 密钥类型: RSA and RSA (默认)
- 密钥长度: 4096 位
- 有效期: 根据需要设置（建议至少 2 年）
- 输入您的个人信息（姓名、电子邮件等）
- 设置一个安全的密码

### 3. 查看生成的密钥

```bash
gpg --list-keys
```

### 4. 查看密钥 ID

```bash
gpg --list-keys --keyid-format LONG
```

输出示例:
```
pub   rsa4096/ABCDEF1234567890 2025-09-01 [SC] [有效至：2027-09-01]
      1234567890ABCDEF1234567890ABCDEF12345678
uid                 [ultimate] 您的名字 <your.email@example.com>
sub   rsa4096/1234ABCD5678EFGH 2025-09-01 [E] [有效至：2027-09-01]
```

在这个例子中，`ABCDEF1234567890` 就是您的密钥 ID。

### 5. 发布密钥到公钥服务器

Maven Central 要求您的 GPG 密钥必须发布到公共密钥服务器。

```bash
gpg --keyserver keyserver.ubuntu.com --send-keys 您的密钥ID
```

建议同时发布到多个服务器以确保可用性:

```bash
gpg --keyserver keys.openpgp.org --send-keys 您的密钥ID
gpg --keyserver pgp.mit.edu --send-keys 您的密钥ID
gpg --keyserver keyserver.ubuntu.com --send-keys 您的密钥ID
```

### 常见问题及解决方案

1. **权限问题**:
   ```bash
   # 修复 .gnupg 目录的所有权和权限
   sudo chown -R $(whoami) ~/.gnupg
   chmod 700 ~/.gnupg
   chmod 600 ~/.gnupg/*
   ```

2. **连接问题**:
   ```bash
   # 使用 hkp 协议
   gpg --keyserver hkp://keyserver.ubuntu.com:80 --send-keys 您的密钥ID
   ```

3. **防火墙问题**:
   - 检查防火墙设置，确保允许 GPG 进行网络连接
   - 如果在公司网络环境中，可能需要联系 IT 部门

4. **手动上传**:
   如果所有自动方法都失败，可以导出公钥并手动上传:
   ```bash
   gpg --export --armor 您的密钥ID > public-key.asc
   ```
   然后登录 Maven Central 并在用户配置中上传此文件。

5. **验证密钥是否已发布**:
   ```bash
   gpg --keyserver keyserver.ubuntu.com --recv-keys 您的密钥ID
   ```

## 发布流程

### 1. 准备发布

在发布之前，请确保完成以下准备工作：

1. **检查版本号**
   - 在 `pom.xml` 中确认版本号是否正确
   - 遵循语义化版本规范 (Semantic Versioning)：主版本.次版本.修订号
   - 对于新功能，增加次版本号；对于 bug 修复，增加修订号；对于不兼容的 API 更改，增加主版本号

2. **确保所有测试通过**
   ```bash
   mvn clean test
   ```

3. **检查项目文档**
   - README.md 是否包含最新信息
   - JavaDoc 是否完整且无错误

4. **检查 pom.xml 配置**
   - groupId、artifactId 和 version 是否正确
   - 所有必要的元数据是否完整（名称、描述、URL、许可证、开发者信息等）
   - SCM 信息是否正确

### 2. 构建和本地测试

在发布到 Maven 中央仓库之前，建议先在本地构建并测试：

```bash
mvn clean install
```

这将在本地 Maven 仓库中安装项目，您可以在其他项目中引用它进行测试。

### 3. 执行发布

项目配置了两个 Maven profile：

- `dev` - 默认激活，用于本地开发和测试，跳过 GPG 签名
- `release` - 包含 GPG 签名配置和发布配置，用于发布到 Maven 中央仓库

发布到 Maven 中央仓库：

```bash
mvn clean deploy -P release
```

如果需要提供 GPG 密码：

```bash
mvn clean deploy -P release -Dgpg.passphrase=您的GPG密钥密码
```

### 4. 发布后的验证

发布完成后，您可以在以下位置验证您的构件：

1. **Maven Central Repository**
   - 访问 https://central.sonatype.com/
   - 搜索您的 groupId 或 artifactId

2. **Maven 搜索引擎**
   - 访问 https://search.maven.org/
   - 搜索您的 groupId 或 artifactId

注意：新发布的构件可能需要几个小时才能在 Maven 中央仓库中可见。

## 常见问题与解决方案

### 1. GPG 相关问题

#### GPG 命令不可用

**错误信息**：
```
Cannot run program "gpg": error=2, No such file or directory
```

**解决方案**：
- 确保已正确安装 GPG（参见上面的安装说明）
- 确保 GPG 命令在系统 PATH 中
- 在 Windows 上，可能需要重启终端或系统
- 使用 `which gpg`（Unix/Linux/macOS）或 `where gpg`（Windows）确认 GPG 的安装位置

#### 找不到密钥

**错误信息**：
```
gpg: no default secret key: No secret key
```

**解决方案**：
- 确保已生成 GPG 密钥，并使用 `gpg --list-secret-keys` 验证
- 检查 pom.xml 中的 `<keyname>` 标签是否与您的密钥 ID 匹配
- 如果您有多个密钥，请明确指定要使用的密钥 ID


### 2. Maven 相关问题

#### 401 Unauthorized 错误

**错误信息**：
```
401 Unauthorized
```

**解决方案**：
- 检查 settings.xml 中的凭据是否正确
- 确认您的 Maven Central 账号有权限发布到指定的 groupId
- 检查您的访问令牌是否过期（通常需要定期更新）

#### 依赖冲突

**错误信息**：
```
Dependency convergence error
```

**解决方案**：
- 使用 `mvn dependency:tree` 分析依赖树
- 使用 `<exclusions>` 标签排除冲突的依赖
- 考虑使用 Maven 的依赖管理功能

#### 构建失败

**错误信息**：
```
BUILD FAILURE
```

**解决方案**：
- 仔细阅读错误消息，找出具体原因
- 确保所有测试通过
- 检查 pom.xml 中的配置是否正确
- 尝试使用 `-X` 参数获取更详细的调试信息：`mvn clean deploy -P release -X`

