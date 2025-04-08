import sys
from openai import OpenAI
import os
from datetime import datetime

from File_Reformatter import reformat_recipe

client = OpenAI(api_key="insert_API_key_here")

def main():
    try:
        readfromjava = " ".join(sys.argv[1:])

        #readfromjava = "double the calories"

        thread_file_path = r"insert_directory_here"

        # Retrieve Current thread
        try:
            with open(thread_file_path, 'r') as file:
                thread_id = file.read()
        except Exception as e:
            print(f"Error reading file {thread_file_path}: {e}")
            exit(1)

        # Send a message to the assistant
        message = client.beta.threads.messages.create(
            thread_id = thread_id,
            role="user",
            content=readfromjava,
        )

        # Poll the assistant for a response
        run = client.beta.threads.runs.create_and_poll(
            thread_id = thread_id,
            assistant_id="asst_NN9a8K6zwIHWolM90WYvMb7L",
        )

        if run.status == 'completed':
            # Retrieve the list of messages
            messages = client.beta.threads.messages.list(
                thread_id = thread_id,
            )

            # Print the entire response to the console
            print("Retrieved messages:", messages)

            # Create a unique filename based on the current date and time
            current_time = datetime.now().strftime("%H%M%d%m%Y")
            filename = f"recipe{current_time}.txt"

            # Specify the path to save the file
            output_directory = r'insert_directory_here'
            file_path = os.path.join(output_directory, filename)
            status = 'successful'

            # Ensure the output directory exists
            if not os.path.exists(output_directory):
                try:
                    os.makedirs(output_directory)
                except OSError as e:
                    print(f"Error creating directory {output_directory}: {e}")
                    exit(1)

            # Save the full message to a .txt file
            try:
                with open(file_path, 'w') as file:
                    file.write(str(messages))
                print(f"Full message saved to {file_path}")
            except Exception as e:
                print(f"Error writing to file {file_path}: {e}")
                exit(1)
        else:
            print("run status:", run.status)
            exit(1)

        reformat_recipe(file_path)
        print(status)

    except Exception as e:
        print(f"An error occurred: {e}")
        exit(1)

if __name__ == "__main__":
    main()
