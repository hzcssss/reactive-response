#!/bin/bash

# 查找所有Java文件
find src -name "*.java" -type f | while read file; do
  # 检查是否包含旧的包名
  if grep -q "io.github.reactive.response" "$file"; then
    echo "文件 $file 中包含旧的包名引用"
    grep -n "io.github.reactive.response" "$file"
  fi
done