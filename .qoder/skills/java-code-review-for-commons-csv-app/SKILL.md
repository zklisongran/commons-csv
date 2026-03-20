---
name: java-code-review-for-commons-csv-app
description: 专门用于Apache Commons CSV项目的Java代码审查技能，基于12类常见bug清单进行系统性代码质量检查。使用此技能来审查CSV处理相关的Java代码，识别潜在的空指针异常、数组越界、资源泄露等关键问题。
---

# Apache Commons CSV Java代码审查技能

## 快速开始

当审查commons-csv项目的Java代码时，按照以下系统化方法进行：

1. **预审查准备** - 了解变更上下文和影响范围
2. **12类常见bug专项检查** - 逐项对照bug清单
3. **代码质量评估** - 检查最佳实践遵循情况
4. **测试覆盖验证** - 确认关键路径有充分测试

## 12类常见Bug专项检查清单

### 1. 空指针异常检查 🔍
重点关注：
- `parser.getHeaderMapRaw()` 返回值使用前的null检查
- `CSVRecord.values` 数组访问前的null验证
- `ExtendedBufferedReader.lastChar` 的UNDEFINED状态处理

### 2. 数组越界异常检查 ⚠️
检查点：
- `CSVRecord.get(int index)` 方法的边界验证
- 循环遍历记录时的索引范围检查
- 多字节字符缓冲区索引计算

### 3. 资源泄露检查 🛡️
验证：
- `CSVParser.close()` 方法的正确调用
- try-with-resources语句的使用
- 异常处理中的资源清理

### 4. 编码问题检查 📝
关注：
- `ExtendedBufferedReader` 中的字符编码处理
- UTF-8/UTF-16代理对的正确处理
- BOM标记的识别和处理

### 5. 并发安全检查 🔒
检查：
- `CSVPrinter` 中`ReentrantLock`的正确使用
- 共享StringBuilder的线程安全性
- 静态变量的状态竞争问题

### 6. 内存泄漏检查 💾
验证：
- 大文件处理时`recordList`的及时清理
- `Token`对象复用时的内容重置
- 缓冲区大小的合理控制

### 7. 格式解析错误检查 📊
检查：
- 分隔符、引号、转义字符的配置一致性
- 多字符分隔符的处理逻辑
- 注释行与数据行的正确区分

### 8. 性能问题检查 ⚡
关注：
- 字符串拼接是否使用StringBuilder
- 正则表达式的使用频率和复杂度
- 对象创建的必要性和垃圾回收压力

### 9. 边界条件处理检查 🚧
验证：
- 空文件和纯空白文件的处理
- 单行CSV且以分隔符结尾的情况
- 特殊Unicode字符的兼容性

### 10. 状态管理错误检查 🔄
检查：
- `CSVParser.recordNumber` 计数的准确性
- `Token.type` 状态转换的完整性
- 位置跟踪的精确性

### 11. 类型转换异常检查 🔧
验证：
- Object类型参数的安全转换
- 泛型类型擦除的处理
- 枚举与字符串转换的健壮性

### 12. 数据一致性检查 ✅
检查：
- 引号内转义字符的正确处理
- 行尾分隔符(CRLF vs LF)的一致性
- 数值类型数据的精度保持

## 代码审查反馈模板

使用以下格式提供审查反馈：

```
## 代码审查报告 - [文件名]

### 严重问题 ❌ (必须修复)
- [具体问题描述] - [修复建议]

### 建议改进 ⚠️ (推荐优化)
- [改进点] - [优化方案]

### 良好实践 ✅ (值得肯定)
- [做得好的地方]

### 测试建议 🧪
- [需要增加的测试场景]
```

## 审查工作流程

```
审查进度:
- [ ] 1. 变更影响分析完成
- [ ] 2. 12类bug检查完成
- [ ] 3. 代码质量评估完成
- [ ] 4. 测试覆盖验证完成
- [ ] 5. 反馈报告生成完成
```

## 参考资源

- [Commons CSV Bug清单](../../dialogs/common-bugs-in-commons-csv-20260320.md)
- [项目测试用例](../../src/test/java/org/apache/commons/csv/)
- [官方API文档](https://commons.apache.org/proper/commons-csv/apidocs/)

## 使用时机

在以下情况下自动应用此技能：
- 审查commons-csv项目的pull request
- 分析CSV处理相关的Java代码变更
- 进行代码质量评估和bug排查
- 设计新的CSV处理功能时的风险评估