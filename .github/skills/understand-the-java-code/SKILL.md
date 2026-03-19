---
name: understand-the-java-code
description: '用于快速接手 Java 项目：阅读 README，输出项目价值分析，给出 Windows 11 PowerShell 7 下可执行的编译/运行/测试步骤，并把问答保存到 dialogs/understand-the-java-code-<timestamp>.md。适用于 onboarding、新人接手、项目理解、运行验证、测试验证。'
argument-hint: '可选参数：timestamp（例如 2026-03-06--19-39）'
user-invocable: true
---

# Understand The Java Code

## 适用场景
- 你刚接手一个 Java 项目，需要在最短时间内理解价值与边界。
- 你需要一份可复现的本地运行指南，而不是抽象说明。
- 你希望把分析结果沉淀为 `dialogs/understand-the-java-code-<timestamp>.md` 便于后续查阅。

## 输入约定
- 必读文件：项目根目录 `README.md`。
- 输出文件名：`understand-the-java-code-<timestamp>.md`。
- `<timestamp>` 格式：`YYYY-MM-DD--HH-mm`（示例：`2026-03-06--19-39`）。

## 标准流程
1. 读取 `README.md`，提取：
- 项目定位、主要功能、核心的特性。
- 与同类方案相比的优势。
- 已知限制、前置条件、不适用场景。

2. 生成问题 1 的回答（项目价值分析）：
- 主要功能
- 特点
- 优势
- 劣势
- 适用场景

3. 生成问题 2 的回答（Windows 11 PowerShell 7 实操）：
- 明确环境要求（JDK、Maven/Gradle、PATH 验证命令）。
- 给出从项目根目录开始的逐步命令，确保可直接复制执行。
- 若 README 缺少“可见结果”的运行样例：
- 在合理目录创建一个最小可运行示例应用（例如 `examples/windows11-demo/`）。
- 示例应实际调用当前项目能力，并说明输入、输出与预期结果。
- 输出“验证成功标准”（例如控制台输出、生成文件、退出码）。

4. 生成问题 3 的回答（自动化测试）：
- 给出运行全部测试命令。
- 给出运行单测/指定模块的常见命令（如可行）。
- 说明测试报告位置与如何判定通过/失败。

5. 写入结果文档：
- 路径：`dialogs/understand-the-java-code-<timestamp>.md`
- 格式：Markdown
- 结构必须包含：
  - 标题与时间戳
  - 问题 1 回答
  - 问题 2 回答
  - 问题 3 回答
  - 失败排查（常见错误与修复）

## 分支决策规则
- 若 README 的构建工具不明确：
- 优先检测 `pom.xml`（Maven）或 `build.gradle`（Gradle），并据此生成命令。

- 若用户环境缺少构建工具（例如 `mvn` 不存在）：
- 先给出验证命令（`mvn -v` / `gradle -v`），再给出安装与 PATH 配置步骤。
- 给出“安装后复验”命令。

- 若项目是库而非可执行应用：
- 必须提供一个“调用库能力”的示例应用，并说明如何编译运行该示例。

- 若命令可能受平台差异影响：
- 默认给出 Windows 11 PowerShell 7 命令，避免混入 bash 专用语法。

## 输出质量标准（完成检查）
- 可执行性：命令应能按顺序运行，不跳步。
- 可验证性：每个关键步骤都有预期输出或结果文件。
- 可诊断性：至少包含 3 条常见失败原因与修复建议。
- 可复用性：输出文档命名、目录、结构固定一致。
- 可读性：使用清晰标题、代码块、命令与说明分离。

## 建议输出骨架
```markdown
# understand-the-java-code - <timestamp>

## 1）项目的主要功能、特点、优势、劣势和适用场景

## 2）在 Windows 11 PowerShell 7 中编译并运行项目
### 2.1 环境准备
### 2.2 编译步骤
### 2.3 运行示例应用
### 2.4 结果验证

## 3）运行自动化测试
### 3.1 全量测试
### 3.2 定向测试（可选）
### 3.3 测试报告位置与判定标准

## 常见问题排查
```

## 使用示例
- `/understand-the-java-code`
- `/understand-the-java-code timestamp=2026-03-19--10-35`
