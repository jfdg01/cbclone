import os

def extract_code_from_file(file_path):
    with open(file_path, 'r', encoding='utf-8') as file:
        lines = file.readlines()
        
    code_lines = []
    for line in lines:
        if not (line.startswith('import') or line.startswith('package')):
            code_lines.append(line)
    
    return ''.join(code_lines)

def save_code_to_file(code, output_file):
    with open(output_file, 'w', encoding='utf-8') as file:
        file.write(code)

def main():
    # Get the directory where the script is located
    script_directory = os.path.dirname(os.path.abspath(__file__))
    output_file = os.path.join(script_directory, 'code.txt')
    
    all_code = ""
    
    # Walk through the directory where the script is located
    for root, _, files in os.walk(script_directory):
        for file in files:
            if file.endswith('.java'):
                file_path = os.path.join(root, file)
                all_code += extract_code_from_file(file_path)
    
    save_code_to_file(all_code, output_file)

if __name__ == "__main__":
    main()
