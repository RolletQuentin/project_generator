import os
import inquirer
from lxml import etree as ET


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
    with open(os.path.join(project_dir, project_name, 'src/main/resources/application.properties'), 'a') as f:
        with open(f'quarkus/options/{option}/application.properties', 'r') as option_f:
            f.write(option_f.read())

    # Append option-dependencies to pom.xml
    add_dependencies_to_pom(os.path.join(
        project_dir, project_name, 'pom.xml'), f'quarkus/options/{option}/dependencies.xml')

    # Copy quarkus/options/{option}/{option} folder to src/main/java
    os.system(f"cp -r quarkus/options/{option}/{option} {
              os.path.join(project_dir, project_name, 'src/main/java/org/acme')}")


def generate_backend(backend, project_dir, project_name, options):
    if backend == 'Quarkus':
        os.system(f"git clone https://github.com/RolletQuentin/quarkus_keycloak_template.git {
                  os.path.join(project_dir, project_name)}")

        # Add options
        for option in options:
            add_option_to_project(project_dir, project_name, option)


def main():
    # Step 1: Prompt for Project Name and Directory
    questions = [
        inquirer.Text('project_name',
                      message="What's the name of your project?"),
        inquirer.Path('project_dir', message="Where do you want to create the project?",
                      path_type=inquirer.Path.DIRECTORY)
    ]
    answers = inquirer.prompt(questions)
    project_name = answers['project_name']
    project_dir = answers['project_dir']

    # Step 2: Prompt for Backend Choice
    backend_question = [
        inquirer.List('backend', message="Choose your backend",
                      choices=['Quarkus'])
    ]
    backend_answer = inquirer.prompt(backend_question)
    backend = backend_answer['backend']

    # Step 3: Prompt for Additional Options
    options_question = [
        inquirer.Checkbox('options', message="Select additional options", choices=[
                          's3'])
    ]
    options_answer = inquirer.prompt(options_question)
    options = options_answer['options']

    # Step 4: Clone the Backend Template
    generate_backend(backend, project_dir, project_name, options)


if __name__ == '__main__':
    main()
