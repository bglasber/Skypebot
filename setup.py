from distutils.core import setup,Command
from setuptools.command.test import test as TestCommand


class PyTest(TestCommand):
    def finalize_options(self):
        TestCommand.finalize_options(self)
        self.test_args = []
        self.test_suite = True

    def run_tests(self):
        import pytest
        pytest.main(self.test_args)

setup(name='Skypebot',
      version='0.1',
      url="http://bglasber.github.com",
      author="Brad Glasbergen",
      author_email="bglasber@gmail.com",
      tests_require=["pytest"],
      cmdclass = {"test": PyTest},
      requires=['Skype4Py', 'feedparser'],
)
