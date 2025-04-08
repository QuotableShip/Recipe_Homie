
import re
import os

def reformat_recipe(file_path):
    # Read the content of the file
    try:
        with open(file_path, 'r') as file:
            content = file.read()

        formatted_directory = r'insert_directory_here'

        # Extract the recipe content including 'Title:' and 'Date Cooked:'
        match = re.search(r'(Title:.*?Date Cooked:)', content, re.DOTALL)
        if match:
            recipe_content = match.group(1).strip()
        else:
            print("Recipe content not found.")
            error = "generation_error"
            error_file_name = f"Recipe_Failed_To_Generate"
            error_file_path = os.path.join(formatted_directory, error_file_name)

            with open(error_file_path, 'w') as file:
                file.write(error)


        # Replace '\n' with actual new lines
        formatted_content = recipe_content.replace(r'\n', '\n')

        # Create a new file name based on the original file name
        directory, original_filename = os.path.split(file_path)
        new_filename = f"formatted_{original_filename}"
        formatted_directory = r'insert_directory_here'
        new_file_path = os.path.join(formatted_directory, new_filename)

        if not os.path.exists(formatted_directory):
            try:
                os.makedirs(formatted_directory)
            except OSError as e:
                print(f"Error creating directory {formatted_directory}: {e}")
                return


        # Write the formatted content to a new file
        with open(new_file_path, 'w') as file:
            file.write(formatted_content)


        print(f"Formatted recipe saved to {new_file_path}")
    except Exception as e:
        print(f"An error occurred while reformatting the recipe: {e}")
