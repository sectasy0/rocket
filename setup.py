import pathlib
from setuptools import setup, find_packages

HERE = pathlib.Path(__file__).parent

setup(
    name='rocket-client',
    version='0.1.0',
    description='rocket protocol client',
    long_description=(HERE / "README.md").read_text(),
    long_description_content_type="text/markdown",
    author='sectasy',
    keywords=['rocket', 'rcon alternative'],
    license='MIT License',
    author_email='sectasy0@gmail.com',
    url='https://github.com/sectasy0/rocket',
    packages=find_packages(),
    install_requires=['cryptography==39.0.1'],
)