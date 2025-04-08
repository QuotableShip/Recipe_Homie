import os
import sys
from datetime import datetime
from openai import OpenAI

from File_Reformatter import reformat_recipe

client = OpenAI(api_key="insert_API_key_here")

def main():
    try:
        readFromJava = " ".join(sys.argv[1:])
        initial_format = readFromJava.replace('[', '').replace(']', '')
        ingredients = ', '.join([item.strip() for item in initial_format.split(',') if item.strip()])

        # Create a new thread
        thread = client.beta.threads.create()

        # Send a message to the assistant
        message = client.beta.threads.messages.create(
            thread_id=thread.id,
            role="user",
            content="create a recipe with:" + ingredients
        )

        # Poll the assistant for a response
        run = client.beta.threads.runs.create_and_poll(
            thread_id=thread.id,
            assistant_id="asst_NN9a8K6zwIHWolM90WYvMb7L"
        )

        if run.status == 'completed':
            # Retrieve the list of messages
            messages = client.beta.threads.messages.list(
                thread_id=thread.id
            )
            # Print the entire response to the console
            print("Retrieved messages:", messages)

            # Specify the directory to save the thread ID
            output_thread_directory = r'insert_directory_here'
            thread_file_path = os.path.join(output_thread_directory, 'thread_id.txt')

            # Ensure the output directory exists
            if not os.path.exists(output_thread_directory):
                try:
                    os.makedirs(output_thread_directory)
                except OSError as e:
                    print(f"Error creating directory {output_thread_directory}: {e}")
                    exit(1)

            try:
                with open(thread_file_path, 'w') as file:
                    file.write(str(thread.id))
                print(f"Thread ID saved to {thread_file_path}")
            except Exception as e:
                print(f"Error writing to file {thread_file_path}: {e}")
                exit(1)

            # Create a unique filename based on the current date and time
            current_time = datetime.now().strftime("%H%M%d%m%Y")
            filename = f"recipe{current_time}.txt"

            # Specify the path to save the file
            unformatted_recipe_directory = r'insert_directory_here'
            recipe_file_path = os.path.join(unformatted_recipe_directory, filename)

            # Ensure the output directory exists
            if not os.path.exists(unformatted_recipe_directory):
                try:
                    os.makedirs(unformatted_recipe_directory)
                except OSError as e:
                    print(f"Error creating directory {unformatted_recipe_directory}: {e}")
                    exit(1)

            # Save the full message to a .txt file
            try:
                with open(recipe_file_path, 'w') as file:
                    file.write(str(messages))
                print(f"Full message saved to {recipe_file_path}")
                status = 'successful'
            except Exception as e:
                print(f"Error writing to file {recipe_file_path}: {e}")
                exit(1)
        else:
            print(f"Run status: {run.status}")
            exit(1)

        if status == 'successful':
            reformat_recipe(recipe_file_path)
        print(status)

    except Exception as e:
        print(f"An error occurred: {e}")
        exit(1)

if __name__ == "__main__":
    main()

