# Ethopedia

## Navigating the Monorepo

```
monorepo/
├── infra/
│   └── aws - Typescript AWS cdk project
├── platform/
│   └── apps/
│       └── api - Kotlin GraphQL api. Hosted at api.ethopedia.org
└── apps/
    └── site - Next.js web app. Hosted at www.ethopedia.org
```

## Running the project

To run everything locally run:

```shell
$ pnpm install
$ pnpm run dev
```

The graphql api will run on port 8080. The next.js app will be on port 3000.
