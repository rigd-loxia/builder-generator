# CONTRIBUTING

When contributing to this repository, please first discuss the change you wish to make via [issues](https://github.com/rigd-loxia/builder-generator/issues).

Please note if you are working on a certain issue then make sure to stay active with development.

## Git Commit, Branch, and PR Naming Conventions

When you are working with git, please be sure to follow the conventions below on your pull requests, branches, and commits:

```text
PR Title: #[ISSUE ID] Title of the PR
PR Description: [OPTIONAL EXTRA INFORMATION] + closes #[ISSUE ID]
Branch: [ISSUE ID]-title-of-the-pr (shorter)
Commit: #[ISSUE ID] what was done
```

Examples:

```text
PR Title: #2 Add builder inheritence
PR Description: closes #2
Branch: 2-add-builder-inheritence
Commit: #2 add builder inheritence
```

## Installation

To get started with Builder-Generator locally, follow these steps

1. Fork the repo
2. Clone your fork
3. Navigate to the project directory
4. Build with mvn

   ```sh
   mvn verify
   ```

## Working on New Features

If you want to work on a new feature, follow these steps.

1. Create or choose an issue
2. Fork the repo
3. Clone your fork
4. Checkout a new branch
5. Do your work
6. Commit
7. Push your branch to your fork
8. Go into github UI and create a PR from your fork & branch.
