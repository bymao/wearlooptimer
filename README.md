# wearlooptimer

[English](docs/README_EN.md)

一个安卓手表自动倒计时 APP。

## 已实现界面与交互
- 黑色背景，顶部约 2/3 区域显示白色倒计时时间（默认 `00:00`）
- 点击小时或分钟后，可通过滑动选择对应数值，右侧单位只显示一次作为刻度提示
- 秒钟区域为灰色，只用于显示不可选择
- 底部显示「开始 / 结束」两个个圆角方形按钮
- 倒计时结束后会触发 3 次震动提醒

## 编译

```bash
./gradlew :app:assembleDebug
```

生成的 APK 位于 `app/build/outputs/apk/debug/`。

## 自动编译

本项目已配置 GitHub Actions，每次推送到 `main` 分支或提交 Pull Request 时会自动编译，并将 APK 上传为构建产物。
