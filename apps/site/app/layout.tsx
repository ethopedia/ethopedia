// Import styles of packages that you've installed.
// All packages except `@mantine/hooks` require styles imports
import '@mantine/core/styles.css';

import { MantineProvider, ColorSchemeScript } from '@mantine/core';
import '@fontsource-variable/inter';
import Head from "next/head";

export const metadata = {
  title: 'Ethopedia',
  description: 'A search engine for EthosLab',
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en">
      <head>
        <ColorSchemeScript />
        <link rel="shortcut icon" href="/favicon.svg" />
        <meta
          name="viewport"
          content="minimum-scale=1, initial-scale=1, width=device-width, user-scalable=no"
        />
      </head>
      <body>
        <MantineProvider
          theme={{
            fontFamily: 'Inter Variable, sans-serif',
            headings: { fontFamily: 'Inter Variable, sans-serif' },
          }}
        >
          {children}
        </MantineProvider>
      </body>
    </html>
  );
}
