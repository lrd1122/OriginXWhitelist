# 🛡️ OriginX Whitelist

高性能、多功能的Minecraft白名单插件，支持基于玩家名、IP和UUID的白名单管理。

## ✨ 主要特性

- 🔒 支持三种白名单模式：玩家名、IP地址、UUID
- 🌍 完整的多语言支持 (支持中文和英文)
- 🛡️ 内置反压测系统，保护服务器安全
- 📊 GUI界面管理，操作便捷
- ⚡ 高性能异步处理，不影响服务器性能

## 📝 命令列表

- `/owhitelist` - 打开图形管理界面
- `/owhitelist add <类型> <值>` - 添加白名单
- `/owhitelist remove <类型> <值>` - 移除白名单
- `/owhitelist list [类型]` - 查看白名单列表

## 🔧 权限节点

- `originxwhitelist.admin` - 管理员权限，允许使用所有命令

## 🎯 GUI界面功能

### 主菜单
- 语言设置
- 防压测配置
- IP白名单开关
- UUID白名单开关
- 白名单列表管理

### 白名单管理
- 添加白名单（支持玩家名/IP/UUID）
- 查看现有白名单
- 一键删除白名单

### 安全设置
- 配置最大尝试次数
- 设置封禁时长
- 清除临时封禁

## 📋 配置文件

```yaml
language: "zh_CN"  # 支持 zh_CN, en_US
security:
  max-login-attempts: 5  # 最大尝试次数
  block-duration: 3600   # 封禁时长(秒)
whitelist:
  enable-ip: true   # 启用IP白名单
  enable-uuid: true # 启用UUID白名单
```

## 🌐 多语言支持

- 完整的中英文支持
- 自动检测系统语言
- 可在游戏内随时切换
- 支持自定义语言文件

## 🚀 反压测特性

- 登录尝试次数限制
- 自动临时封禁
- 可配置封禁时长
- GUI一键解封

## 📦 下载与安装

1. 下载插件jar文件
2. 放入plugins文件夹
3. 重启服务器
4. 使用`/owhitelist`进行配置

## 🔍 注意事项

- 请确保给予管理员`originxwhitelist.admin`权限
- 首次使用建议查看配置文件进行个性化设置
- 支持热重载语言文件

## 💡 建议与反馈

如有任何问题或建议，欢迎：
- 在Github提交Issue
- 加入我们的QQ讨论群 676396354
- 通过帖子评论反馈

---