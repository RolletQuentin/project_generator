import os
import inquirer
from lxml import etree as ET
import json


def add_dependencies_to_pom(pom_path, dependencies_path):
    tree = ET.parse(pom_path)
    root = tree.getroot()
    ns = {'mvn': 'http://maven.apache.org/POM/4.0.0'}

    # Get the dependencies tag
    dependencies_tag = root.find('mvn:dependencies', ns)

    dependencies = ET.parse(dependencies_path).getroot()
    for dependency in dependencies:
        dependencies_tag.append(dependency)

    tree.write(pom_path, encoding='utf-8',
               xml_declaration=True, pretty_print=True)


def add_option_to_project(project_dir, project_name, option):
    # Append option-application.properties to application.properties
    if os.path.exists(f'quarkus/options/{option}/application.properties'):
        with open(os.path.join(project_dir, project_name, 'src/main/resources/application.properties'), 'a') as f:
            with open(f'quarkus/options/{option}/application.properties', 'r') as option_f:
                f.write(option_f.read())

    # Append option-dependencies to pom.xml
    if os.path.exists(f'quarkus/options/{option}/dependencies.xml'):
        add_dependencies_to_pom(os.path.join(
            project_dir, project_name, 'pom.xml'), f'quarkus/options/{option}/dependencies.xml')

    # Copy quarkus/options/{option}/{option} folder to src/main/java/org/acme
    if os.path.exists(f'quarkus/options/{option}/{option}'):
        os.system(f"cp -r quarkus/options/{option}/{option} {
            os.path.join(project_dir, project_name, 'src/main/java/org/acme')}")

    # region:    --- Particular Options

    if option == 'keycloak':
        # Append InitialializeDatabase.java to src/main/java/org/acme
        os.system(f"cp quarkus/options/{option}/InitializeDatabase.java {
                  os.path.join(project_dir, project_name, 'src/main/java/org/acme')}")

        # Copy quarkus/options/{option}/user folder to src/main/java/org/acme
        os.system(f"cp -r quarkus/options/{option}/user {
            os.path.join(project_dir, project_name, 'src/main/java/org/acme')}")

    # endregion: --- Particular Options


def generate_backend(backend, project_dir, project_name, options):

    if backend == 'Quarkus':
        os.system(f"git clone https://github.com/RolletQuentin/quarkus_template.git {
                  os.path.join(project_dir, project_name, "backend")}")

        # Add options
        for option in options:
            add_option_to_project(
                project_dir, project_name + "/backend", option)


def add_option_to_vue_project(project_dir, project_name, option):
    # Update dependencies
    if os.path.exists(f'vue/options/{option}/dependencies.json'):
        with open(f'vue/options/{option}/dependencies.json', 'r') as f:
            dependencies = json.load(f)
            for dependency in dependencies:
                # npm install in the frontend folder
                os.system(
                    f"cd {os.path.join(project_dir, project_name)} && npm install {dependency}")

    # Update src/main.ts
    if os.path.exists(f'vue/options/{option}/main.ts'):
        with open(os.path.join(project_dir, project_name, 'src/main.ts'), 'a') as f:
            with open(f'vue/options/{option}/main.ts', 'r') as option_f:
                f.write(option_f.read())

    # Merge vue/options/{option}/src folder with src folder
    if os.path.exists(f'vue/options/{option}/src'):
        os.system(f"cp -r vue/options/{option}/src/* {
            os.path.join(project_dir, project_name, 'src')}")


def generate_frontend(frontend, project_dir, project_name, options):
    if frontend == 'Vue':
        os.system(f"git clone https://github.com/RolletQuentin/vue_template.git {
                  os.path.join(project_dir, project_name, "frontend")}")

        # Add options
        for option in options:
            add_option_to_vue_project(
                project_dir, project_name + "/frontend", option)


def main():
    # Prompt for Project Name and Directory
    questions = [
        inquirer.Text('project_name',
                      message="What's the name of your project?"),
        inquirer.Path('project_dir', message="Where do you want to create the project?",
                      path_type=inquirer.Path.DIRECTORY)
    ]
    answers = inquirer.prompt(questions)
    project_name = answers['project_name']
    project_dir = answers['project_dir']

    # region:    --- Backend

    # Prompt for Backend Choice
    backend_question = [
        inquirer.List('backend', message="Choose your backend",
                      choices=['Quarkus'])
    ]
    backend_answer = inquirer.prompt(backend_question)
    backend = backend_answer['backend']

    # Prompt for Additional Options
    options_question = [
        inquirer.Checkbox('options', message="Select additional options for backend", choices=[
                          'keycloak', 's3'])
    ]
    options_answer = inquirer.prompt(options_question)
    options = options_answer['options']

    # endregion: --- Backend

    # region:    --- Frontend

    # Prompt for Frontend Choice
    frontend_question = [
        inquirer.List('frontend', message="Choose your frontend",
                      choices=['Vue'])
    ]

    frontend_answer = inquirer.prompt(frontend_question)
    frontend = frontend_answer['frontend']

    # Prompt for Additional Options
    options_question = [
        inquirer.Checkbox('options', message="Select additional options for frontend", choices=[
                          'keycloak'])
    ]
    options_answer = inquirer.prompt(options_question)
    options = options_answer['options']

    # Clone the templates
    generate_backend(backend, project_dir, project_name, options)
    generate_frontend(frontend, project_dir, project_name, options)

    # endregion: --- Frontend


if __name__ == '__main__':
    main()
