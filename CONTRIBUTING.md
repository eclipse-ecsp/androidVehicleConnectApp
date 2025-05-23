# How to contribute

Support and contributions from the open source community are essential for keeping
`eclipse-ecsp/androidVehicleConnectApp` up to date and always improving! There are a few guidelines that we need
contributors to follow to keep the project consistent, as well as allow us to keep
maintaining `eclipse-ecsp/androidVehicleConnectApp` in a reasonable amount of time.

ECSP project welcomes contributions and suggestions. Please follow the below steps to be able to contribute to ECSP,
- Create an [Eclipse Foundation account](https://accounts.eclipse.org/).
- Review and Agree to [Eclipse Contributor Agreement](https://www.eclipse.org/legal/eca/).
- More details about contribution can be found in [Eclipse Project Handbook - Contributors](https://www.eclipse.org/projects/handbook/#contributing-contributors) section.


Please note that this project is released with a [Contributor Code of Conduct][coc].

By participating in this project you agree to abide by its terms.

[coc]: ./CODE_OF_CONDUCT.md

## Creating an Issue

Before you create a new Issue:

1. Please make sure there is no [open issue](https://github.com/eclipse-ecsp/androidVehicleConnectApp/issues) yet.
2. If it is a bug report, include the steps to reproduce the issue and please create a reproducible test case.
3. If it is a feature request, please share the motivation for the new feature and how you would implement it.
4. Please include links to the corresponding GitHub documentation.

## Making Changes

- Create a topic branch from the main branch.
- Check for unnecessary whitespace / changes with `git diff --check` before committing.
- Keep git commit messages clear and appropriate. Ideally follow commit conventions described below.

## Submitting the Pull Request

- Push your changes to your topic branch on your fork of the repo.
- Submit a pull request from your topic branch to the [main](https://github.com/eclipse-ecsp/androidVehicleConnectApp) branch on the `eclipse-ecsp/androidVehicleConnectApp` repository.
- Be sure to tag any issues your pull request is taking care of / contributing to. \* Adding "Closes #123"
to a pull request description will auto close the issue once the pull request is merged in.


## Merging a PR and Shipping a release (maintainers only)

- A PR can only be merged into main branch by a maintainer if: CI is passing, approved by another maintainer and is up-to-date with the default branch.
- Ensure that the PR is tagged with related [issue](https://github.com/eclipse-ecsp/androidVehicleConnectApp/issues) it intends to resolve.
- Change log for all the PRs merged since the last release should be included in the release notes.
- Automatically generated release notes is configured for the repo and must be used while creating a new release tag.
