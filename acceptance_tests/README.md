# Puppet Wrapper Acceptance Tests

Folder for acceptance tests of the Puppet Wrapper.

## How to Run the Acceptance Tests

### Prerequisites:

- Python 2.7 or newer

- pip installed (http://docs.python-guide.org/en/latest/starting/install/linux/)

- virtualenv installed (pip install virtalenv)

- Git installed (yum install git-core / apt-get install git)

### Environment preparation:

- Create a virtual environment somewhere, e.g. in ~/venv (virtualenv ~/venv)

- Activate the virtual environment (source ~/venv/bin/activate)

- Change to the test/acceptance folder of the project

- Install the requirements for the acceptance tests in the virtual environment (pip2.7 install -r requirements.txt --allow-all-external).

### Tests execution:

- Change to the fiware-sdc/puppet-wrapper/acceptance_tests folder of the project if not already on it

- Run lettuce_tools with appropriate params (see available ones with the -h option)
