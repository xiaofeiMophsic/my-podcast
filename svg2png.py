import re
import subprocess
import os
import sys
from pathlib import Path

def batch_optimize_and_convert(input_folder, output_width=512):
    # 1. 检查文件夹是否存在
    folder_path = Path(input_folder)
    if not folder_path.is_dir():
        print(f"错误: 找不到文件夹 '{input_folder}'")
        return

    # 2. 创建输出文件夹 (在原文件夹下新建 png_output)
    output_dir = folder_path / "png_output"
    output_dir.mkdir(exist_ok=True)

    # 3. 寻找所有 .svg 文件
    svg_files = list(folder_path.glob("*.svg"))
    if not svg_files:
        print(f"文件夹中没有发现 .svg 文件。")
        return

    print(f"找到 {len(svg_files)} 个文件，准备开始转换...")

    for svg_file in svg_files:
        try:
            # 读取内容
            content = svg_file.read_text(encoding='utf-8')

            # 清洗 CSS 变量: var(--name, fallback) -> fallback
            cleaned_content = re.sub(r'var\(--[^,]+,\s*([^)]+)\)', r'\1', content)

            # 临时保存清洗后的文件
            temp_svg = folder_path / f"temp_{svg_file.name}"
            temp_svg.write_text(cleaned_content, encoding='utf-8')

            # 设定输出文件名
            output_png = output_dir / f"{svg_file.stem}.png"

            # 调用 resvg 转换
            # -w 指定宽度，图标会自动填满并保持比例
            subprocess.run([
                "resvg", 
                str(temp_svg), 
                str(output_png), 
                "-w", str(output_width)
            ], check=True, capture_output=True)

            print(f"  [成功] {svg_file.name} -> {output_png.name}")

            # 删除临时文件
            temp_svg.unlink()

        except Exception as e:
            print(f"  [失败] {svg_file.name}: {e}")

    print(f"\n全部处理完成！图片已保存在: {output_dir}")

if __name__ == "__main__":
    # 使用方式: python batch_svg2png.py [文件夹路径] [宽度]
    # 示例: python batch_svg2png.py ./my_icons 1024
    
    dir_input = sys.argv[1] if len(sys.argv) > 1 else "."
    width_input = sys.argv[2] if len(sys.argv) > 2 else 512
    
    batch_optimize_and_convert(dir_input, width_input)