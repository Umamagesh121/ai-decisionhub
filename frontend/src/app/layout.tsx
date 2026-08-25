import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "AI DecisionHub",
  description: "Predict → Decide → Execute → Verify → Learn → Improve",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  );
}
